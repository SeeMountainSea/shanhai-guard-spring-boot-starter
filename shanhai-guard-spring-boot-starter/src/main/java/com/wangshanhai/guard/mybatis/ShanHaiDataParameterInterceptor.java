package com.wangshanhai.guard.mybatis;

import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.wangshanhai.guard.annotation.FieldDataGuard;
import com.wangshanhai.guard.annotation.ShanHaiDataGuard;
import com.wangshanhai.guard.config.DataGuardConfig;
import com.wangshanhai.guard.dataplug.DataExecModel;
import com.wangshanhai.guard.service.ShanHaiDataGuardService;
import com.wangshanhai.guard.utils.Logger;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * 数据防护处理
 * @author Shmily
 */
@Component
@Intercepts({
        @Signature(type = ParameterHandler.class, method = "setParameters", args = PreparedStatement.class),
})
public class ShanHaiDataParameterInterceptor implements Interceptor {

    private ShanHaiDataGuardService dataGuardService;
    private DataGuardConfig shanhaiDataGuardConfig;

    public ShanHaiDataParameterInterceptor(ShanHaiDataGuardService dataGuardService,DataGuardConfig shanhaiDataGuardConfig) {
        this.dataGuardService = dataGuardService;
        this.shanhaiDataGuardConfig=shanhaiDataGuardConfig;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args=invocation.getArgs();
        long start=System.currentTimeMillis();
        //@Signature 指定了 type= parameterHandler 后，这里的 invocation.getTarget() 便是parameterHandler
        //若指定ResultSetHandler ，这里则能强转为ResultSetHandler
        MybatisParameterHandler parameterHandler = (MybatisParameterHandler) invocation.getTarget();
        // 获取参数对像，即 mapper 中 paramsType 的实例
        Field parameterField = parameterHandler.getClass().getDeclaredField("parameterObject");
        parameterField.setAccessible(true);

        Field sqlCommandTypeField = parameterHandler.getClass().getDeclaredField("sqlCommandType");
        sqlCommandTypeField.setAccessible(true);
        //取出实例
        Object parameterObject = parameterField.get(parameterHandler);
        SqlCommandType sqlCommandType= (SqlCommandType)sqlCommandTypeField.get(parameterHandler);
        //对类字段进行加密
        if(!Objects.isNull(parameterObject)){
            Class<?> parameterObjectClass = parameterObject.getClass();
            //Mybatis数据处理(Mybatis-Plus的QueryWrapper也会走这个，但是解析不了，因此不支持该写法做参数设置)
            if(parameterObject instanceof Map){
                Map<String,Object> paramsWait=(Map)parameterObject;
                Map<String,Object> params=new HashMap<>();
                ArrayList<String> paramsWaitList=new ArrayList<String>();
                //对Mapper内的参数进行去重，否则会多次进行数据操作
                for(String pk:paramsWait.keySet()){
                    Object pkObj=paramsWait.get(pk);
                    if(pkObj==null){
                        params.put(pk,pkObj);
                        continue;
                    }
                    if(!paramsWaitList.contains(pkObj.toString())){
                        params.put(pk,pkObj);
                        paramsWaitList.add(pkObj.toString());
                    }
                }
                 for(String pk:params.keySet()){
                     Object pkObj=params.get(pk);
                     if(pkObj!=null){
                         Class<?> pkObjClass = pkObj.getClass();
                         ShanHaiDataGuard shanHaiDataGuard = AnnotationUtils.findAnnotation(pkObjClass, ShanHaiDataGuard.class);
                         if(shanHaiDataGuard!=null){
                             //对类字段进行加密
                             //取出当前当前类所有字段，传入加密方法
                             Field[] declaredFields = pkObjClass.getDeclaredFields();
                             dataEscape(declaredFields, pkObj,sqlCommandType,shanhaiDataGuardConfig);
                         }
                         if(shanhaiDataGuardConfig.isTraceLog()){
                             if(pkObjClass.getSimpleName().contains("Wrapper")){
                                 Logger.debug("[ShanhaiDataGuard-setParameters-plus]-type:{} is not support",pkObjClass.getSimpleName());
                             }
                         }
                     }
                     ((Map)parameterObject).put(pk,pkObj);
                 }
            }else{//Mybatis Plus数据处理
                ShanHaiDataGuard shanHaiDataGuard = AnnotationUtils.findAnnotation(parameterObjectClass, ShanHaiDataGuard.class);
                if(shanHaiDataGuard!=null){
                    //对类字段进行加密
                    //取出当前当前类所有字段，传入加密方法
                    Field[] declaredFields = parameterObjectClass.getDeclaredFields();
                    dataEscape(declaredFields, parameterObject,sqlCommandType,shanhaiDataGuardConfig);
                }
            }

        }
        if(shanhaiDataGuardConfig.isSlowFilter()){
            Field boundSqlField = parameterHandler.getClass().getDeclaredField("boundSql");
            boundSqlField.setAccessible(true);
            BoundSql boundSql = (BoundSql) boundSqlField.get(parameterHandler);
            Field mappedStatementField = parameterHandler.getClass().getDeclaredField("mappedStatement");
            mappedStatementField.setAccessible(true);
            MappedStatement mappedStatement = (MappedStatement) mappedStatementField.get(parameterHandler);
            long time=System.currentTimeMillis()-start;
            if(time>shanhaiDataGuardConfig.getSlowTime()){
                Logger.warn("[ShanhaiDataGuard-setParameters-execTime]-time(ms):{},mapper:{},sql:{}",time,mappedStatement.getId(),boundSql.getSql());
            }
            if(shanhaiDataGuardConfig.isTraceLog()){
                Logger.info("[ShanhaiDataGuard-setParameters-execTime]-time(ms):{},mapper:{},sql:{}",time,mappedStatement.getId(),boundSql.getSql());
            }
        }
        return invocation.proceed();
    }

    /**
     * 对指定对象的指定字段进行数据处理
     * @param declaredFields 字段
     * @param paramsObject 对象
     * @param sqlCommandType 执行模式   INSERT|UPDATE|DELETE|SELECT
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public <T> T dataEscape (Field[] declaredFields, T paramsObject, SqlCommandType sqlCommandType,DataGuardConfig shanhaiDataGuardConfig) throws IllegalAccessException {
        String execModel=sqlCommandType.name();
        //取出所有被EncryptTransaction注解的字段
        for (Field field : declaredFields) {
            FieldDataGuard encryptTransaction = field.getAnnotation(FieldDataGuard.class);
            if (!Objects.isNull(encryptTransaction)) {
                field.setAccessible(true);
                Object object = field.get(paramsObject);
                //只处理String类型字段
                if (object instanceof String) {
                    String value = (String) object;
                    try {
                        String tmpText=value;
                        ShanHaiTmpData shanHaiTmpData=new ShanHaiTmpData();
                        shanHaiTmpData.setExecModel(execModel);
                        shanHaiTmpData.setRuleId(encryptTransaction.ruleId());
                        shanHaiTmpData.setTargetClass(paramsObject.getClass().getName());
                        shanHaiTmpData.setTargetField(field.getName());
                        //参数脱敏仅对新增|更新操作有效
                        if(encryptTransaction.hyposensit()){
                            boolean canExec=false;
                            if(execModel.equals(DataExecModel.INSERT)){
                                if(encryptTransaction.hyposensitExecModel().equals(DataExecModel.INSERT)
                                        ||encryptTransaction.hyposensitExecModel().equals(DataExecModel.INSERTANDUPDATE)){
                                    canExec=true;
                                }
                            }
                            if(execModel.equals(DataExecModel.UPDATE)){
                                if(encryptTransaction.hyposensitExecModel().equals(DataExecModel.UPDATE)
                                        ||encryptTransaction.hyposensitExecModel().equals(DataExecModel.INSERTANDUPDATE)){
                                    canExec=true;
                                }
                            }
                            if(canExec){
                                shanHaiTmpData.setSourceValue(tmpText);
                                shanHaiTmpData.setHyposensitMethod(encryptTransaction.hyposensitMethod());
                                tmpText=dataGuardService.hyposensit(shanHaiTmpData);
                                field.set(paramsObject,tmpText);
                                if(shanhaiDataGuardConfig.isTraceLog()){
                                    Logger.info("[ShanhaiDataGuard-setParameters-Hyposensit]-info:{},result:{}",shanHaiTmpData,tmpText);
                                }
                            }
                        }
                        //参数加密仅对新增|更新|查询操作有效
                        if(encryptTransaction.encrypt()){
                            boolean canExec=false;
                            if(execModel.equals(DataExecModel.INSERT)){
                                if(encryptTransaction.encryptExecModel().equals(DataExecModel.INSERT)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.INSERTANDUPDATE)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.ALL)){
                                    canExec=true;
                                }
                            }
                            if(execModel.equals(DataExecModel.UPDATE)){
                                if(encryptTransaction.encryptExecModel().equals(DataExecModel.UPDATE)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.INSERTANDUPDATE)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.ALL)){
                                    canExec=true;
                                }
                            }
                            if(execModel.equals(DataExecModel.SELECT)){
                                if(encryptTransaction.encryptExecModel().equals(DataExecModel.SELECT)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.ALL)){
                                    canExec=true;
                                }
                            }
                            if(canExec){
                                shanHaiTmpData.setSourceValue(tmpText);
                                shanHaiTmpData.setEncryptMethod(encryptTransaction.encryptMethod());
                                tmpText=dataGuardService.encrypt(shanHaiTmpData);
                                field.set(paramsObject,tmpText);
                                if(shanhaiDataGuardConfig.isTraceLog()){
                                    Logger.info("[ShanhaiDataGuard-setParameters-Encrypt]-info:{},result:{}",shanHaiTmpData,tmpText);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return paramsObject;
    }
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}

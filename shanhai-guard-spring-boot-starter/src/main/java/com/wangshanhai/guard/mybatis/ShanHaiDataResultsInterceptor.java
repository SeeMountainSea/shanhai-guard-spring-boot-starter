package com.wangshanhai.guard.mybatis;

import com.wangshanhai.guard.annotation.FieldDataGuard;
import com.wangshanhai.guard.annotation.ShanHaiDataGuard;
import com.wangshanhai.guard.config.DataGuardConfig;
import com.wangshanhai.guard.dataplug.DataExecModel;
import com.wangshanhai.guard.service.ShanHaiDataGuardService;
import com.wangshanhai.guard.utils.Logger;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

/**
 * 批量处理数据查询结果
 * @author Shmily
 */
@Component
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ShanHaiDataResultsInterceptor implements Interceptor {

    private ShanHaiDataGuardService dataGuardService;

    private DataGuardConfig shanhaiDataGuardConfig;

    public ShanHaiDataResultsInterceptor(ShanHaiDataGuardService dataGuardService,DataGuardConfig shanhaiDataGuardConfig) {
        this.dataGuardService = dataGuardService;
        this.shanhaiDataGuardConfig=shanhaiDataGuardConfig;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start=System.currentTimeMillis();
        DefaultResultSetHandler   defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
        Field mappedStatementField = defaultResultSetHandler.getClass().getDeclaredField("mappedStatement");
        mappedStatementField.setAccessible(true);
        MappedStatement mappedStatement = (MappedStatement) mappedStatementField.get(defaultResultSetHandler);
        try{
            ShanHaiDataGuard shanHaiDataGuard=mappedStatement.getResultMaps().get(0).getType().getAnnotation(ShanHaiDataGuard.class);
            if(shanHaiDataGuard==null){
                if(shanhaiDataGuardConfig.isTraceLog()){
                    Logger.info("[ShanhaiDataGuard-handleResultSets-skip]-mapper:{}",mappedStatement.getId());
                }
                return  invocation.proceed();
            }
        }catch (Exception e){
            Logger.error("[ShanhaiDataGuard-handleResultSets-loaderror]-mapper:{},msg:{}",mappedStatement.getId(),e.getMessage());
            return invocation.proceed();
        }
        //取出查询的结果
        Object resultObject = invocation.proceed();
        if (Objects.isNull(resultObject)) {
            return null;
        }
        //基于selectList
        if (resultObject instanceof ArrayList) {
            @SuppressWarnings("unchecked")
            ArrayList<Objects> resultList = (ArrayList<Objects>) resultObject;
            if (!CollectionUtils.isEmpty(resultList) && needToDecrypt(resultList.get(0))) {
                for (Object result : resultList) {
                    //逐一解密
                    decrypt(result,shanhaiDataGuardConfig);
                }
            }
            //基于selectOne
        } else {
            if (needToDecrypt(resultObject)) {
                decrypt(resultObject,shanhaiDataGuardConfig);
            }
        }
        if(shanhaiDataGuardConfig.isSlowFilter()){
            Field boundSqlField = defaultResultSetHandler.getClass().getDeclaredField("boundSql");
            boundSqlField.setAccessible(true);
            BoundSql boundSql = (BoundSql) boundSqlField.get(defaultResultSetHandler);
            long time=System.currentTimeMillis()-start;
            if(time>shanhaiDataGuardConfig.getSlowTime()){
                Logger.warn("[ShanhaiDataGuard-handleResultSets-slow]-time(ms):{},mapper:{},sql:{}",time,mappedStatement.getId(),boundSql.getSql());
            }
            if(shanhaiDataGuardConfig.isTraceLog()){
                Logger.info("[ShanhaiDataGuard-handleResultSets-execTime]-time(ms):{},mapper:{},sql:{}",time,mappedStatement.getId(),boundSql.getSql());
            }
        }
        return resultObject;
    }

    /**
     * 通用解密方法
     * @param result 原始数据
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public <T> T decrypt(T result, DataGuardConfig shanhaiDataGuardConfig) throws IllegalAccessException {
        //取出resultType的类
        Class<?> resultClass = result.getClass();
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //取出所有被DecryptTransaction注解的字段
            FieldDataGuard fieldDataGuard = field.getAnnotation(FieldDataGuard.class);
            if (!Objects.isNull(fieldDataGuard)) {
                field.setAccessible(true);
                Object object = field.get(result);
                //String的解密
                if (object instanceof String) {
                    //对注解的字段进行逐一解密
                    try {
                        String tmpText=(String) object;
                        ShanHaiTmpData shanHaiTmpData=new ShanHaiTmpData();
                        shanHaiTmpData.setExecModel(DataExecModel.SELECT);
                        shanHaiTmpData.setRuleId(fieldDataGuard.ruleId());
                        shanHaiTmpData.setTargetField(field.getName());
                        shanHaiTmpData.setTargetClass(result.getClass().getName());
                        //数据解密（仅查询模式支持）
                        if(fieldDataGuard.decrypt()){
                            shanHaiTmpData.setSourceValue(tmpText);
                            shanHaiTmpData.setDecryptMethod(fieldDataGuard.decryptMethod());
                            tmpText=dataGuardService.decrypt(shanHaiTmpData);
                            field.set(result,tmpText);
                            if(shanhaiDataGuardConfig.isTraceLog()){
                                Logger.info("[ShanhaiDataGuard-handleResultSets-Decrypt]-info:{},result:{}",shanHaiTmpData,tmpText);
                            }
                        }
                        //数据脱敏（查询模式）
                        if(fieldDataGuard.hyposensit()){
                            boolean canExec=false;
                            if(fieldDataGuard.hyposensitExecModel().equals(DataExecModel.SELECT)){
                                canExec=true;
                            }
                            if(canExec){
                                shanHaiTmpData.setSourceValue(tmpText);
                                shanHaiTmpData.setHyposensitMethod(fieldDataGuard.hyposensitMethod());
                                tmpText=dataGuardService.hyposensit(shanHaiTmpData);
                                field.set(result,tmpText);
                                if(shanhaiDataGuardConfig.isTraceLog()){
                                    Logger.info("[ShanhaiDataGuard-handleResultSets-Hyposensit]-info:{},result:{}",shanHaiTmpData,tmpText);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断是否需要解密
     * @param object
     * @return
     */
    private boolean needToDecrypt(Object object) {
        Class<?> objectClass = object.getClass();
        ShanHaiDataGuard shanHaiDataGuard = AnnotationUtils.findAnnotation(objectClass, ShanHaiDataGuard.class);
        return Objects.nonNull(shanHaiDataGuard);
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}

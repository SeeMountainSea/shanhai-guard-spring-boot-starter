package com.wangshanhai.guard.mybatis;

import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.wangshanhai.guard.annotation.FieldDataGuard;
import com.wangshanhai.guard.annotation.ShanHaiDataGuard;
import com.wangshanhai.guard.config.DataGuardConfig;
import com.wangshanhai.guard.dataplug.DataExecModel;
import com.wangshanhai.guard.service.ShanHaiDataGuardService;
import com.wangshanhai.guard.utils.Logger;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

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
        //@Signature 指定了 type= parameterHandler 后，这里的 invocation.getTarget() 便是parameterHandler
        //若指定ResultSetHandler ，这里则能强转为ResultSetHandler
        MybatisParameterHandler parameterHandler = (MybatisParameterHandler) invocation.getTarget();
        // 获取参数对像，即 mapper 中 paramsType 的实例
        Field parameterField = parameterHandler.getClass().getDeclaredField("parameterObject");
        parameterField.setAccessible(true);
        //取出实例
        Object parameterObject = parameterField.get(parameterHandler);
        //对类字段进行加密
        if(!Objects.isNull(parameterObject)){
            Class<?> parameterObjectClass = parameterObject.getClass();
            if(parameterObject instanceof Map){
                 Map<String,Object> params=(Map)parameterObject;
                 for(String pk:params.keySet()){
                     Object pkObj=params.get(pk);
                     Class<?> pkObjClass = pkObj.getClass();
                     ShanHaiDataGuard shanHaiDataGuard = AnnotationUtils.findAnnotation(pkObjClass, ShanHaiDataGuard.class);
                     if(shanHaiDataGuard!=null){
                         //对类字段进行加密
                         //取出当前当前类所有字段，传入加密方法
                         Field[] declaredFields = pkObjClass.getDeclaredFields();
                         encrypt(declaredFields, pkObj,DataExecModel.UPDATE,shanhaiDataGuardConfig);
                     }
                     ((Map)parameterObject).put(pk,pkObj);
                 }
            }else{
                ShanHaiDataGuard shanHaiDataGuard = AnnotationUtils.findAnnotation(parameterObjectClass, ShanHaiDataGuard.class);
                if(shanHaiDataGuard!=null){
                    //对类字段进行加密
                    //取出当前当前类所有字段，传入加密方法
                    Field[] declaredFields = parameterObjectClass.getDeclaredFields();
                    encrypt(declaredFields, parameterObject, DataExecModel.SAVE,shanhaiDataGuardConfig);
                }
            }

        }
        return invocation.proceed();
    }

    /**
     * 对特定字段使用特定加密算法进行数据加密
     * @param declaredFields 字段
     * @param paramsObject 对象
     * @param execModel 执行模式  1：新增 2：更新
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public <T> T encrypt(Field[] declaredFields, T paramsObject,String execModel,DataGuardConfig shanhaiDataGuardConfig) throws IllegalAccessException {
        //取出所有被EncryptTransaction注解的字段
        for (Field field : declaredFields) {
            FieldDataGuard encryptTransaction = field.getAnnotation(FieldDataGuard.class);
            if (!Objects.isNull(encryptTransaction)) {
                field.setAccessible(true);
                Object object = field.get(paramsObject);
                //暂时只实现String类型的加密
                if (object instanceof String) {
                    String value = (String) object;
                    //加密
                    try {
                        String tmpText=value;
                        ShanHaiTmpData shanHaiTmpData=new ShanHaiTmpData();
                        shanHaiTmpData.setExecModel(execModel);
                        shanHaiTmpData.setRuleId(encryptTransaction.ruleId());
                        shanHaiTmpData.setTargetClass(paramsObject.getClass().getName());
                        shanHaiTmpData.setTargetField(field.getName());
                        if(encryptTransaction.hyposensit()){
                            boolean canExec=false;
                            if(execModel.equals(DataExecModel.SAVE)){
                                if(encryptTransaction.hyposensitExecModel().equals(DataExecModel.SAVE)
                                        ||encryptTransaction.hyposensitExecModel().equals(DataExecModel.SAVEANDUPDATE)
                                        ||encryptTransaction.hyposensitExecModel().equals(DataExecModel.SAVEANDQUERY)){
                                    canExec=true;
                                }
                            }else{
                                if(encryptTransaction.hyposensitExecModel().equals(DataExecModel.UPDATE)
                                        ||encryptTransaction.hyposensitExecModel().equals(DataExecModel.SAVEANDUPDATE)
                                        ||encryptTransaction.hyposensitExecModel().equals(DataExecModel.UPDATEANDQUERY)){
                                    canExec=true;
                                }
                            }
                            if(canExec){
                                shanHaiTmpData.setSourceValue(tmpText);
                                shanHaiTmpData.setHyposensitMethod(encryptTransaction.hyposensitMethod());
                                tmpText=dataGuardService.hyposensit(shanHaiTmpData);
                                field.set(paramsObject,tmpText);
                                if(shanhaiDataGuardConfig.isTraceLog()){
                                    Logger.info("[ShanhaiDataGuard-Update-Hyposensit]-info:{},result:{}",shanHaiTmpData,tmpText);
                                }
                            }
                        }
                        if(encryptTransaction.encrypt()){
                            boolean canExec=false;
                            if(execModel.equals( DataExecModel.SAVE)){
                                if(encryptTransaction.encryptExecModel().equals(DataExecModel.SAVE)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.SAVEANDUPDATE)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.SAVEANDQUERY)){
                                    canExec=true;
                                }
                            }else{
                                if(encryptTransaction.encryptExecModel().equals(DataExecModel.UPDATE)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.SAVEANDUPDATE)
                                        ||encryptTransaction.encryptExecModel().equals(DataExecModel.UPDATEANDQUERY)){
                                    canExec=true;
                                }
                            }
                            if(canExec){
                                shanHaiTmpData.setSourceValue(tmpText);
                                shanHaiTmpData.setEncryptMethod(encryptTransaction.encryptMethod());
                                tmpText=dataGuardService.encrypt(shanHaiTmpData);
                                field.set(paramsObject,tmpText);
                                if(shanhaiDataGuardConfig.isTraceLog()){
                                    Logger.info("[ShanhaiDataGuard-Update-Encrypt]-info:{},result:{}",shanHaiTmpData,tmpText);
                                }
                            }
                        }
                        if(encryptTransaction.decrypt()){
                            boolean canExec=false;
                            if(execModel.equals( DataExecModel.SAVE)){
                                if(encryptTransaction.decryptExecModel().equals(DataExecModel.SAVE)
                                        ||encryptTransaction.decryptExecModel().equals(DataExecModel.SAVEANDUPDATE)
                                        ||encryptTransaction.decryptExecModel().equals(DataExecModel.SAVEANDQUERY)){
                                    canExec=true;
                                }
                            }else{
                                if(encryptTransaction.decryptExecModel().equals(DataExecModel.UPDATE)
                                        ||encryptTransaction.decryptExecModel().equals(DataExecModel.SAVEANDUPDATE)
                                        ||encryptTransaction.decryptExecModel().equals(DataExecModel.UPDATEANDQUERY)){
                                    canExec=true;
                                }
                            }
                            if(canExec){
                                shanHaiTmpData.setSourceValue(tmpText);
                                shanHaiTmpData.setDecryptMethod(encryptTransaction.decryptMethod());
                                tmpText=dataGuardService.decrypt(shanHaiTmpData);
                                field.set(paramsObject,tmpText);
                                if(shanhaiDataGuardConfig.isTraceLog()){
                                    Logger.info("[ShanhaiDataGuard-Update-Decrypt]-info:{},result:{}",shanHaiTmpData,tmpText);
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

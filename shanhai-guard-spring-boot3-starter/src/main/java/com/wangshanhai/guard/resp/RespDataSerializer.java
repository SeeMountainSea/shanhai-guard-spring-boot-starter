package com.wangshanhai.guard.resp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.wangshanhai.guard.annotation.RespFieldGuard;
import com.wangshanhai.guard.config.RespGuardConfig;
import com.wangshanhai.guard.service.RespGuardRuleDefService;
import com.wangshanhai.guard.utils.Logger;

import java.io.IOException;
import java.util.Objects;

/**
 * 自定义序列化实现
 * @author Shmily
 */
public class RespDataSerializer extends StdScalarSerializer<Object> {
    private RespGuardRuleDefService respGuardRuleDefService;
    private RespGuardConfig respGuardConfig;
    private RespFieldGuard respFieldGuard;
    private String tragetClass;
    public RespDataSerializer(Class<Object> t, RespGuardRuleDefService respGuardRuleDefService, RespFieldGuard respFieldGuard,
                              String tragetClass, RespGuardConfig respGuardConfig) {
        super(t);
        this.respGuardRuleDefService=respGuardRuleDefService;
        this.respFieldGuard=respFieldGuard;
        this.tragetClass=tragetClass;
        this.respGuardConfig=respGuardConfig;
    }
    protected RespDataSerializer(Class<Object> t) {
        super(t);
    }
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (!Objects.isNull(value)) {
            if(respGuardConfig.isTraceLog()){
                Logger.info("[ShanhaiRespGuard-RespFieldGuard]-class:{},ruleId:{},superPermissionCode:{}",tragetClass,respFieldGuard.ruleId(),respFieldGuard.superPermissionCode());
            }
            jsonGenerator.writeObject(respGuardRuleDefService.jsonGenerator(respFieldGuard.ruleId(),respFieldGuard.superPermissionCode(),value));
        }
    }
}

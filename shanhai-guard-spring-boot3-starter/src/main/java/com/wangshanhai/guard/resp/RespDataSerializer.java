package com.wangshanhai.guard.resp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
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
    private String ruleId;
    private String tragetClass;
    public RespDataSerializer(Class<Object> t,RespGuardRuleDefService respGuardRuleDefService,String ruleId,
                             String tragetClass, RespGuardConfig respGuardConfig) {
        super(t);
        this.respGuardRuleDefService=respGuardRuleDefService;
        this.ruleId=ruleId;
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
                Logger.info("[ShanhaiRespGuard-RespFieldGuard]-class:{},ruleId:{}",tragetClass,ruleId);
            }
            jsonGenerator.writeObject(respGuardRuleDefService.jsonGenerator(ruleId,value));
        }
    }
}

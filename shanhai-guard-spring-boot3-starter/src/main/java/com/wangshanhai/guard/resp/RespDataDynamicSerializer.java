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
public class RespDataDynamicSerializer extends StdScalarSerializer<Object> {
    private RespGuardRuleDefService respGuardRuleDefService;
    private String tragetClass;
    private String tragetField;
    private RespGuardConfig respGuardConfig;
    public RespDataDynamicSerializer(Class<Object> t, RespGuardRuleDefService respGuardRuleDefService, String tragetClass,
                                     String tragetField, RespGuardConfig respGuardConfig ) {
        super(t);
        this.respGuardRuleDefService=respGuardRuleDefService;
        this.tragetClass=tragetClass;
        this.tragetField=tragetField;
        this.respGuardConfig=respGuardConfig;
    }
    protected RespDataDynamicSerializer(Class<Object> t) {
        super(t);
    }
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (!Objects.isNull(value)) {
            if(respGuardConfig.isTraceLog()){
                Logger.info("[ShanhaiRespGuard-RespDataGuard]-class:{},field:{}",tragetClass,tragetField);
            }
            jsonGenerator.writeObject(respGuardRuleDefService.jsonDynamicGenerator(tragetClass,tragetField,value));
        }
    }
}

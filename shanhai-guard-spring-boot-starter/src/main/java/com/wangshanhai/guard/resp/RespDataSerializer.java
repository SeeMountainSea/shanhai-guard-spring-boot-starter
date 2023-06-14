package com.wangshanhai.guard.resp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.wangshanhai.guard.service.RespGuardRuleDefService;

import java.io.IOException;
import java.util.Objects;

/**
 * 自定义序列化实现
 * @author Shmily
 */
public class RespDataSerializer extends StdScalarSerializer<Object> {
    private RespGuardRuleDefService respGuardRuleDefService;
    private String ruleId;

    public RespDataSerializer(Class<Object> t,RespGuardRuleDefService respGuardRuleDefService,String ruleId) {
        super(t);
        this.respGuardRuleDefService=respGuardRuleDefService;
        this.ruleId=ruleId;
    }
    protected RespDataSerializer(Class<Object> t) {
        super(t);
    }
    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (!Objects.isNull(value)) {
            jsonGenerator.writeObject(respGuardRuleDefService.jsonGenerator(ruleId,value));
        }
    }
}

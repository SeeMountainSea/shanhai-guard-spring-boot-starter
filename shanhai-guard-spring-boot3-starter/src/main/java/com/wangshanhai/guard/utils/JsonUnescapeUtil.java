package com.wangshanhai.guard.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * JSON美化
 * @author shanhai
 */
public class JsonUnescapeUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode parseNestedJson(String jsonStr) throws Exception {
        return processNode(mapper.readTree(jsonStr));
    }

    private static JsonNode processNode(JsonNode node) throws Exception {
        if (node.isObject()) {
            return processObject((ObjectNode) node);
        } else if (node.isArray()) {
            return processArray((ArrayNode) node);
        } else if (node.isTextual()) {
            return processText(node.textValue());
        }
        return node;
    }

    private static ObjectNode processObject(ObjectNode node) throws Exception {
        ObjectNode newObj = mapper.createObjectNode();
        node.fields().forEachRemaining(entry -> {
            try {
                newObj.set(entry.getKey(), processNode(entry.getValue()));
            } catch (Exception e) {
                newObj.put(entry.getKey(), entry.getValue().asText());
            }
        });
        return newObj;
    }

    private static ArrayNode processArray(ArrayNode node) throws Exception {
        ArrayNode newArr = mapper.createArrayNode();
        for (JsonNode element : node) {
            newArr.add(processNode(element));
        }
        return newArr;
    }

    private static JsonNode processText(String text) throws Exception {
        if (isJsonString(text)) {
            JsonNode parsed = mapper.readTree(text);
            // 递归处理新解析出的节点
            return parsed.isTextual() ? new TextNode(text) : processNode(parsed);
        }
        return new TextNode(text);
    }

    private static boolean isJsonString(String str) {
        str = str.trim();
        return (str.startsWith("{") && str.endsWith("}")) ||
                (str.startsWith("[") && str.endsWith("]"));
    }
}
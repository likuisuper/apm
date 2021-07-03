package com.cxylk.agent.common;

import com.cxylk.agent.common.json.JsonWriter;

import java.util.HashMap;
import java.util.Map;


/**
 * @author cxylk
 */
public class JsonUtil {
    public static String toJson(Object obj) {
        Map<String, Object> item = new HashMap<>();
        item.put("TYPE", false);
        item.put(JsonWriter.SKIP_NULL_FIELDS, true);
        String json = JsonWriter.objectToJson(obj, item);
        return json;
    }
}

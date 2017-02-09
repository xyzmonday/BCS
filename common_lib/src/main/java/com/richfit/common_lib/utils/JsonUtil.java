package com.richfit.common_lib.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.richfit.domain.bean.RowConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @author modnay JSON工具类
 */
public class JsonUtil {

    private static final Gson gson = new Gson();

    public static Map<String, Object> json2Map(final String json) {
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static String map2Json(Map<String,Object> map) {
        return gson.toJson(map);
    }

    public static String object2Json(Object object) {
        return gson.toJson(object);
    }

    private static Map<String, Object> readExtraByKey(JSONObject source,
                                                      List<RowConfig> configs) {
        Map<String, Object> map = new HashMap<>();
        try {
            for (RowConfig config : configs) {
                String str = source.getString(config.propertyCode);
                map.put(config.propertyCode, str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
}
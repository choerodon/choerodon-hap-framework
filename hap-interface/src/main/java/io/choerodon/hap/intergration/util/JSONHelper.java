/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project
 * Name:hstaffParent Package Name:hstaff.core.util Date:2016/7/8 0008 Create
 * By:zongyun.zhou@hand-china.com
 *
 */
package io.choerodon.hap.intergration.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;

import java.util.List;

public class JSONHelper {
  public static JSONObject getErrorJSONObject(String errorMsg) {
    JSONObject json = new JSONObject();
    json.put("status", "E");
    json.put("errorMsg", errorMsg == null ? "" : errorMsg);
    return json;
  }

  public static JSONObject getSuccessJSONObject(Object obj) {
    JsonConfig jsonConfig = new JsonConfig();
    jsonConfig.registerDefaultValueProcessor(String.class, new DefaultValueProcessor() {
      public Object getDefaultValue(Class type) {
        return JSONNull.getInstance();
      }
    });
    jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultValueProcessor() {
      public Object getDefaultValue(Class type) {
        return JSONNull.getInstance();
      }
    });
    JSONObject json = JSONObject.fromObject(obj, jsonConfig);
    json.put("con_status", "S");
    return json;
  }

  public static JSONArray getSuccessJSONArray(List list) {
    JsonConfig jsonConfig = new JsonConfig();
    jsonConfig.registerDefaultValueProcessor(String.class, new DefaultValueProcessor() {
      public Object getDefaultValue(Class type) {
        return JSONNull.getInstance();
      }
    });
    jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultValueProcessor() {
      public Object getDefaultValue(Class type) {
        return JSONNull.getInstance();
      }
    });
    JSONArray json = JSONArray.fromObject(list, jsonConfig);
    return json;
  }

  public static JSONObject getEmptyJSONObject() {
    JSONObject json = new JSONObject();
    json.put("con_status", "S");
    json.put("status", "S");
    return json;
  }


}

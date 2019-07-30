package io.choerodon.hap.intergration.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.hap.intergration.exception.HapApiException;
import org.dom4j.*;

import java.io.IOException;
import java.util.*;

/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved.
 * Project Name:HmapParent
 * Package Name:hmap.core.util
 * Date:2016/8/18
 * Create By:jiguang.sun@hand-china.com
 */

public class JSONAndMap {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private JSONAndMap() {

    }
    /*
    * json转xml
    * 如果namespace为空，则不使用命名空间
    * */
    public static String jsonToXml(String json, String namespace) throws HapApiException {
        Map map = jsonToMap(json);
        String xml = map2Xml(map, namespace);
        return xml;
    }

    /*
    * json 转 map
    * */
    private static Map jsonToMap(String inbound) throws HapApiException{
        Map<String, Object> map = new HashMap<>();
        try {
            map = objectMapper.readValue(inbound,map.getClass());
        } catch (IOException e) {
            throw new HapApiException(HapApiException.ERROR_MAP_TO_XML,"转换失败");
        }
        /*JSONObject json = JSONObject.fromObject(inbound);
        if (!json.isEmpty() && json.size() > 0) {
            Iterator it = json.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = json.get(key).toString();

                if (value.startsWith("{") && value.endsWith("}")) {
                    map.put(key, jsonToMap(value));
                } else {
                    map.put(key, value);
                }
            }


        }*/
        return map;
    }


    /*
    * map转成xml
    * */
    public static String map2Xml(Map map, String namespace) throws HapApiException {

        StringBuilder xmlStr = new StringBuilder();

        try {
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                Object rootName = it.next();
                Object rootValue = map.get(rootName);
                String str;
                if (rootValue instanceof Map) {
                    str = analysisMap((Map) rootValue, namespace);
                    if (namespace == null) {
                        xmlStr.append("<" + rootName.toString());
                    } else {
                        xmlStr.append("<" + namespace + rootName.toString());
                    }

                    if ("".equals(str)) {
                        xmlStr.append("/>\n");
                    } else {
                        xmlStr.append(">\n");
                        xmlStr.append(str);
                        if (namespace == null) {
                            xmlStr.append("</" + rootName.toString() + ">\n");
                        } else {
                            xmlStr.append("</" + namespace + rootName.toString() + ">\n");
                        }

                    }
                } else {
                    str = JudgmentType(rootValue, rootName.toString(), namespace);
                    xmlStr.append(str);
                }
            }
        } catch (Exception e) {
            throw new HapApiException(HapApiException.ERROR_MAP_TO_XML,"格式错误");
        }

        return xmlStr.toString();
    }


    /*
    * xml成map
    * 把返回的xml转化成map
    * */
    public static Map xml2map(String xmlString) throws HapApiException {
        Document doc = null;
        Map<String, Object> map = null;
        try {
            doc = DocumentHelper.parseText(xmlString);
            Element rootElement = doc.getRootElement();
            map = new HashMap<>();
            if (rootElement.elements().size() == 0) {
                if (rootElement.getNamespace().getText() != null && rootElement.getNamespace().getText() != "") {
                    Map<String, Object> childMap = new HashMap<String, Object>();
                    for (Object obj : rootElement.declaredNamespaces()) {
                        Namespace ns = (Namespace) obj;
                        childMap.put("@xmlns:" + ns.getPrefix(), ns.getURI());
                    }
                    if (!"".equals(rootElement.getText())) {
                        childMap.put("#text", rootElement.getText());
                    }
                    map.put(getName(rootElement), childMap);
                } else {
                    map.put(getName(rootElement), rootElement.getText() == "" ? "" : rootElement.getText());
                }
            } else {
                Map<String, Object> tempMap = new HashMap<>();
                ele2map(tempMap, rootElement);
                if (rootElement.getNamespace().getText() != null && rootElement.getNamespace().getText() != "") {
                    for (Object obj : rootElement.declaredNamespaces()) {
                        Namespace ns = (Namespace) obj;
                        tempMap.put("@xmlns:" + ns.getPrefix(), ns.getURI());
                    }
                }
                map.put(getName(rootElement), tempMap);
            }
        } catch (DocumentException e) {
            throw new HapApiException(HapApiException.ERROR_XML_TO_MAP,"格式错误");
        }
        return map;
    }


    public static String analysisMap(Map map, String namespace) {
        StringBuilder xmlStr = new StringBuilder();
        Set<Object> objSet = map.keySet();
        for (Object key : objSet) {
            if (key == null || key.toString().trim() == "") {
                continue;
            }
            Object value = map.get(key);
            xmlStr.append(JudgmentType(value, key.toString(), namespace));
        }
        return xmlStr.toString();
    }

    public static String analysisCollection(Collection objects, String key, String namespace) {
        StringBuilder xmlStr = new StringBuilder();
        for (Object object : objects) {
            xmlStr.append(JudgmentType(object, key, namespace));
        }
        return xmlStr.toString();
    }

    public static String analysisObjectArr(Object[] objects, String key, String namespace) {
        StringBuilder xmlStr = new StringBuilder();
        for (Object object : objects) {
            xmlStr.append(JudgmentType(object, key, namespace));
        }
        return xmlStr.toString();
    }

    public static String JudgmentType(Object value, String key, String namespace) {
        StringBuilder xmlStr = new StringBuilder();
        if (isBasicTypes(value)) {
            if (value == null) {
                if (namespace == null) {
                    xmlStr.append("<" + key + "/>\n");
                } else {
                    xmlStr.append("<" + namespace + key + "/>\n");
                }

            } else {
                if (namespace == null) {
                    xmlStr.append("<" + key + ">");
                    xmlStr.append(value);
                    xmlStr.append("</" + key + ">\n");
                } else {
                    xmlStr.append("<" + namespace + key + ">");
                    xmlStr.append(value);
                    xmlStr.append("</" + namespace + key + ">\n");
                }

            }
            return xmlStr.toString();
        } else if (value instanceof Map) {
            if (namespace == null) {
                xmlStr.append("<" + key + ">\n");
                xmlStr.append(analysisMap((Map) value, namespace));
                xmlStr.append("\n</" + key + ">");
            } else {
                xmlStr.append("<" + namespace + key + ">\n");
                xmlStr.append(analysisMap((Map) value, namespace));
                xmlStr.append("\n</" + namespace + key + ">");
            }

            return xmlStr.toString();
        } else if (value instanceof Collection) {
            return analysisCollection((Collection) value, key, namespace);
        } else if (value instanceof Object[]) {
            return analysisObjectArr((Object[]) value, key, namespace);
        } else {
            if (namespace == null) {
                xmlStr.append("<" + key.toString() + "/>\n");
            } else {
                xmlStr.append("<" + namespace + key.toString() + "/>\n");
            }

            return xmlStr.toString();
        }
    }

    private static boolean isBasicTypes(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return true;
        }
        if (obj instanceof Integer) {
            return true;
        }
        if (obj instanceof Double) {
            return true;
        }
        if (obj instanceof Float) {
            return true;
        }
        if (obj instanceof Byte) {
            return true;
        }
        if (obj instanceof Long) {
            return true;
        }
        if (obj instanceof Character) {
            return true;
        }
        if (obj instanceof Short) {
            return true;
        }
        if (obj instanceof Boolean) {
            return true;
        }
        return false;
    }


    private static void ele2map(Map map, Element ele) {
        List<Element> elements = ele.elements();
        if (elements.size() == 1) {
            if (elements.get(0).elements().size() == 0) {
                map.put(getName(elements.get(0)), elements.get(0).getText() == "" ? "" : elements.get(0).getText());
            } else {
                Map<String, Object> tempMap = new HashMap<String, Object>();
                ele2map(tempMap, elements.get(0));
                map.put(getName(elements.get(0)), tempMap);
            }
        } else {
            Map<String, Object> tempMap = new HashMap<String, Object>();
            for (Element childElement : elements) {
                String string = getName(childElement);
                if (tempMap.get(string) == null) {
                    List<Element> elements2 = null;
                    if (string.contains(":")) {
                        elements2 = ele.elements(childElement.getQName());
                    } else {
                        elements2 = ele.elements(string);
                    }
                    if (elements2.size() > 1) {
                        List<Object> list = new ArrayList<Object>();
                        for (Element element : elements2) {
                            if (element.elements().size() == 0) {
                                list.add(element.getText() == "" ? "" : element.getText());
                            } else {
                                Map<String, Object> tempMap1 = new HashMap<String, Object>();
                                ele2map(tempMap1, element);
                                list.add(tempMap1);
                            }
                        }
                        map.put(string, list);
                    } else if (elements2.size() > 0) {
                        Map<String, Object> tempMap1 = new HashMap<String, Object>();
                        if (elements2.get(0).elements().size() == 0) {
                            map.put(string, elements2.get(0).getText() == "" ? "" : elements2.get(0).getText());
                        } else {
                            ele2map(tempMap1, elements2.get(0));
                            map.put(string, tempMap1);
                        }
                    }
                }
                tempMap.put(string, true);
            }
        }
    }

    private static String getName(Element ele) {
        StringBuffer sb = new StringBuffer();
        if (ele.getNamespacePrefix() != null && ele.getNamespacePrefix() != "") {
            sb.append(ele.getNamespacePrefix() + ":");
        }
        sb.append(ele.getName());
        return sb.toString();
    }


}

package io.choerodon.hap.task.info;

/**
 * 任务执行处理-接口.
 *
 * @author lijian.yin@hand-china.com
 * @since 2017/11/15.
 **/

public class ParameterInfo {

    private String name;

    private String key;

    private Object value;

    private Object text;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }
}

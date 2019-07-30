package io.choerodon.hap.function.service.impl;

/**
 * @author qiang.zeng
 * @since 2018/11/23.
 */
public class FunctionResourceQuery {
    private Long functionId;
    private String name;
    private String url;

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

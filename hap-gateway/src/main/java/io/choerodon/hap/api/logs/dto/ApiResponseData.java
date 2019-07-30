package io.choerodon.hap.api.logs.dto;

import io.choerodon.web.dto.ResponseData;

/**
 * api调用返回data DTO.
 *
 * @author peng.jiang@hand-china.com
 * @since 017/10/30.
 */
public class ApiResponseData extends ResponseData {

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.extensible.dto;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface IBaseDto {
    String __ID = "__id";
    String __STATUS = "__status";
    String __TLS = "__tls";
    String SORTNAME = "sortname";
    String SORTORDER = "sortorder";

    String _TOKEN = "_token";

    Object getAttribute(String key);

    void setAttribute(String key,Object value);
}

/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.testext.dto;

import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;


/**
 * @author shengyang.zhou@hand-china.com
 */
public interface UserDemoExt {

    @ColumnType(jdbcType = JdbcType.VARCHAR)
    void setUserPhone(String userPhone);

    String getUserPhone();

}

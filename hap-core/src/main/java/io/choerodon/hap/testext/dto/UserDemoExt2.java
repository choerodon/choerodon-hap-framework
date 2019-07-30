/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.testext.dto;

import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import java.util.Date;


/**
 * @author shengyang.zhou@hand-china.com
 */
public interface UserDemoExt2 extends UserDemoExt {

    @ColumnType(jdbcType = JdbcType.TIMESTAMP)
    void setEndActiveTime(Date date);

    Date getEndActiveTime();

}

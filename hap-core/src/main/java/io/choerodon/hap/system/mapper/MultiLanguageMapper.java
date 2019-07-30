package io.choerodon.hap.system.mapper;

import io.choerodon.mybatis.annotation.MultiLanguageField;

import java.util.List;
import java.util.Map;

/**
 * @author njq.niu@hand-china.com
 * @since 2016年3月30日
 */
public interface MultiLanguageMapper {

    List<MultiLanguageField> select(Map<String, String> map);
}
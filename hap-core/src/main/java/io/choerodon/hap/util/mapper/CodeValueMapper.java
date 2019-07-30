package io.choerodon.hap.util.mapper;


import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * 快速编码行Mapper
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9.
 */
public interface CodeValueMapper extends Mapper<CodeValue> {

    /**
     * 根据快码Id删除快码值.
     *
     * @param key 快码Id
     * @return int
     */
    int deleteByCodeId(CodeValue key);

    /**
     * 根据快码Id删除快码值 多语言表数据.
     *
     * @param key 快码Id
     * @return int
     */
    int deleteTlByCodeId(CodeValue key);

    /**
     * 根据快码代码查询快码值列表.
     *
     * @param codeName 快码代码
     * @return 快码值列表
     */
    List<CodeValue> selectCodeValuesByCodeName(String codeName);

    /**
     * 根据快码Id查询快码值列表.
     *
     * @param codeValue 快码Id
     * @return 快码值列表
     */
    List<CodeValue> selectCodeValuesByCodeId(CodeValue codeValue);

    /**
     * 根据父级快码值Id查询快码值列表.
     *
     * @param parentId 父级快码值Id
     * @return 快码值列表
     */
    List<CodeValue> selectCodeValuesByParentId(Long parentId);

    /**
     * 根据快码值Id获取快码值.
     *
     * @param codeValueId 快码值Id
     * @return 快码值
     */
    CodeValue getCodeValueById(Long codeValueId);

    /**
     * 根据快码查询快码值列表.
     *
     * @param code 快码
     * @return 快码值列表
     */
    List<CodeValue> selectCodeValueByCode(Code code);
}
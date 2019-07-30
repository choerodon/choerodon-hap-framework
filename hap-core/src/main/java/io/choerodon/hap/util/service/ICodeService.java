package io.choerodon.hap.util.service;

import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

/**
 * 快速编码Service
 *
 * @author runbai.chen@hand-china.com
 * @since 2016/6/9.
 */
public interface ICodeService extends IBaseService<Code> {

    /**
     * 查询快码列表.
     *
     * @param code     编码
     * @param page     页码
     * @param pagesize 每页数量
     * @return 快码列表
     */
    List<Code> selectCodes(Code code, int page, int pagesize);

    /**
     * 根据快码Id查询快码值列表.
     *
     * @param value 快码Id
     * @return 快码值列表
     */
    List<CodeValue> selectCodeValues(CodeValue value);

    /**
     * 插入快码.
     *
     * @param code 快码
     * @return 快码
     */
    Code createCode(Code code);

    /**
     * 批量删除头行及多语言数据.
     *
     * @param codes 快码列表
     * @return 影响行数
     */
    int batchDelete(List<Code> codes);

    /**
     * 批量删除CodeValue.
     *
     * @param values 快码值列表
     * @return 是否删除成功
     */
    boolean batchDeleteValues(List<CodeValue> values);

    /**
     * 更新快码头行
     *
     * @param code 快码
     * @return 快码
     */
    Code updateCode(Code code);

    /**
     * 批量更新快码.
     *
     * @param codes 快码列表
     * @return 快码列表
     */
    List<Code> batchUpdate(List<Code> codes);

    /**
     * 根据快码名称查询所有代码值.
     *
     * @param codeName 快码名称
     * @return 代码值列表
     */
    List<CodeValue> selectCodeValuesByCodeName(String codeName);


    /**
     * 根据代码和值获取CodeValue.
     *
     * @param codeName 代码
     * @param value    代码值
     * @return codeValue 代码值DTO
     * @author frank.li
     */
    CodeValue getCodeValue(String codeName, String value);

    /**
     * 根据代码和含义获取代码值.
     * <p>
     * 从 cache 直接取值.
     *
     * @param codeName 代码
     * @param meaning  含义
     * @return value 代码值
     * @author frank.li
     */
    String getCodeValueByMeaning(String codeName, String meaning);

    /**
     * 根据代码和值获取Meaning.
     *
     * @param codeName 代码
     * @param value    代码值
     * @return meaning 含义
     */
    String getCodeMeaningByValue(String codeName, String value);

    /**
     * 根据代码和值获取描述.
     *
     * @param codeName 代码
     * @param value    代码值
     * @return description 描述
     */
    String getCodeDescByValue(String codeName, String value);

    /**
     * 根据快码名称查询有效代码值.
     *
     * @param codeName 快码名称
     * @return 代码值列表
     */
    List<CodeValue> getCodeValuesByCode(String codeName);


    /**
     * 根据代码和值获取子快码codeValue.
     *
     * @param codeName 代码
     * @param value    代码值
     * @return codeValue 代码值DTO
     * @author frank.li
     */
    List<CodeValue> getChildCodeValue(String codeName, String value);

    /**
     * 根据父代码、值和子代码获取子代码值.
     *
     * @param parentCodeName 父代码
     * @param value          父代码值
     * @param childCodeName  子代码
     * @return 子代码值
     */
    List<CodeValue> getChildCodeValue(String parentCodeName, String value, String childCodeName);

    /**
     * 优先从缓存中获取快码，
     * 缓存中为空，则从数据库中加载并更新缓存
     *
     * @param codeName 快码名称
     * @return 快码
     */
    Code getValue(String codeName);

    /**
     * 根据快码值Id获取快码值.
     *
     * @param codeValueId 快码值Id
     * @return 快码值
     */
    CodeValue getCodeValueById(Long codeValueId);

    /**
     * 根据快码代码获取快码.
     *
     * @param codeName 快码代码
     * @return 快码
     */
    Code getByCodeName(String codeName);
}

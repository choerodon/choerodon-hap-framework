package io.choerodon.hap.util.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.util.cache.impl.SysCodeCache;
import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.hap.util.mapper.CodeMapper;
import io.choerodon.hap.util.mapper.CodeValueMapper;
import io.choerodon.hap.util.service.ICodeService;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 快速编码ServiceImpl
 *
 * @author shengyang.zhou@hand-china.com
 * @since 2016/6/9.
 */
@Service
@Dataset("Code")
@Transactional(rollbackFor = Exception.class)
public class CodeServiceImpl extends BaseServiceImpl<Code> implements ICodeService, IDatasetService<Code> {

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private CodeValueMapper codeValueMapper;

    @Autowired
    private SysCodeCache codeCache;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Code> selectCodes(Code code, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return codeMapper.selectCodes(code);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CodeValue> selectCodeValues(CodeValue value) {
        return codeValueMapper.selectCodeValuesByCodeId(value);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CodeValue> selectCodeValuesByCodeName(String codeName) {
        Code code = getValue(codeName);
        if (code != null) {
            return code.getCodeValues();
        } else {
            return codeValueMapper.selectCodeValuesByCodeName(codeName);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CodeValue getCodeValue(String codeName, String value) {
        Code code = getValue(codeName);
        if (code == null) {
            return null;
        }
        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String getCodeValueByMeaning(String codeName, String meaning) {
        Code code = getValue(codeName);
        if (code == null) {
            return null;
        }
        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (v.getMeaning().equals(meaning)) {
                return v.getValue();
            }
        }
        return null;
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String getCodeMeaningByValue(String codeName, String value) {
        Code code = getValue(codeName);
        if (code == null) {
            return null;
        }
        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (v.getValue().equals(value)) {
                return v.getMeaning();
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String getCodeDescByValue(String codeName, String value) {
        Code code = getValue(codeName);
        if (code == null) {
            return null;
        }
        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (v.getValue().equals(value)) {
                return v.getDescription();
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CodeValue> getCodeValuesByCode(String codeName) {
        Code code = getValue(codeName);
        if (code != null && BaseConstants.YES.equals(code.getEnabledFlag())) {
            return getEnabledCodeValues(code);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CodeValue> getChildCodeValue(String codeName, String value) {
        CodeValue codeValue = getCodeValue(codeName, value);
        if (codeValue != null) {
            return codeValueMapper.selectCodeValuesByParentId(codeValue.getCodeValueId());
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CodeValue> getChildCodeValue(String parentCodeName, String value,
                                             String childCodeName) {
        CodeValue parentCodeValue = getCodeValue(parentCodeName, value);
        List<CodeValue> childCodeValues = new ArrayList<>();
        if (parentCodeValue != null) {
            Code childCode = getValue(childCodeName);
            if (childCode != null && CollectionUtils.isNotEmpty(childCode.getCodeValues())) {
                Long parentCodeValueId = parentCodeValue.getCodeValueId();
                for (CodeValue codeValue : childCode.getCodeValues()) {
                    if (parentCodeValueId.equals(codeValue.getParentCodeValueId())) {
                        childCodeValues.add(codeValue);
                    }
                }
            }
        }
        return childCodeValues;
    }

    @Override
    public Code getValue(String codeName) {
        String locale = RequestHelper.getCurrentRequest().getLocale();
        Code code = codeCache.getValue(codeName + "." + locale);
        if (code == null) {
            code = codeMapper.getByCodeName(codeName);
            if (code != null) {
                CodeValue codeValue = new CodeValue();
                codeValue.setCodeId(code.getCodeId());
                List<CodeValue> list = codeValueMapper.selectCodeValuesByCodeId(codeValue);
                code.setCodeValues(list);
                codeCache.setValue(codeName + "." + locale, code);
            }
        }
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Code createCode(Code code) {
        // 插入头行
        codeMapper.insertSelective(code);
        // 判断如果行不为空，则迭代循环插入
        if (code.getCodeValues() != null) {
            processCodeValues(code);
        }
        codeCache.reload(code.getCodeId());
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Code> codes) {
        int result = 0;
        // 删除头行
        for (Code code : codes) {
            result = +deleteCode(code);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteValues(List<CodeValue> values) {
        Set<Long> codeIdSet = new HashSet<>();
        for (CodeValue value : values) {
            int updateCount = codeValueMapper.deleteByPrimaryKey(value);
            checkOvn(updateCount, value);
            codeIdSet.add(value.getCodeId());
        }
        for (Long codeId : codeIdSet) {
            codeCache.reload(codeId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Code updateCode(Code code) {
        int count = codeMapper.updateByPrimaryKey(code);
        checkOvn(count, code);
        // 判断如果行不为空，则迭代循环插入
        if (code.getCodeValues() != null) {
            processCodeValues(code);
        }
        codeCache.remove(code.getCode());
        codeCache.reload(code.getCodeId());
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Code> batchUpdate(List<Code> codes) {
        for (Code code : codes) {
            if (code.getCodeId() == null) {
                createCode(code);
            } else {
                updateCode(code);
            }
        }
        return codes;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CodeValue getCodeValueById(Long codeId) {
        return codeValueMapper.getCodeValueById(codeId);
    }

    @Override
    public Code getByCodeName(String codeName) {
        return codeMapper.getByCodeName(codeName);
    }

    @Override
    public List<Code> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        Code code = new Code();
        try {
            BeanUtils.populate(code, body);
            return this.selectCodes(code, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<Code> mutations(List<Code> codes) {
        for (Code code : codes) {
            switch (code.get__status()) {
                case BaseDTO.STATUS_ADD:
                    createCode(code);
                    break;
                case BaseDTO.STATUS_DELETE:
                    deleteCode(code);
                    break;
                case BaseDTO.STATUS_UPDATE:
                    updateCode(code);
                    break;
                default:
                    break;
            }
        }
        return codes;
    }

    private List<CodeValue> getEnabledCodeValues(Code code) {
        List<CodeValue> enabledCodeValues = new ArrayList<>();
        List<CodeValue> allCodeValues = code.getCodeValues();
        if (allCodeValues != null) {
            for (CodeValue codevalue : allCodeValues) {
                if (BaseConstants.YES.equals(codevalue.getEnabledFlag())) {
                    enabledCodeValues.add(codevalue);
                }
            }
        }
        return enabledCodeValues;
    }

    /**
     * 批量操作快码行数据.
     *
     * @param code 头行数据
     */
    private void processCodeValues(Code code) {
        for (CodeValue codeValue : code.getCodeValues()) {
            if (codeValue.getCodeValueId() == null) {
                // 设置头ID跟行ID一致
                codeValue.setCodeId(code.getCodeId());
                codeValueMapper.insertSelective(codeValue);
            } else {
                int count = codeValueMapper.updateByPrimaryKey(codeValue);
                checkOvn(count, codeValue);
            }
        }
    }


    private int deleteCode(Code code) {
        CodeValue codeValue = new CodeValue();
        codeValue.setCodeId(code.getCodeId());
        // 首先删除行的多语言数据
        codeValueMapper.deleteTlByCodeId(codeValue);
        // 然后删除行
        codeValueMapper.deleteByCodeId(codeValue);
        // 最后删除头
        int updateCount = codeMapper.deleteByPrimaryKey(code);
        checkOvn(updateCount, code);
        codeCache.remove(code.getCode());
        return updateCount;
    }


}

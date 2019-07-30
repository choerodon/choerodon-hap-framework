package io.choerodon.hap.util.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.util.cache.impl.SysCodeCache;
import io.choerodon.hap.util.dto.Code;
import io.choerodon.hap.util.dto.CodeValue;
import io.choerodon.hap.util.mapper.CodeValueMapper;
import io.choerodon.hap.util.service.ICodeValueService;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  17:00 2018/11/5
 * Description:
 */
@Service
@Dataset("CodeValue")
@Transactional(rollbackFor = Exception.class)
public class CodeValueServiceImpl extends BaseServiceImpl<CodeValue> implements ICodeValueService, IDatasetService<CodeValue> {

    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private SysCodeCache codeCache;

    @Override
    public List<CodeValue> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        CodeValue value = new CodeValue();
        value.setCodeId((long) (Integer) body.get("codeId"));
        return codeValueMapper.selectCodeValuesByCodeId(value);
    }

    @Override
    public List<CodeValue> mutations(List<CodeValue> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            Set<Long> codeIdSet = new HashSet<>();
            for (CodeValue codeValue : values) {
                switch (codeValue.get__status()) {
                    case  BaseDTO.STATUS_DELETE:
                        int updateCount = codeValueMapper.deleteByPrimaryKey(codeValue);
                        checkOvn(updateCount, codeValue);
                        codeIdSet.add(codeValue.getCodeId());
                        break;
                    default:
                        break;
                }
            }
            for (Long codeId : codeIdSet) {
                codeCache.reload(codeId);
            }
        }
        return values;
    }

    @Override
    public List<CodeValue> selectCodeValueByCode(Code code) {
        return codeValueMapper.selectCodeValueByCode(code);
    }
}

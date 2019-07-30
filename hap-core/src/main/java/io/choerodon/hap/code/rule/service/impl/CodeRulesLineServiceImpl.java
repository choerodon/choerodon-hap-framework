package io.choerodon.hap.code.rule.service.impl;

import io.choerodon.hap.code.rule.dto.CodeRulesLine;
import io.choerodon.hap.code.rule.service.ICodeRulesLineService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.mybatis.common.query.SortField;
import io.choerodon.mybatis.common.query.SortType;
import io.choerodon.mybatis.entity.Criteria;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Dataset("CodeRuleLine")
@Transactional(rollbackFor = Exception.class)
public class CodeRulesLineServiceImpl extends BaseServiceImpl<CodeRulesLine> implements ICodeRulesLineService, IDatasetService<CodeRulesLine> {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public CodeRulesLine updateRecord(CodeRulesLine record) {
        mapper.updateByPrimaryKey(record);
        return record;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            CodeRulesLine codeRulesLine = new CodeRulesLine();
            BeanUtils.populate(codeRulesLine, body);
            Criteria criteria = new Criteria(codeRulesLine);
            criteria.setSortFields(Collections.singleton(new SortField(CodeRulesLine.FIELD_FIELD_SEQUENCE, SortType.ASC)));
            return super.selectOptions(codeRulesLine, criteria);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<CodeRulesLine> mutations(List<CodeRulesLine> codeRulesLineList) {
        for (CodeRulesLine line : codeRulesLineList) {
            switch (line.get__status()) {
                case DTOStatus.DELETE:
                    mapper.deleteByPrimaryKey(line);
                    break;
            }
        }
        return codeRulesLineList;
    }
}
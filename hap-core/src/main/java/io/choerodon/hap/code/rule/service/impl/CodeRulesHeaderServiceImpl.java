package io.choerodon.hap.code.rule.service.impl;

import io.choerodon.hap.cache.impl.SysCodeRuleCache;
import io.choerodon.hap.code.rule.CodeRuleConstants;
import io.choerodon.hap.code.rule.dto.CodeRulesHeader;
import io.choerodon.hap.code.rule.dto.CodeRulesLine;
import io.choerodon.hap.code.rule.mapper.CodeRulesHeaderMapper;
import io.choerodon.hap.code.rule.mapper.CodeRulesLineMapper;
import io.choerodon.hap.code.rule.service.ICodeRulesHeaderService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.mybatis.entity.Criteria;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author xiangyu.qi@hand-china.com
 */

@Service
@Dataset("CodeRuleHeader")
@Transactional(rollbackFor = Exception.class)
public class CodeRulesHeaderServiceImpl extends BaseServiceImpl<CodeRulesHeader> implements ICodeRulesHeaderService, IDatasetService<CodeRulesHeader> {

    @Autowired
    private SysCodeRuleCache ruleCache;

    @Autowired
    private CodeRulesHeaderMapper headerMapper;

    @Autowired
    private CodeRulesLineMapper lineMapper;

    public CodeRulesHeader updateCodeRule(CodeRulesHeader record) {
        headerMapper.updateByPrimaryKey(record);
        //是否删除序列缓存重新创建
        boolean deleteSeq = false;
        if (record.getLines() != null) {
            deleteSeq = processLines(record);
        }
        ruleCache.remove(record.getRuleCode());
        if (deleteSeq) {
            ruleCache.removeSeq(record.getRuleCode());
        }
        if (BaseConstants.YES.equalsIgnoreCase(record.getEnableFlag())) {
            ruleCache.reload(record);
        }
        return record;
    }

    public CodeRulesHeader createCodeRule(CodeRulesHeader record) {
        headerMapper.insertSelective(record);
        // 判断如果行不为空，则迭代循环插入
        if (record.getLines() != null) {
            processLines(record);
        }
        if (BaseConstants.YES.equalsIgnoreCase(record.getEnableFlag())) {
            ruleCache.reload(record);
        }
        return record;
    }

    @Override
    public int deleteByPrimaryKey(CodeRulesHeader record) {
        //删除行
        CodeRulesLine line = new CodeRulesLine();
        line.setHeaderId(record.getHeaderId());
        lineMapper.deleteByHeaderId(line);
        int result = headerMapper.deleteByPrimaryKey(record);
        ruleCache.remove(record.getRuleCode());
        ruleCache.removeSeq(record.getRuleCode());
        return result;
    }

    @Override
    public List<CodeRulesHeader> batchUpdate(List<CodeRulesHeader> list) {
        for (CodeRulesHeader header : list) {
            if (header.getHeaderId() == null) {
                self().createCodeRule(header);
            } else {
                self().updateCodeRule(header);
            }
        }
        return list;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            CodeRulesHeader codeRulesHeader = new CodeRulesHeader();
            BeanUtils.populate(codeRulesHeader, body);
            Criteria criteria = new Criteria(codeRulesHeader);
            criteria.where(new WhereField(CodeRulesHeader.FIELD_HEADER_ID), new WhereField(CodeRulesHeader.FIELD_RULE_CODE, Comparison.LIKE), new WhereField(CodeRulesHeader.FIELD_RULE_NAME, Comparison.LIKE));
            return super.selectOptions(codeRulesHeader, criteria, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<CodeRulesHeader> mutations(List<CodeRulesHeader> codeRulesHeaders) {
        for (CodeRulesHeader header : codeRulesHeaders) {
            switch (header.get__status()) {
                case DTOStatus.ADD:
                    self().createCodeRule(header);
                    break;
                case DTOStatus.UPDATE:
                    self().updateCodeRule(header);
                    break;
                case DTOStatus.DELETE:
                    self().deleteByPrimaryKey(header);
                    break;
            }
        }
        return codeRulesHeaders;
    }

    private boolean processLines(CodeRulesHeader header) {
        boolean updateSeq = false;
        for (CodeRulesLine line : header.getLines()) {
            if (line.getHeaderId() == null) {
                line.setHeaderId(header.getHeaderId()); // 设置头ID跟行ID一致
                lineMapper.insertSelective(line);
            } else {
                int count = lineMapper.updateByPrimaryKey(line);
                checkOvn(count, line);
                if (CodeRuleConstants.FIELD_TYPE_SEQUENCE.equalsIgnoreCase(line.getFiledType())) {
                    updateSeq = true;
                }
            }
        }
        return updateSeq;
    }
}
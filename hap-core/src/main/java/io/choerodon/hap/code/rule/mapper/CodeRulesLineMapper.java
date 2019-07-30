package io.choerodon.hap.code.rule.mapper;

import io.choerodon.hap.code.rule.dto.CodeRulesLine;
import io.choerodon.mybatis.common.Mapper;

public interface CodeRulesLineMapper extends Mapper<CodeRulesLine> {

    int deleteByHeaderId(CodeRulesLine line);
}
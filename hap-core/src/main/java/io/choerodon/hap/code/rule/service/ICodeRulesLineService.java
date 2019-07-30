package io.choerodon.hap.code.rule.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.code.rule.dto.CodeRulesLine;
import io.choerodon.mybatis.service.IBaseService;

public interface ICodeRulesLineService extends IBaseService<CodeRulesLine>, ProxySelf<ICodeRulesLineService> {

    CodeRulesLine updateRecord(CodeRulesLine record);
}
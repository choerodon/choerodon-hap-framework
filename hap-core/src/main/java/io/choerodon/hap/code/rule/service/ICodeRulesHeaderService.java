package io.choerodon.hap.code.rule.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.code.rule.dto.CodeRulesHeader;
import io.choerodon.mybatis.service.IBaseService;

public interface ICodeRulesHeaderService extends IBaseService<CodeRulesHeader>, ProxySelf<ICodeRulesHeaderService> {

    CodeRulesHeader createCodeRule(CodeRulesHeader record);

    CodeRulesHeader updateCodeRule( CodeRulesHeader record);
}
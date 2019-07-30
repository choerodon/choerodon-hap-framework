package io.choerodon.hap.system.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.hap.system.dto.Form;
import io.choerodon.mybatis.service.IBaseService;

import java.util.List;

public interface IFormBuilderService extends IBaseService<Form>, ProxySelf<IFormBuilderService> {

    List<Form> batchUpdate(@StdWho List<Form> forms);

    int batchDelete(List<Form> forms);

}
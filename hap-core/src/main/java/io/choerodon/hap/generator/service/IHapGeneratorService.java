package io.choerodon.hap.generator.service;

import io.choerodon.hap.generator.dto.GeneratorInfo;
import io.choerodon.hap.generator.dto.TableName;

import java.util.List;

/**
 * Created by jialong.zuo@hand-china.com on 2016/10/24.
 */
public interface IHapGeneratorService {
    public List<String> showTables();

    public List<TableName> showTablesObj();

    public int generatorFile(GeneratorInfo info);

}

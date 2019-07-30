package io.choerodon.hap.generator.controllers;

import io.choerodon.hap.generator.dto.GeneratorInfo;
import io.choerodon.hap.generator.dto.TableName;
import io.choerodon.hap.generator.service.IHapGeneratorService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2016/10/24.
 */
@Controller
@RequestMapping(value = "/generator")
public class HapGeneratorController extends BaseController {
    @Autowired
    IHapGeneratorService service;

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/alltables", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData showTables() {
        return new ResponseData(service.showTables());
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/alltablesobj", method = RequestMethod.POST)
    @ResponseBody
    public List<TableName> showTablesObj() {
        return service.showTablesObj();
    }

    @Permission(type = ResourceType.SITE)
    @RequestMapping(value = "/newtables")
    @ResponseBody
    public int generatorTables(GeneratorInfo generatorInfo) {
        return service.generatorFile(generatorInfo);
    }

}

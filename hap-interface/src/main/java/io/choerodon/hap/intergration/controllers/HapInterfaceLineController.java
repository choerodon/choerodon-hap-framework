package io.choerodon.hap.intergration.controllers;

import io.choerodon.hap.intergration.dto.HapInterfaceLine;
import io.choerodon.hap.intergration.service.IHapInterfaceLineService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * @author jiguang.sun@hand-china.com
 * @version 2016/7/26.
 */
@Controller
@RequestMapping(value = {"/sys/api", "/sys/interface", "/api/sys/interface"})
public class HapInterfaceLineController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(HapInterfaceLineController.class);

    @Autowired
    private IHapInterfaceLineService lineService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryLine")
    @ResponseBody
    public ResponseData getLineList(HttpServletRequest request, @ModelAttribute HapInterfaceLine lineAndLineTlDTO) {
        logger.info("query line by LineAndLineTlDTO  lineId:{}", lineAndLineTlDTO.getLineId());
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(lineService.getLineAndLineTl(iRequest, lineAndLineTlDTO));
    }


    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/insertLine")
    @ResponseBody
    public ResponseData insertLine(HttpServletRequest request, @RequestBody List<HapInterfaceLine> hmsInterfaceLines, BindingResult result) {
        // logger.info("add line by LineAndLineTlDTO  headerId:{}", hmsInterfaceLine.getHeaderId());
        getValidator().validate(hmsInterfaceLines, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest iRequest = createRequestContext(request);
        for (HapInterfaceLine hmsInterfaceLine : hmsInterfaceLines) {
            hmsInterfaceLine.setLineId(UUID.randomUUID().toString());
            hmsInterfaceLine.setLineDescription(hmsInterfaceLine.getLineName());
            int updateRow = lineService.insertLine(iRequest, hmsInterfaceLine);
            if (updateRow <= 0) {
                return new ResponseData(false);
            }
        }
        return new ResponseData(hmsInterfaceLines);

    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/updateLine")
    @ResponseBody
    public ResponseData updateLine(HttpServletRequest request, @RequestBody List<HapInterfaceLine> hmsInterfaceLines, BindingResult result) {
        //logger.info("update line by hmsInterfaceLine  lineId:{}", hmsInterfaceLine.getLineId());
        getValidator().validate(hmsInterfaceLines, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }

        IRequest iRequest = createRequestContext(request);
        for (HapInterfaceLine hmsInterfaceLine : hmsInterfaceLines) {
            hmsInterfaceLine.setLineDescription(hmsInterfaceLine.getLineName());
            int updateRow = lineService.updateLine(iRequest, hmsInterfaceLine);
            if (updateRow <= 0) {
                return new ResponseData(false);
            }
        }
        return new ResponseData(hmsInterfaceLines);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/deleteLine")
    @ResponseBody
    public ResponseData deleteLine(HttpServletRequest request, @RequestBody List<HapInterfaceLine> interfaceLines) {
        logger.info("delete line by hmsInterfaceLine  size:", interfaceLines.size());
        lineService.batchDelete(interfaceLines);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getLinesByHeaderId")
    @ResponseBody
    public ResponseData getLinesByHeaderId(HttpServletRequest request, HapInterfaceLine lineAndLineTlDTO,
                                           @RequestParam(defaultValue = DEFAULT_PAGE) final int page, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {

        IRequest iRequest = createRequestContext(request);

        return new ResponseData(lineService.getLinesByHeaderId(iRequest, lineAndLineTlDTO, page, pagesize));
    }


}

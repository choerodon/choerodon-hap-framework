package io.choerodon.hap.intergration.controllers;


import io.choerodon.hap.core.exception.TokenException;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.service.IHapInterfaceHeaderService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author jiguang.sun@hand-china.com
 *         xiangyu.qi@hand-china.com 2016/11/01
 * @version 2016/7/21
 */

@Controller
@RequestMapping(value = {"/sys/api", "/sys/interface", "/api/sys/interface"})
public class HapInterfaceHeaderController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(HapInterfaceHeaderController.class);

    @Autowired
    private IHapInterfaceHeaderService headerService;

    @Autowired
    private IHapInterfaceLineService lineService;

    /**
     * 获取所有的系统路径
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/queryAllHeader")
    @ResponseBody
    public ResponseData getHeaderList(HttpServletRequest request, HapInterfaceHeader headerAndHeaderTlDTO
            , @RequestParam(defaultValue = DEFAULT_PAGE) final int page, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
        return new ResponseData(headerService.getAllHeader(headerAndHeaderTlDTO, page, pagesize));

    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/submit")
    public ResponseData submitCode(@RequestBody List<HapInterfaceHeader> interfaces, BindingResult result, HttpServletRequest request) throws TokenException {
        getValidator().validate(interfaces, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(headerService.batchUpdate(interfaces));
    }


    /*
    * 新增 HmsInterfaceHeader
    * */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/addHeader")
    @ResponseBody
    public ResponseData addHeader(HttpServletRequest request, @RequestBody HapInterfaceHeader hapInterfaceHeader, BindingResult result) {

        getValidator().validate(hapInterfaceHeader, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        hapInterfaceHeader.setHeaderId(UUID.randomUUID().toString());
        hapInterfaceHeader.setDescription(hapInterfaceHeader.getName());
        HapInterfaceHeader hapInterfaceHeaderNew = headerService.insertSelective(hapInterfaceHeader);

        if (hapInterfaceHeaderNew != null) {
            return new ResponseData();
        } else {
            return new ResponseData(false);
        }

    }

    /*
    * 更新 HeaderAndHeaderTlDTO
    * */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/updateHeader")
    @ResponseBody
    public ResponseData updateHeader(HttpServletRequest request, @RequestBody HapInterfaceHeader hapInterfaceHeader, BindingResult result) {

        getValidator().validate(hapInterfaceHeader, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }

        hapInterfaceHeader.setDescription(hapInterfaceHeader.getName());

        int updateRow = headerService.updateHeader(hapInterfaceHeader);

        if (updateRow > 0) {
            return new ResponseData(true);
        } else {
            return new ResponseData(false);
        }


    }

    /*
   * 删除 HmsInterfaceHeader
   * */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/deleteHeader")
    @ResponseBody
    public ResponseData deleteHeader(HttpServletRequest request, @RequestBody List<HapInterfaceHeader> interfaceHeaders) {

        IRequest iRequest = createRequestContext(request);
        //删除行
        lineService.batchDeleteByHeaders(iRequest, interfaceHeaders);
        //删除头
        headerService.batchDelete(interfaceHeaders);

        return new ResponseData();

    }

    /*
    * 根据headerId 查询 header and line
    * */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getHeaderAndLine")
    @ResponseBody
    public ResponseData getHeaderAndLine(HttpServletRequest request, @RequestBody HapInterfaceHeader headerAndHeaderTlDTO) {
        return new ResponseData(headerService.getHeaderAndLineList(headerAndHeaderTlDTO));

    }

    /*
    * 根据headerId获取header
    * */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getHeaderByHeaderId")
    @ResponseBody
    public ResponseData getHeaderByHeaderId(HttpServletRequest request, HapInterfaceHeader headerAndHeaderTlDTO) {
        return new ResponseData(headerService.getHeaderByHeaderId(headerAndHeaderTlDTO));
    }


    /*
    * 根据lineId获取headerAndLine
    * */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getHeaderAndLineByLineId")
    @ResponseBody
    public ResponseData getHeaderAndLineByLineId(HttpServletRequest request, HapInterfaceHeader headerAndLineDTO) {
        logger.info("getHeaderAndLineByLineId lineId:{}", headerAndLineDTO.getLineId());
        HapInterfaceHeader headerAndLineDTO1 = headerService.getHeaderAndLineByLineId(headerAndLineDTO);

        return new ResponseData(Collections.singletonList(headerAndLineDTO1));
    }

    /*
    * 所有有效的请求.
    * */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getAllHeaderAndLine")
    @ResponseBody
    public ResponseData getAllHeaderAndLine(HttpServletRequest request,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) final int page, @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {

        return new ResponseData(headerService.getAllHeaderAndLine(page, pagesize));
    }
}

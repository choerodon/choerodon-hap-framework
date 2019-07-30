package io.choerodon.hap.util.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.util.dto.Lov;
import io.choerodon.hap.util.dto.LovItem;
import io.choerodon.hap.util.service.IKendoLovService;
import io.choerodon.hap.util.service.ILovService;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 通用lov的控制器.
 *
 * @author njq.niu@hand-china.com
 * @since 2016/2/1
 */

@RestController
@RequestMapping(value = {"/sys", "/api/sys"})
public class LovController extends BaseController {

    @Autowired
    private ILovService lovService;

    @Autowired
    private IKendoLovService kendoLovService;

    /**
     * 通用lov配置项查询.
     *
     * @param item    LovItem
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/lovitem/query")
    public ResponseData getLovItems(LovItem item, HttpServletRequest request) {
        return new ResponseData(lovService.selectLovItems(item));
    }

    /**
     * 通用lov查询.
     *
     * @param lov      Lov
     * @param page     起始页
     * @param pagesize 分页大小
     * @param request  HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/lov/query")
    public ResponseData queryLov(Lov lov, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        return new ResponseData(lovService.selectLovs(lov, page, pagesize));
    }

    /**
     * 根据动态的lovCode获取LOV配置.
     *
     * @param contextPath 应用上下文路径
     * @param locale      系统当前语言
     * @param lovCode     lov编码
     * @return LOV配置
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/lov/getLovByCustomCode")
    public String getLovByCustomCode(@RequestParam("contextPath") String contextPath, @RequestParam("locale") Locale locale, @RequestParam("lovCode") String lovCode) {
        return '"' + StringEscapeUtils.escapeJson(kendoLovService.getLov(contextPath, locale, lovCode)) + '"';
    }


    /**
     * 加载通用lov.
     *
     * @param lovId   id
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/lov/load")
    public ResponseData loadLov(@RequestParam Long lovId, HttpServletRequest request) {
        List<Lov> list = new ArrayList<>();
        list.add(lovService.loadLov(lovId));
        return new ResponseData(list);
    }

    /**
     * 删除通用lov.
     *
     * @param items items
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/lov/remove")
    public ResponseData removeLov(@RequestBody List<Lov> items) {
        lovService.batchDeleteLov(items);
        return new ResponseData();
    }

    /**
     * 删除通用lov配置项.
     *
     * @param items LovItem
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/lovitem/remove")
    public ResponseData removeLovItems(@RequestBody List<LovItem> items) {
        lovService.batchDeleteItems(items);
        return new ResponseData();
    }

    /**
     * 保存通用lov.
     *
     * @param lovs    lovs
     * @param result  BindingResult
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/lov/submit")
    public ResponseData submitLov(HttpServletRequest request, @RequestBody List<Lov> lovs, final BindingResult result) {
        getValidator().validate(lovs, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(lovService.batchUpdate(lovs));
    }

    /**
     * 查询lov头行配置项.
     *
     * @param lov     Lov
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping(value = "/lov/lov_define")
    public Lov queryLovDefine(Lov lov, HttpServletRequest request) {
        return lovService.selectLovDefine(lov);
    }
}

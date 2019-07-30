package io.choerodon.hap.attachment.controllers;

import io.choerodon.hap.attachment.dto.AttachCategory;
import io.choerodon.hap.attachment.exception.CategorySourceTypeRepeatException;
import io.choerodon.hap.attachment.service.IAttachCategoryService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 附件分类.
 *
 * @author xiaohua
 */
@Controller
public class AttachCategoryController extends BaseController {

    @Autowired
    private IAttachCategoryService categoryService;

    /**
     * 附件分类列表.
     *
     * @param request  HttpServletRequest
     * @param category AttachCategory参数
     * @return ResponseData 结果对象
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attachment/category/query")
    public ResponseData query(HttpServletRequest request, AttachCategory category) {
        if (category.getParentCategoryId() == null) {
            category.setParentCategoryId(AttachCategory.NO_PARENT);
        }
        category.setSortname(AttachCategory.FIELD_CATEGORY_NAME);
        category.setSortorder("ASC");
        return new ResponseData(categoryService.selectCategories(createRequestContext(request), category));
    }

    /**
     * 保存或者更新目录.
     *
     * @param request    HttpServletRequest
     * @param categories AttachCategory列表
     * @param result     BindingResult
     * @return ResponseData 结果对象
     * @throws CategorySourceTypeRepeatException sourceType重复
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attachment/category/submit")
    public ResponseData submitCategory(HttpServletRequest request, @RequestBody List<AttachCategory> categories, BindingResult result) throws CategorySourceTypeRepeatException {
        getValidator().validate(categories, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        categoryService.validateType(categories);
        return new ResponseData(categoryService.batchUpdate(createRequestContext(request), categories));
    }

    /**
     * 附件目录管理界面.
     *
     * @param request          HttpServletRequest
     * @param parentCategoryId 父级分类Id
     * @return ModelAndView
     */
    @Permission(type = ResourceType.SITE)
    @GetMapping("/attach/sys_attach_category_manage.html")
    public ModelAndView attachmentCategoryList(HttpServletRequest request,
                                               @RequestParam(required = false) String parentCategoryId) {
        ModelAndView view = new ModelAndView(getViewPath() + "/attach/sys_attach_category_manage");
        if (parentCategoryId != null) {
            List<AttachCategory> cates = categoryService.selectCategoryBreadcrumbList(createRequestContext(request),
                    Long.valueOf(parentCategoryId));
            view.addObject("breadCrumb", cates);
        }

        return view;
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping("/sys/attachment/category/list")
    public ResponseData attachmentCategoryListUP(HttpServletRequest request,
                                                 @RequestBody Map<String, Object> body) {
        if (body != null && body.size() != 0) {
            List<AttachCategory> cates = categoryService.selectCategoryBreadcrumbList(createRequestContext(request),
                    Long.parseLong(body.get("parentCategoryId").toString()));
            return new ResponseData(cates);
        }
        return new ResponseData();
    }

    /*
     * 文件管理页面
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attachment/category/queryTree")
    @ResponseBody
    public List<AttachCategory> queryTree(HttpServletRequest request, AttachCategory category) {
        IRequest requestCtx = createRequestContext(request);
        return categoryService.queryTree(requestCtx, category);
    }

}

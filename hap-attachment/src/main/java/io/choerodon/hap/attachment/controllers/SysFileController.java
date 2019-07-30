package io.choerodon.hap.attachment.controllers;

import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.service.ISysFileService;
import io.choerodon.hap.core.exception.TokenException;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * 系统文件Controller.
 *
 * @author xiaohua
 */
@Controller
public class SysFileController extends BaseController {

    @Autowired
    private ISysFileService sysFileService;

    /**
     * 系统文件列表.
     *
     * @param request    HttpServletRequest
     * @param sysFile    SysFile参数对象
     * @param categoryId 所属分类
     * @param page       页号
     * @param pagesize   单页条数
     * @return ResponseData 结果对象
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attach/file/query")
    public ResponseData query(HttpServletRequest request, SysFile sysFile, Long categoryId,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(sysFileService.selectFilesByCategoryId(requestCtx, sysFile, categoryId, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attachment/query")
    public ResponseData queryFilesBySourceTypeAndSourceKey(HttpServletRequest request, String sourceType, String sourceKey) {
        return new ResponseData(sysFileService.selectFilesBySourceTypeAndSourceKey(createRequestContext(request), sourceType, sourceKey));
    }


    /**
     * 系统文件列表.
     *
     * @param request  HttpServletRequest
     * @param sysFile  SysFile参数对象
     * @param page     页号
     * @param pagesize 单页条数
     * @return ResponseData 结果对象
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attach/file/queryFiles")
    public ResponseData query(HttpServletRequest request, SysFile sysFile,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(sysFileService.selectFiles(requestCtx, sysFile, page, pagesize));
    }

    /**
     * 删除.
     *
     * @param request  HttpServletRequest
     * @param sysFiles SysFile列表
     * @return ResponseData 结果对象
     */
    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attach/file/remove")
    public ResponseData remove(HttpServletRequest request, @RequestBody List<SysFile> sysFiles) {
        IRequest requestContext = createRequestContext(request);
        sysFileService.removeFiles(requestContext, sysFiles);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/sys/attach/file/delete")
    public ResponseData deleteFile(HttpServletRequest request, @RequestBody SysFile file) throws TokenException {
        IRequest requestContext = createRequestContext(request);
        sysFileService.deletefiles(requestContext, file);

        return new ResponseData(Collections.singletonList(file));

    }
}

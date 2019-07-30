package io.choerodon.hap.system.controllers.sys;

import io.choerodon.hap.system.dto.Profile;
import io.choerodon.hap.system.dto.ProfileValue;
import io.choerodon.hap.system.service.IProfileService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 配置文件Controller
 *
 * @author frank.li
 * @since 2016/6/9.
 */
@RestController
@RequestMapping(value = {"/sys", "/api/sys"})
public class ProfileController extends BaseController {

    @Autowired
    private IProfileService profileService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/profile/query")
    public ResponseData query(Profile profile, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        return new ResponseData(profileService.selectProfiles(profile, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/profilevalue/query")
    public ResponseData queryProfileValues(ProfileValue value) {
        return new ResponseData(profileService.selectProfileValues(value));
    }

    @RequestMapping(value = "/profilevalue/querylevelvalues", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseData queryLevelValues(ProfileValue value, Long levelId, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        return new ResponseData(profileService.selectLevelValues(value, levelId, page, pagesize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/profile/submit")
    public ResponseData submit(@RequestBody List<Profile> profiles, BindingResult result,
                               HttpServletRequest request) {
        getValidator().validate(profiles, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(profileService.batchUpdate(requestContext, profiles));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/profile/remove")
    public ResponseData remove(@RequestBody List<Profile> profiles, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        profileService.batchDelete(requestContext, profiles);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/profilevalue/remove")
    public ResponseData removeProfileValues(@RequestBody List<ProfileValue> profileValues, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        profileService.batchDeleteValues(requestContext, profileValues);
        return new ResponseData();
    }
}

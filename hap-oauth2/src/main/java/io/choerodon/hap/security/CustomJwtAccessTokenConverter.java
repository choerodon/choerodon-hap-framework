package io.choerodon.hap.security;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.account.service.IUserService;
import io.choerodon.hap.fnd.dto.Company;
import io.choerodon.hap.hr.service.IEmployeeAssignService;
import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.infra.enums.MemberType;
import io.choerodon.hap.system.dto.SysPreferences;
import io.choerodon.hap.system.service.ISysPreferencesService;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.web.core.IRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 自定义JwtAccessTokenConverter.
 *
 * @author njq.niu@hand-china.com
 */
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

    @Autowired
    private ChoerodonRoleService roleService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISysPreferencesService preferencesService;

    @Autowired
    private IEmployeeAssignService employeeAssignService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                     OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = (DefaultOAuth2AccessToken) super.enhance(accessToken, authentication);
        if (authentication.getUserAuthentication() != null && authentication.getUserAuthentication().getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails cud = (CustomUserDetails) authentication.getUserAuthentication().getPrincipal();
            Map<String, Object> additionalInformation = result.getAdditionalInformation();
            User user = userService.convertToUser(cud);
            setUserInfo(additionalInformation, user);
            setRoleInfo(additionalInformation, user);
            // Encode Token to JWT
            String encoded = super.encode(result, authentication);
            // Set JWT as value of the token
            result.setValue(encoded);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        OAuth2Authentication oAuth2Authentication = super.extractAuthentication(map);

        OAuth2Request originRequest = oAuth2Authentication.getOAuth2Request();
        OAuth2Request request = new OAuth2Request(originRequest.getRequestParameters(), originRequest.getClientId(), originRequest.getAuthorities(), true, originRequest.getScope(), originRequest.getResourceIds(), null, null,
                (Map<String, Serializable>) map);
        return new OAuth2Authentication(request, oAuth2Authentication.getUserAuthentication());
    }

    private void setUserInfo(Map<String, Object> additionalInformation, User user) {
        additionalInformation.put(User.FIELD_USER_ID, user.getUserId());
        additionalInformation.put(User.FIELD_EMPLOYEE_CODE, user.getEmployeeCode());
        if (user.getEmployeeId() != null) {
            Long companyId = employeeAssignService.getCompanyByEmployeeId(user.getEmployeeId());
            if (companyId != null) {
                additionalInformation.put(Company.FIELD_COMPANY_ID, companyId);
            }

        }
        SysPreferences pref = preferencesService.selectUserPreference(BaseConstants.PREFERENCE_LOCALE, user.getUserId());
        if (pref != null) {
            additionalInformation.put(BaseConstants.PREFERENCE_LOCALE, StringUtils.parseLocaleString(pref.getPreferencesValue()));
        }
    }

    private void setRoleInfo(Map<String, Object> additionalInformation, User user) {
        List<Long> roleIds = roleService.selectEnableRoleIdsByMemberIdAndMemberType(user.getUserId(), MemberType.USER.value());
        if (CollectionUtils.isNotEmpty(roleIds)) {
            additionalInformation.put(IRequest.FIELD_ALL_ROLE_ID, roleIds);
        }
    }

}

package io.choerodon.hap.security;

import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author njq.niu@hand-china.com
 */
@Component
public class CustomWebExpressionVoter extends WebExpressionVoter {

    @Autowired
    private PermissionAccessor permissionAccessor;
    /**
     * permission没有注册时,是否放过权限校验（默认进行权限校验）.
     * 线上环境设置为false 强制进行权限校验
     * 本地开发可设置为true 方便调试
     */
    @Value("${default.permission.access:false}")
    private boolean defaultPermissionAccess;

    @Override
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {

        for (ConfigAttribute attribute : attributes) {
            if ("permitAll".equals(attribute.toString())) {
                return ACCESS_GRANTED;
            }
        }

        HttpServletRequest request = fi.getRequest();
        String uri = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());
        PermissionDTO permissionDTO = permissionAccessor.getPermissionOfUriAndMethod(uri, request.getMethod());
        if (permissionDTO == null) {
            return defaultPermissionAccess ? ACCESS_GRANTED : ACCESS_DENIED;
        }
        PermissionAccessor.CURRENT_PERMISSION.set(permissionDTO);
        if (permissionDTO.getPublicAccess()) {
            return ACCESS_GRANTED;
        }
        return super.vote(authentication, fi, attributes);
    }


}

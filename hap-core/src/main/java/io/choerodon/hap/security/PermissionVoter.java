package io.choerodon.hap.security;

import io.choerodon.hap.iam.app.service.ChoerodonRoleService;
import io.choerodon.hap.iam.app.service.RolePermissionService;
import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import io.choerodon.hap.iam.infra.dto.RoleDTO;
import io.choerodon.web.core.IRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author hailor
 * @since 16/6/12.
 */
@Component
public class PermissionVoter implements AccessDecisionVoter<FilterInvocation> {
    private static final Logger logger = LoggerFactory.getLogger(PermissionVoter.class);
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private ChoerodonRoleService roleService;
    /**
     * permission没有注册时,是否放过权限校验（默认进行权限校验）.
     * 线上环境设置为false 强制进行权限校验
     * 本地开发可设置为true 方便调试
     */
    @Value("${default.permission.access:false}")
    private boolean defaultPermissionAccess;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;
        assert authentication != null;
        assert fi != null;
        assert attributes != null;

        // 已经 permitAll 的 url 不再过滤(主要是一些资源类 url,通用 url)
        for (ConfigAttribute attribute : attributes) {
            if ("permitAll".equals(attribute.toString())) {
                return result;
            }
        }

        HttpServletRequest request = fi.getRequest();
        String uri = StringUtils.substringAfter(request.getRequestURI(), request.getContextPath());

        if ("".equals(uri)) {
            return ACCESS_ABSTAIN;
        }

        PermissionDTO permissionDTO = PermissionAccessor.CURRENT_PERMISSION.get();
        if (permissionDTO == null) {
            return defaultPermissionAccess ? ACCESS_GRANTED : ACCESS_DENIED;
        }
        PermissionAccessor.CURRENT_PERMISSION.remove();
        if (permissionDTO.getLoginAccess()) {
            if (logger.isDebugEnabled()) {
                logger.debug("url :'{}' need no access control.", uri);
            }
            return ACCESS_ABSTAIN;
        }
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            Set<String> authrotities = AuthorityUtils.authorityListToSet(oAuth2Authentication.getAuthorities());
            List<Long> roleIds = new ArrayList<>();
            for (String roleCode : authrotities) {
                RoleDTO roleDTO = roleService.selectByCode(roleCode);
                if (roleDTO != null) {
                    roleIds.add(roleDTO.getId());
                }
            }
            if (CollectionUtils.isNotEmpty(roleIds)) {
                List<Long> rolePermissionIds = rolePermissionService.queryExistingPermissionIdsByRoleIds(roleIds);
                if (CollectionUtils.isNotEmpty(rolePermissionIds) && rolePermissionIds.contains(permissionDTO.getId())) {
                    return ACCESS_ABSTAIN;
                }
            }
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return ACCESS_DENIED;
        }
        Long roleId = (Long) session.getAttribute(IRequest.FIELD_ROLE_ID);
        Long[] roleIds = (Long[]) session.getAttribute(IRequest.FIELD_ALL_ROLE_ID);
        if (roleId == null || ArrayUtils.isEmpty(roleIds)) {
            return ACCESS_DENIED;
        }
        List<Long> rolePermissionIds = rolePermissionService.queryExistingPermissionIdsByRoleIds(Arrays.asList(roleIds));
        if (CollectionUtils.isNotEmpty(rolePermissionIds) && rolePermissionIds.contains(permissionDTO.getId())) {
            return ACCESS_ABSTAIN;
        }
        logger.debug("access to uri :'{}' denied.", uri);
        result = ACCESS_DENIED;

        return result;
    }

}

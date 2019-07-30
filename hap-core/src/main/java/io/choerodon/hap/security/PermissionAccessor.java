package io.choerodon.hap.security;

import io.choerodon.hap.iam.infra.dto.PermissionDTO;
import io.choerodon.hap.iam.infra.mapper.PermissionMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qiang.zeng
 */
@Component
public class PermissionAccessor {
    private static final Logger logger = LoggerFactory.getLogger(PermissionAccessor.class);
    public static final ThreadLocal<PermissionDTO> CURRENT_PERMISSION = new ThreadLocal<>();
    public static final String PERMISSION_TYPE_API = "api";
    public static final String PERMISSION_TYPE_HTML_PAGE = "html_page";
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    private PermissionMapper permissionMapper;


    public PermissionDTO getPermissionOfUriAndMethod(String uri, String method) {
        // 数据库method全小写
        method = method.toLowerCase();
        // 查询通过url注册的api
        List<PermissionDTO> permissionDTOList = getApiPermissionByMethodAndPath(method, uri);
        if (CollectionUtils.isEmpty(permissionDTOList)) {
            // 查询通过pattern注册的api
            // 所有的 html和dataSet 都是通过 pattern 映射的,但是这些 pattern 都没有注册
            // 每个 html和dataSet 的路径都是单独注册,对于一个 html或dataSet url,如果在权限表中没找到,说明这个 url 没有注册
            // 这种情况,没必要再去解析 这个 url 对应哪个 pattern
            if (!StringUtils.endsWith(uri, ".html") && !StringUtils.startsWith(uri, "/dataset")) {
                permissionDTOList = getApiPermissionByMethodAndPath(method, null);
                if (CollectionUtils.isNotEmpty(permissionDTOList)) {
                    permissionDTOList = permissionDTOList.stream().filter(t -> matcher.match(t.getPath(), uri))
                            .sorted((PermissionDTO o1, PermissionDTO o2) -> {
                                Comparator<String> patternComparator = matcher.getPatternComparator(uri);
                                return patternComparator.compare(o1.getPath(), o2.getPath());
                            }).collect(Collectors.toList());
                }
            }
            if (CollectionUtils.isEmpty(permissionDTOList) && !StringUtils.startsWith(uri, "/dataset")) {
                // 查询通过url注册的html_page
                permissionDTOList = getHtmlPermissionByPath(uri);
            }
        }
        if (CollectionUtils.isEmpty(permissionDTOList)) {
            if (logger.isWarnEnabled()) {
                logger.debug("url {} is not registered", uri);
            }
            return null;
        }
        return permissionDTOList.get(0);
    }

    private List<PermissionDTO> getApiPermissionByMethodAndPath(String method, String path) {
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setMethod(method);
        permissionDTO.setPath(path);
        permissionDTO.setPermissionType(PERMISSION_TYPE_API);
        return permissionMapper.select(permissionDTO);
    }

    private List<PermissionDTO> getHtmlPermissionByPath(String path) {
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setPath(path);
        permissionDTO.setPermissionType(PERMISSION_TYPE_HTML_PAGE);
        return permissionMapper.select(permissionDTO);
    }
}

package io.choerodon.hap.security.oauth.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.hap.security.oauth.dto.TokenLogs;
import io.choerodon.hap.security.oauth.service.ITokenLogsService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qixiangyu
 */
@RestController
@RequestMapping(value = "/sys/token/logs")
public class TokenLogsController extends BaseController {

    @Autowired
    private ITokenLogsService service;

    @Autowired
    @Qualifier("tokenServices")
    private ConsumerTokenServices tokenServices;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    public ResponseData query(TokenLogs dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.select(dto, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/revoke")
    public ResponseData revoke(HttpServletRequest request, @RequestParam String token) {
        tokenServices.revokeToken(token);
        return new ResponseData();
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/remove")
    public ResponseData remove(HttpServletRequest request, @RequestBody String token) {
        JSONObject jsonObject = JSON.parseObject(token);
        tokenServices.revokeToken((String) jsonObject.get("token"));
        return new ResponseData();
    }
}
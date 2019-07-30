package io.choerodon.hap.security.oauth.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.security.oauth.dto.TokenLogs;
import io.choerodon.hap.security.oauth.mapper.TokenLogsMapper;
import io.choerodon.hap.security.oauth.service.ITokenLogsService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * oauth2日志服务 - 实现类.
 *
 * @author qixiangyu
 */
@Service
@Dataset("Token")
@Transactional
public class TokenLogsServiceImpl extends BaseServiceImpl<TokenLogs> implements ITokenLogsService, IDatasetService<TokenLogs> {

    @Autowired
    private TokenLogsMapper tokenLogsMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<TokenLogs> select(TokenLogs condition, int pageNum, int pageSize) {
        IRequest request = RequestHelper.getCurrentRequest();
        if (!"ADMIN".equalsIgnoreCase(request.getEmployeeCode())) {
            condition.setUserId(request.getUserId());
        }
        if ("valid".equalsIgnoreCase(condition.getTokenStatus())) {
            condition.setTokenExpiresTime(new Date());
            condition.setRevokeFlag("Y");
        } else if ("invalid".equalsIgnoreCase(condition.getTokenStatus())) {
            PageHelper.startPage(pageNum, pageSize);
            return processTokenStatus(tokenLogsMapper.selectInvalid(condition));
        }

        return processTokenStatus(super.selectOptions(condition, null, pageNum, pageSize));
    }

    @Override
    @Transactional
    public int revokeToken(String tokenValue) {
        return tokenLogsMapper.revokeToken(tokenValue);
    }

    private List<TokenLogs> processTokenStatus(List<TokenLogs> logs) {
        for (TokenLogs log : logs) {
            if ("N".equalsIgnoreCase(log.getRevokeFlag())) {
                log.setTokenStatus("invalid");
            } else if (log.getTokenExpiresTime().before(new Date())) {
                log.setTokenStatus("invalid");
            } else {
                log.setTokenStatus("valid");
            }
        }
        return logs;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {

            TokenLogs tokenLogs = new TokenLogs();
            BeanUtils.populate(tokenLogs, body);
            if (StringUtils.isEmpty(sortname)) {
                tokenLogs.setSortname("id");
                tokenLogs.setSortorder("DESC");
            } else {
                tokenLogs.setSortname(sortname);
                tokenLogs.setSortorder(isDesc ? "desc" : "asc");
            }
            return this.select(tokenLogs, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<TokenLogs> mutations(List<TokenLogs> objs) {
        return null;
    }
}
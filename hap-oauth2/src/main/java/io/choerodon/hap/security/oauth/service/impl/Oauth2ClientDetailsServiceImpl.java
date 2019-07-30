package io.choerodon.hap.security.oauth.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.security.oauth.dto.Oauth2ClientDetails;
import io.choerodon.hap.security.oauth.mapper.Oauth2ClientDetailsMapper;
import io.choerodon.hap.security.oauth.service.IOauth2ClientDetailsService;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author xiangyu.qi@hand-china.com
 */
@Service
@Dataset("Client")
@Transactional(rollbackFor = Exception.class)
public class Oauth2ClientDetailsServiceImpl extends BaseServiceImpl<Oauth2ClientDetails>
        implements IOauth2ClientDetailsService, IDatasetService<Oauth2ClientDetails> {
    private static final String CACHE_OAUTH_CLIENT = "oauth_client";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private Oauth2ClientDetailsMapper detailsMapper;


    @Override
    public Oauth2ClientDetails insertSelective(Oauth2ClientDetails record) {
        detailsMapper.insertSelective(record);
        getClientCache().setValue(record.getClientId(), record);
        return record;
    }

    @Override
    public Oauth2ClientDetails updateClient(Oauth2ClientDetails clientDetails) {
        Criteria criteria = new Criteria(clientDetails);
        criteria.update(Oauth2ClientDetails.FIELD_ACCESS_TOKEN_VALIDITY, Oauth2ClientDetails.FIELD_CLIENT_SECRET,
                Oauth2ClientDetails.FIELD_ADDITIONAL_INFORMATION, Oauth2ClientDetails.FIELD_AUTHORITIES,
                Oauth2ClientDetails.FIELD_AUTO_APPROVE, Oauth2ClientDetails.FIELD_REDIRECTURI,
                Oauth2ClientDetails.FIELD_REFRESH_TOKEN_VALIDITY, Oauth2ClientDetails.FIELD_SCOPE,
                Oauth2ClientDetails.FIELD_AUTHORIZED_GRANTTYPES, Oauth2ClientDetails.FIELD_RESOURCE_IDS);
        updateByPrimaryKeyOptions(clientDetails, criteria);
        getClientCache().remove(clientDetails.getClientId());
        getClientCache().setValue(clientDetails.getClientId(), clientDetails);
        return clientDetails;
    }

    @Override
    public List<Oauth2ClientDetails> batchUpdate(List<Oauth2ClientDetails> clientDetailses) {
        for (Oauth2ClientDetails details : clientDetailses) {
            if (details.getId() == null) {
                details.setClientSecret(UUID.randomUUID().toString());
                self().insertSelective(details);
            } else {
                self().updateClient(details);
            }
        }
        return clientDetailses;
    }


    @Override
    public int deleteByPrimaryKey(Oauth2ClientDetails details) {
        //TODO: 删除对应应用或者不允许删除
        if (StringUtils.isEmpty(details.getClientId())) {
            details = detailsMapper.selectByPrimaryKey(details);
        }
        int result = detailsMapper.deleteByPrimaryKey(details);
        getClientCache().remove(details.getClientId());
        return result;
    }

    @Override
    public Oauth2ClientDetails selectByClientId(String clientID) {
        Oauth2ClientDetails details = getClientCache().getValue(clientID);
        if (details == null) {
            details = detailsMapper.selectByClientId(clientID);
            if (details != null) {
                getClientCache().setValue(clientID, details);
            }
        }
        return details;
    }

    @Override
    public String updatePassword(Long id) {
        String uuid = UUID.randomUUID().toString();
        detailsMapper.updatePassword(id, uuid);
        return uuid;
    }

    private Cache<Oauth2ClientDetails> getClientCache() {
        return cacheManager.getCache(CACHE_OAUTH_CLIENT);
    }

    @Override
    public Oauth2ClientDetails selectById(Long id) {
        return detailsMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Oauth2ClientDetails oauth2ClientDetails = new Oauth2ClientDetails();
            BeanUtils.populate(oauth2ClientDetails, body);
            return super.selectOptions(oauth2ClientDetails, null, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }

    }

    @Override
    public List<Oauth2ClientDetails> mutations(List<Oauth2ClientDetails> oauth2ClientDetailsList) {
        for (Oauth2ClientDetails oauth2ClientDetails : oauth2ClientDetailsList) {
            switch (oauth2ClientDetails.get__status()) {
                case DTOStatus.ADD:
                    oauth2ClientDetails.setClientSecret(UUID.randomUUID().toString());
                    self().insertSelective(oauth2ClientDetails);
                    break;
                case DTOStatus.DELETE:
                    this.deleteByPrimaryKey(oauth2ClientDetails);
                    break;
                case DTOStatus.UPDATE:
                    self().updateClient(oauth2ClientDetails);
                    break;
                default:
                    break;
            }
        }
        return oauth2ClientDetailsList;
    }

}
package io.choerodon.hap.intergration.service.impl;


import com.github.pagehelper.PageHelper;
import io.choerodon.hap.cache.impl.ApiConfigCache;
import io.choerodon.hap.core.annotation.StdWho;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.dto.HapInterfaceLine;
import io.choerodon.hap.intergration.mapper.HapInterfaceHeaderMapper;
import io.choerodon.hap.intergration.mapper.HapInterfaceLineMapper;
import io.choerodon.hap.intergration.service.IHapAuthenticationService;
import io.choerodon.hap.intergration.service.IHapInterfaceHeaderService;
import io.choerodon.hap.intergration.service.IHapInterfaceLineService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.choerodon.hap.system.dto.DTOStatus.ADD;
import static io.choerodon.hap.system.dto.DTOStatus.DELETE;
import static io.choerodon.hap.system.dto.DTOStatus.UPDATE;

/**
 * @author jiguang.sun@hand-china.com
 * @since 2016/7/21.
 */
@Service
@Dataset("InterfaceHeader")
@Transactional(rollbackFor = Exception.class)
public class HapInterfaceHeaderServiceImpl extends BaseServiceImpl<HapInterfaceHeader> implements IHapInterfaceHeaderService, IDatasetService<HapInterfaceHeader> {

    private final Logger logger = LoggerFactory.getLogger(HapInterfaceHeaderServiceImpl.class);

    @Autowired
    private HapInterfaceHeaderMapper hapInterfaceHeaderMapper;

    @Autowired
    private HapInterfaceLineMapper hapInterfaceLineMapper;

    @Autowired
    private ApiConfigCache apiCache;


    @Autowired
    private IHapInterfaceLineService lineService;

    @Autowired
    private IHapAuthenticationService authenticationService;

    private static final String AUTH_TYPE_OAUTH2 = "OAUTH2";


    @Override
    public List<HapInterfaceHeader> getAllHeader(HapInterfaceHeader interfaceHeader, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hapInterfaceHeaderMapper.getAllHeader(interfaceHeader);
    }

    @Override
    public List<HapInterfaceHeader> getHeaderAndLineList(HapInterfaceHeader interfaceHeader) {
        List<HapInterfaceHeader> list = hapInterfaceHeaderMapper.getHeaderAndLineList(interfaceHeader);
        if (list.isEmpty() || list.size() < 0) {
            list = hapInterfaceHeaderMapper.getHeaderByHeaderId(interfaceHeader);
        }
        return list;

    }

    @Override
    public HapInterfaceHeader getHeaderAndLine(String sysName, String apiName) {
        logger.info("sysName apiName:{}", sysName + apiName);
        HapInterfaceHeader headerAndLineDTO = apiCache.getValue(sysName + HapInterfaceHeader.CACHE_SEPARATOR + apiName);
        if (headerAndLineDTO == null) {
            HapInterfaceHeader headerAndLineDTO1 = hapInterfaceHeaderMapper.getHeaderAndLineBySysNameAndApiName(sysName, apiName);
            if (headerAndLineDTO1 != null) {
                apiCache.setValue(sysName + HapInterfaceHeader.CACHE_SEPARATOR + apiName, headerAndLineDTO1);
            }
            return headerAndLineDTO1;
        } else {
            return headerAndLineDTO;

        }

    }

    /*
     * 获取所有的header和line数据——> HeaderAndHeaderTlDTO
     * */
    @Override
    public List<HapInterfaceHeader> getAllHeaderAndLine() {
        return hapInterfaceHeaderMapper.getAllHeaderAndLine();
    }


    /*
     * 获取所有的header和line数据——> HeaderAndHeaderTlDTO(分页)
     * */
    @Override
    public List<HapInterfaceHeader> getAllHeaderAndLine(int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hapInterfaceHeaderMapper.getAllHeaderAndLine();
    }

    @Override
    public List<HapInterfaceHeader> getHeaderByHeaderId(HapInterfaceHeader HapInterfaceHeader) {
        return hapInterfaceHeaderMapper.getHeaderByHeaderId(HapInterfaceHeader);
    }

    @Override
    public HapInterfaceHeader getHeaderAndLineByLineId(HapInterfaceHeader headerAndLineDTO) {
        return hapInterfaceHeaderMapper.getHeaderAndLineBylineId(headerAndLineDTO);
    }

    @Override
    public int updateHeader(HapInterfaceHeader hmsInterfaceHeader) {

        int result = hapInterfaceHeaderMapper.updateByPrimaryKeySelective(hmsInterfaceHeader);
        checkOvn(result, hmsInterfaceHeader);
        if (result > 0) {
            // 修改头，修改后重新加入缓存
            apiCache.reload();
        }
        return result;
    }

    @Override
    public void createInterface(HapInterfaceHeader interfaceHeader) {
        interfaceHeader.setHeaderId(UUID.randomUUID().toString());
        interfaceHeader.setDescription(interfaceHeader.getName());
        self().insertSelective(interfaceHeader);
        if (interfaceHeader.getLineList() != null) {
            processInterfaceLines(RequestHelper.getCurrentRequest(), interfaceHeader);
        }
    }

    @Override
    public void updateInterface(HapInterfaceHeader interfaceHeader) {
        interfaceHeader.setDescription(interfaceHeader.getName());
        self().updateHeader(interfaceHeader);

        if (interfaceHeader.getLineList() != null) {
            processInterfaceLines(RequestHelper.getCurrentRequest(), interfaceHeader);
        }
        if (AUTH_TYPE_OAUTH2.equalsIgnoreCase(interfaceHeader.getAuthType())) {
            authenticationService.removeToken(interfaceHeader);
        }
    }

    private void processInterfaceLines(IRequest iRequest, HapInterfaceHeader interfaceHeader) {
        for (HapInterfaceLine line : interfaceHeader.getLineList()) {
            line.setLineDescription(line.getLineName());
            if (line.getLineId() == null) {
                line.setHeaderId(interfaceHeader.getHeaderId());
                line.setLineId(UUID.randomUUID().toString());
                lineService.insertLine(iRequest, line);
            } else {
                lineService.updateLine(iRequest, line);
            }
        }
    }

    @Override
    public List<HapInterfaceHeader> batchUpdate(List<HapInterfaceHeader> interfaces) {
        for (HapInterfaceHeader interfaceHeader : interfaces) {
            if (interfaceHeader.getHeaderId() == null) {
                self().createInterface(interfaceHeader);
            } else {
                self().updateInterface(interfaceHeader);
            }
        }
        return interfaces;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {

        try {
            HapInterfaceHeader hapInterfaceHeader = new HapInterfaceHeader();
            BeanUtils.populate(hapInterfaceHeader, body);
            return this.getAllHeader(hapInterfaceHeader, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<HapInterfaceHeader> mutations(List<HapInterfaceHeader> hapInterfaceHeaders) {
        for (HapInterfaceHeader hapInterfaceHeader : hapInterfaceHeaders) {
            switch (hapInterfaceHeader.get__status()) {
                case ADD:
                    self().createInterface(hapInterfaceHeader);
                    break;
                case DELETE:
                    deleteInterface(hapInterfaceHeader);
                    break;
                case UPDATE:
                    self().updateInterface(hapInterfaceHeader);
                    break;
                default:
                    break;
            }
        }
        return hapInterfaceHeaders;
    }

    private void deleteInterface(@StdWho HapInterfaceHeader hapInterfaceHeader) {
        HapInterfaceLine hapInterfaceLine = new HapInterfaceLine();
        hapInterfaceLine.setHeaderId(hapInterfaceHeader.getHeaderId());
        int count = hapInterfaceLineMapper.deleteByHeaderId(hapInterfaceLine);
        checkOvn(count, hapInterfaceLine);
        int countH = hapInterfaceHeaderMapper.deleteByPrimaryKey(hapInterfaceHeader);
        checkOvn(countH, hapInterfaceHeader);
    }
}

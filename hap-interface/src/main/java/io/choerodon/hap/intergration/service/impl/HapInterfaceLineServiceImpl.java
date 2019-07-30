package io.choerodon.hap.intergration.service.impl;

import com.github.pagehelper.PageHelper;
import io.choerodon.hap.cache.impl.ApiConfigCache;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.dto.HapInterfaceLine;
import io.choerodon.hap.intergration.mapper.HapInterfaceLineMapper;
import io.choerodon.hap.intergration.service.IHapInterfaceLineService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.web.core.IRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.choerodon.hap.system.dto.DTOStatus.DELETE;

/**
 * Created by user on 2016/7/26.
 * xiangyu.qi on 2016/11/11
 */
@Service
@Dataset("InterfaceLine")
@Transactional(rollbackFor = Exception.class)
public class HapInterfaceLineServiceImpl extends BaseServiceImpl<HapInterfaceLine> implements IHapInterfaceLineService, IDatasetService<HapInterfaceLine> {

    private final Logger logger = LoggerFactory.getLogger(HapInterfaceLineServiceImpl.class);
    @Autowired
    private HapInterfaceLineMapper hmsLineMapper;

    @Autowired
    private ApiConfigCache apiCache;

    @Override
    public List<HapInterfaceLine> getLineAndLineTl(IRequest request, HapInterfaceLine lineAndLineTlDTO) {
        return hmsLineMapper.getLineAndLineTl(lineAndLineTlDTO);
    }


    @Override
    public List<HapInterfaceLine> getLinesByHeaderId(IRequest request, HapInterfaceLine lineAndLineTlDTO, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return hmsLineMapper.getLinesByHeaderId(lineAndLineTlDTO);
    }

    @Override
    public int insertLine(IRequest request, HapInterfaceLine hmsInterfaceLine) {

        int result = hmsLineMapper.insertSelective(hmsInterfaceLine);

        if (result > 0) {
            apiCache.reload(hmsInterfaceLine.getLineId());
        }

        return result;
    }

    @Override
    public int updateLine(IRequest request, HapInterfaceLine hmsInterfaceLine) {

        int result = hmsLineMapper.updateByPrimaryKeySelective(hmsInterfaceLine);
        checkOvn(result, hmsInterfaceLine);
        if (result > 0) {
            apiCache.reload();
        }

        return result;
    }

    @Override
    public int batchDelete(List<HapInterfaceLine> list) {
        int result = 0;
        for (HapInterfaceLine line : list) {
            result = hmsLineMapper.deleteByPrimaryKey(line);
            checkOvn(result, line);
        }
        if (result > 0) {
            apiCache.reload();
        }
        return result;
    }

    @Override
    public int batchDeleteByHeaders(IRequest request, List<HapInterfaceHeader> lists) {

        int result = 0;

        for (HapInterfaceHeader index : lists) {
            HapInterfaceLine line = new HapInterfaceLine();
            line.setHeaderId(index.getHeaderId());

            hmsLineMapper.deleteTlByHeaderId(line);
            result = hmsLineMapper.deleteByHeaderId(line);
            checkOvn(result, line);
        }
        if (result > 0) {
            apiCache.reload();
        }
        return result;
    }


    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        HapInterfaceLine hapInterfaceLine = new HapInterfaceLine();
        hapInterfaceLine.setHeaderId((String) body.get("headerId"));
        return this.getLinesByHeaderId(null, hapInterfaceLine, page, pageSize);
    }

    @Override
    public List<HapInterfaceLine> mutations(List<HapInterfaceLine> hapInterfaceLines) {
        for (HapInterfaceLine hapInterfaceLine : hapInterfaceLines) {
            switch (hapInterfaceLine.get__status()) {
                case DELETE:
                    self().batchDelete(Collections.singletonList(hapInterfaceLine));
                    break;
                default:
                    break;
            }
        }
        return hapInterfaceLines;
    }
}

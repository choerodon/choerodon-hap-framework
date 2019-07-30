package io.choerodon.hap.report.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.report.dto.Report;
import io.choerodon.hap.report.mapper.ReportMapper;
import io.choerodon.hap.report.service.IReportService;
import io.choerodon.hap.system.mapper.ParameterConfigMapper;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.mybatis.entity.Criteria;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static io.choerodon.hap.system.dto.DTOStatus.ADD;
import static io.choerodon.hap.system.dto.DTOStatus.DELETE;
import static io.choerodon.hap.system.dto.DTOStatus.UPDATE;

/**
 * 报表服务接口实现.
 *
 * @author qiang.zeng
 * @since 2017/9/21
 */
@Service
@Dataset("Report")
@Transactional(rollbackFor = Exception.class)
public class ReportServiceImpl extends BaseServiceImpl<Report> implements IReportService, IDatasetService<Report> {
    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ParameterConfigMapper parameterConfigMapper;

    @Override
    public int batchDelete(List<Report> reports) {
        int count = 0;
        if (CollectionUtils.isNotEmpty(reports)) {
            for (Report report : reports) {
                self().deleteByPrimaryKey(report);
                count++;
            }
        }
        return count;
    }

    @Override
    public int deleteByPrimaryKey(Report report) {
        int updateCount = reportMapper.deleteByPrimaryKey(report);
        checkOvn(updateCount, report);
        parameterConfigMapper.deleteByCodeAndTargetId("REPORT", report.getReportId());
        return updateCount;
    }

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }

    @Override
    public List<Report> selectByReportCode(String reportCode) {
        return reportMapper.selectByReportCode(reportCode);
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Report report = new Report();
            BeanUtils.populate(report, body);
            report.setSortname(sortname);
            report.setSortorder(isDesc ? "desc" : "asc");
            Criteria criteria = new Criteria(report);
            return super.selectOptions(report, criteria, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<Report> mutations(List<Report> reports) {
        for (Report report : reports) {
            switch (report.get__status()) {
                case ADD:
                    super.insertSelective(report);
                    break;
                case UPDATE:
                    super.updateByPrimaryKey(report);
                    break;
                case DELETE:
                    self().deleteByPrimaryKey(report);
                    break;
            }
        }
        return reports;
    }

}
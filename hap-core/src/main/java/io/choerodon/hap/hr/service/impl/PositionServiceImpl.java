package io.choerodon.hap.hr.service.impl;

import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.hr.dto.Position;
import io.choerodon.hap.hr.mapper.EmployeeAssignMapper;
import io.choerodon.hap.hr.service.IPositionService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.choerodon.hap.system.dto.DTOStatus.ADD;
import static io.choerodon.hap.system.dto.DTOStatus.DELETE;
import static io.choerodon.hap.system.dto.DTOStatus.UPDATE;

/**
 * 岗位服务接口实现.
 *
 * @author hailin.xu@hand-china.com
 */
@Service
@Dataset("Position")
public class PositionServiceImpl extends BaseServiceImpl<Position> implements IPositionService, IDatasetService<Position> {

    @Autowired
    private IMessagePublisher messagePublisher;

    @Autowired
    private EmployeeAssignMapper employeeAssignMapper;

    @Override
    protected boolean useSelectiveUpdate() {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Position> batchUpdate(List<Position> list) {
        Criteria criteria = new Criteria();
        criteria.update(Position.FIELD_NAME, Position.FIELD_DESCRIPTION, Position.FIELD_UNIT_ID, Position.FIELD_PARENT_POSITION_ID);
        criteria.updateExtensionAttribute();
        for (Position position : list) {
            if (position.get__status().equalsIgnoreCase(UPDATE)) {
                self().updateByPrimaryKeyOptions(position, criteria);
            } else {
                self().insertSelective(position);
            }
            messagePublisher.publish("position.change", position);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Position position) {
        int ret = super.deleteByPrimaryKey(position);
        employeeAssignMapper.deleteByPositionId(position.getPositionId());
        return ret;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            Position position = new Position();
            BeanUtils.populate(position, body);
            position.setSortname(sortname);
            position.setSortorder(isDesc ? "desc" : "asc");
            Criteria criteria = new Criteria(position);
            criteria.where(new WhereField(Position.FIELD_POSITION_CODE, Comparison.LIKE), Position.FIELD_POSITION_ID, Position.FIELD_NAME, Position.FIELD_PARENT_POSITION_ID);
            return super.selectOptions(position, criteria, page, pageSize);
        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Position> mutations(List<Position> positions) {
        for (Position position : positions) {
            switch (position.get__status()) {
                case ADD:
                    self().batchUpdate(Collections.singletonList(position));
                    break;
                case DELETE:
                    self().deleteByPrimaryKey(position);
                    break;
                case UPDATE:
                    self().batchUpdate(Collections.singletonList(position));
                    break;
                default:
                    break;
            }
        }
        return positions;
    }
}



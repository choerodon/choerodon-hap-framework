package io.choerodon.hap.hr.controllers;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.hap.hr.dto.Position;
import io.choerodon.hap.hr.service.IPositionService;
import io.choerodon.mybatis.common.query.Comparison;
import io.choerodon.mybatis.common.query.WhereField;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 对岗位的操作.
 *
 * @author hailin.xu@hand-china.com
 */

@RestController
public class PositionController extends BaseController {
    @Autowired
    private IPositionService positionService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/hr/position/query")
    public ResponseData getPosition(HttpServletRequest request, Position position, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        Criteria criteria = new Criteria(position);
        criteria.where(new WhereField(Position.FIELD_POSITION_CODE, Comparison.LIKE), Position.FIELD_POSITION_ID, Position.FIELD_NAME, Position.FIELD_PARENT_POSITION_ID);
        return new ResponseData(positionService.selectOptions(position, criteria, page, pagesize));

    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/api/hr/position/query")
    public ResponseData getPositionApi(HttpServletRequest request, Position position, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        Criteria criteria = new Criteria(position);
        criteria.where(new WhereField(Position.FIELD_POSITION_CODE, Comparison.LIKE), Position.FIELD_POSITION_ID, Position.FIELD_NAME, Position.FIELD_PARENT_POSITION_ID);
        return new ResponseData(positionService.selectOptions(position, criteria, page, pagesize));

    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/hr/position/submit")
    public ResponseData submitPosition(@RequestBody List<Position> positions, BindingResult result, HttpServletRequest request) {
        getValidator().validate(positions, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(positionService.batchUpdate(positions));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/hr/position/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Position> positions) {
        positionService.batchDelete(positions);
        return new ResponseData();
    }
}

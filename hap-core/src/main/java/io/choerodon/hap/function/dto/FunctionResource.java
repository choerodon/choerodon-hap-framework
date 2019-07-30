package io.choerodon.hap.function.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 功能资源DTO.
 *
 * @author wuyichu
 */
@Table(name = "sys_function_resource")
public class FunctionResource extends BaseDTO {

    private static final long serialVersionUID = 2205839053452054599L;
    public static final String FIELD_FUNC_SRC_ID = "funcSrcId";
    public static final String FIELD_FUNCTION_ID = "functionId";
    public static final String FIELD_RESOURCE_ID = "resourceId";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long funcSrcId;

    @Column
    private Long functionId;

    @Column
    private Long resourceId;

    public Long getFuncSrcId() {
        return funcSrcId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setFuncSrcId(Long funcSrcId) {
        this.funcSrcId = funcSrcId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}
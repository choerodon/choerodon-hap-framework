/*
 * #{copyright}#
 */

package io.choerodon.hap.core.exception;

import io.choerodon.base.exception.BaseException;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * TokenException.
 * 
 * <p>
 * Created on 15/12/28
 *
 * @author jessen
 */
public class TokenException extends BaseException {

    private static final long serialVersionUID = -4610975985890969248L;

    public static final String CODE = "TOKEN_FAILURE";
    public static final String MSG_NOT_EXISTS = "msg.error.token_not_exists";
    public static final String MSG_CHECK_FAILED = "msg.error.token_check_failed";

    private BaseDTO dto;

    /**
     * use default message : MSG_CHECK_FAILED.
     * 
     * @param dto
     *            source dto
     */
    public TokenException(BaseDTO dto) {
        this(MSG_CHECK_FAILED, dto);
    }

    public TokenException(String descriptionKey, BaseDTO dto) {
        super(CODE, descriptionKey, null);
        this.dto = dto;
    }

    /**
     * 取得校验失败的dto.
     * 
     * @param <T>
     *            目标类型
     * @return 导致异常的dto
     */
    @SuppressWarnings("unchecked")
    public <T> T getDto() {
        return (T) dto;
    }

}

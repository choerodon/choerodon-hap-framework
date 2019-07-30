/*
 * #{copyright}#
 */

package io.choerodon.hap.core.exception;

import io.choerodon.base.exception.BaseException;

/**
 * EmailException.
 * 
 * <p> Created on 16/3/3
 *
 * @author Clerifen Li
 */
public class EmailException extends BaseException {

    private static final long serialVersionUID = 1L;
    
    public static final String MSG_ERROR_SAME_CODE_AND_MARKET_ID_IS_EXISTS = "msg.error.same_code_and_market_id_is_exists";
    
    public static final String MSG_ERROR_SAME_CODE_IS_EXISTS = "msg.error.same_code_is_exists";
    
    public static final String MSG_ERROR_SAME_MARKET_ID_IS_EXISTS = "msg.error.same_market_id_is_exists";
    
    public EmailException(String code) {
        this(code, code, new Object[0]);
    }

    public EmailException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }
    
}

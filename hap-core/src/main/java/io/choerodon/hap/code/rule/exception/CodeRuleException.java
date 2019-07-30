package io.choerodon.hap.code.rule.exception;


import io.choerodon.base.exception.BaseException;

public class CodeRuleException extends BaseException {


    private static final long serialVersionUID = -8733545249616363585L;

    public static final String ERROR_NOT_FOUND = "can not find codeRule from cache";

    public static final String ERROR_SEQ_LENGTH_TO_LONG = "Failed to generate the encoding, the sequence is longer than the length";

    public static final String ERROR_GET_VARIBLE_FAILUE = "Failed to generate the encoding, process varible fail";

    protected CodeRuleException(String code, String descriptionKey, Object[] parameters) {
        super(code, descriptionKey, parameters);
    }

    public CodeRuleException(String message, String descriptionKey){
//        super(EXCEPTION_CODE,message,descriptionKey);
        super(message,descriptionKey,null);

    }

}

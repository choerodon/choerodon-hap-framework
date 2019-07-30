package io.choerodon.hap.dataset.model;

import io.choerodon.hap.dataset.exception.DatasetException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * @author xausky
 */
public abstract class Action {
    private String prefilter = null;
    private String postfilter = null;

    public enum Type {
        SELECT, INSERT, UPDATE, DELETE, LANGUAGES, VEILDATA
    }

    public void setPrefilter(String prefilter) {
        this.prefilter = prefilter;
    }

    public void setPostfilter(String postfilter) {
        this.postfilter = postfilter;
    }

    public Object process(ExpressionParser parser, StandardEvaluationContext context, Map<String, Object> parameter) {
        Object result;
        if(prefilter != null){
            context.setVariable("request", parameter);
            if(Boolean.FALSE.equals(parser.parseExpression(prefilter).getValue(context, Boolean.class))){
                throw new DatasetException("dataset.prefilter.abort");
            }
        }
        result = invoke(parameter);
        if(postfilter != null){
            context.setVariable("request", parameter);
            if(Boolean.FALSE.equals(parser.parseExpression(postfilter).getValue(context, Boolean.class))){
                throw new DatasetException("dataset.postfilter.abort");
            }
        }
        return result;
    }

    /**
     * 执行方法
     * @param parameter
     * @return
     */
    protected abstract Object invoke(Map<String, Object> parameter);
}
/*
 * Copyright LKK Health Products Group Limited
 */

package io.choerodon.hap.intergration.controllers;

import io.choerodon.hap.intergration.annotation.HapInbound;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * cooperate with FeedJson , get the original post body before ResponseBody
 * works.
 *
 * @author shengyang.zhou@hand-china.com
 */
@ControllerAdvice
public class HapInvokeRequestBodyAdvice implements RequestBodyAdvice {

    private static final ThreadLocal<String> LAST_BODY = new ThreadLocal<>();

    private static final String apiClazz = "io.choerodon.hap.api.gateway.controllers.ApiInvokeContoller";

//    private static String inboundClazz = "HapInbound";

    /**
     * get the body and remove it.
     *
     * @return body
     */
    public static String getAndRemoveBody() {
        String body = LAST_BODY.get();
        LAST_BODY.remove();
        return body;
    }

    public static String getBody() {
        return LAST_BODY.get();
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        if (methodParameter.getContainingClass().getName().equals(apiClazz)) {
            return true;
        }
        Annotation[] anos = methodParameter.getMethodAnnotations();
        for (Annotation ano : anos) {
            if (ano instanceof HapInbound) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                  Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(final HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {

        try (InputStream is = inputMessage.getBody(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(is, baos);
            String body = new String(baos.toByteArray(), "UTF-8");
            LAST_BODY.set(body);
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            return new HttpInputMessage() {
                @Override
                public InputStream getBody() throws IOException {
                    return bais;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return inputMessage.getHeaders();
                }
            };
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}

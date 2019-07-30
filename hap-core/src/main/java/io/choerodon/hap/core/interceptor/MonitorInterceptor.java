package io.choerodon.hap.core.interceptor;

import io.choerodon.hap.account.dto.User;
import io.choerodon.hap.security.TokenUtils;
import io.choerodon.base.util.BaseConstants;
import io.choerodon.web.core.impl.RequestHelper;
import io.choerodon.web.util.TimeZoneUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @author njq.niu@hand-china.com
 * @since 2016年1月21日
 */
@Component
public class MonitorInterceptor extends HandlerInterceptorAdapter {

    private static ThreadLocal<Long> holder = new ThreadLocal<>();

    public static final ThreadLocal<Object> REST_INVOKE_HANDLER = new ThreadLocal<>();

    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";
    public static final String SESSION_ID = "sessionId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        fillMDC(request);
        holder.set(System.currentTimeMillis());
        HttpSession session = request.getSession(false);
        if (session != null) {
            String tz = (String) session.getAttribute(BaseConstants.PREFERENCE_TIME_ZONE);
            if (StringUtils.isNotEmpty(tz)) {
                TimeZoneUtils.setTimeZone(TimeZone.getTimeZone(tz));
            }
        }
        SecurityTokenInterceptor.LOCAL_SECURITY_KEY.set(TokenUtils.getSecurityKey(session));
        REST_INVOKE_HANDLER.set(handler);
        RequestHelper.setCurrentRequest(RequestHelper.createServiceRequest(request));
        return true;
    }

    private void fillMDC(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Long userId = (Long) session.getAttribute(User.FIELD_USER_ID);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            if (userId != null) {
                MDC.put(USER_ID, userId.toString());
            }
            MDC.put(REQUEST_ID, uuid);
            MDC.put(SESSION_ID, session.getId());
        }
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        long end = System.currentTimeMillis();
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            Logger logger = LoggerFactory.getLogger(method.getBeanType());
            if (logger.isTraceEnabled()) {
                logger.trace(method.toString() + " - " + (end - holder.get()) + " ms");
            }
        }
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        RequestHelper.clearCurrentRequest();
        holder.remove();
        SecurityTokenInterceptor.LOCAL_SECURITY_KEY.remove();
        REST_INVOKE_HANDLER.remove();
        removeMDC(USER_ID);
        removeMDC(REQUEST_ID);
        removeMDC(SESSION_ID);
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    }

    private void removeMDC(String key) {
        if (MDC.get(key) != null) {
            MDC.remove(key);
        }
    }
}

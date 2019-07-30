/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.core.web;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class HapEnhanceFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        HapServletRequestWrapper wrapper = new HapServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {

    }

    class HapServletRequestWrapper extends HttpServletRequestWrapper {

        private Map<String, String[]> extendMap = null;

        public HapServletRequestWrapper(HttpServletRequest request) {
            super(request);
            initExtendMap(request);
        }

        private void initExtendMap(ServletRequest request) {
            if (extendMap == null) {
                extendMap = new HashMap<>(request.getParameterMap());
                String[] pageSize = extendMap.get("pageSize");
                String[] pagesize = extendMap.get("pagesize");
                if (pagesize != null) {
                    extendMap.put("pageSize", pagesize);
                }
                if (pageSize != null) {
                    extendMap.put("pagesize", pageSize);
                }
                extendMap = Collections.unmodifiableMap(extendMap);
            }
        }

        @Override
        public String getParameter(String name) {
            String[] string = extendMap.get(name);
            if (string != null) {
                return string[0];
            }
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return extendMap;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(extendMap.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return extendMap.get(name);
        }
    }

}

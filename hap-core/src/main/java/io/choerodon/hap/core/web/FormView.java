package io.choerodon.hap.core.web;

import io.choerodon.hap.system.dto.Form;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import io.choerodon.freemarker.DefaultFreeMarkerView;
import io.choerodon.redis.Cache;
import io.choerodon.redis.CacheManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * @author njq.niu@hand-china.com
 */
public class FormView extends DefaultFreeMarkerView {

    protected final Logger logger = LoggerFactory.getLogger(FormView.class);

    private CacheManager cacheManager;
    private Cache<Form> formCache;

    @Override
    protected void initServletContext(ServletContext servletContext) throws BeansException {
        super.initServletContext(servletContext);
        this.cacheManager = (CacheManager) autodetectScreenTagFactory(CacheManager.class);
        formCache = cacheManager.getCache("form");
    }

    protected Object autodetectScreenTagFactory(Class<?> clazz) throws BeansException {
        try {
            return BeanFactoryUtils.beanOfTypeIncludingAncestors(getApplicationContext(), clazz, true, false);
        } catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Must define a single ViewTagFactory bean in this web application context!", ex);
        }
    }

    @Override
    protected void doRender(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Expose model to JSP tags (as request attributes).
        exposeModelAsRequestAttributes(model, request);
        // Expose all standard FreeMarker hash models.
        if (logger.isDebugEnabled()) {
            logger.debug("Rendering FreeMarker template [{}] in FreeMarkerView '{}'", getUrl(), getBeanName());
        }
        processTemplate(model, request, response);
    }

    protected void processTemplate(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SimpleHash fmModel = buildTemplateModel(model, request, response);
        try (Writer writer = response.getWriter()) {
            String name = getUrl();
            Boolean isPreview = false;
            if(name.indexOf("preview/") >= 0){
                isPreview = true;
                name = name.replace("preview/","");
            }
            Form form = null;
            if (name != null) {
                form = formCache.getValue(StringUtils.upperCase(StringUtils.substringBefore(name, ".form")));
            }
            if (form != null) {
                if(!isPreview && form.getIsPublish().equals("N")){
                    writer.write("Form not publish!");
                }else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<#include \"/include/header.html\">");
                    sb.append("<body>");
                    sb.append(form.getContent());
                    sb.append("</body></html>");
                    Template template = new Template(name, sb.toString(), getConfiguration());
                    template.process(fmModel, writer);
                }
            }else {
                writer.write("Form not found!");
            }
        } catch (Exception e) {
            if(logger.isErrorEnabled()){
                logger.error("Failed to parse screen template for URL [" + getUrl() + "]", e);
            }
            throw e;
        }
    }

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        String name = getUrl();
        return name.endsWith(".form");
    }
}

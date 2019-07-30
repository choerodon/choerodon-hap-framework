package io.choerodon.hap.attachment.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Locale;

/**附件上传
 * @author jinqin.ma@hand-china.com
 *
 */

public interface IAttachmentProvider {
	String getAttachListHtml(String sourceType, String sourceKey, Locale locale, String contextPath) throws Exception;

	String getAttachHtml(String sourceType, String sourceKey, Locale locale, String contextPath, boolean enableRemove,
                         boolean enableUpload) throws IOException, TemplateException;

	void setConfiguration(Configuration configuration);
}

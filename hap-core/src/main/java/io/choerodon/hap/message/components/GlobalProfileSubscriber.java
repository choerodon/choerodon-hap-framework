package io.choerodon.hap.message.components;

import io.choerodon.hap.core.AppContextInitListener;
import io.choerodon.hap.message.profile.GlobalProfileListener;
import io.choerodon.hap.message.profile.ListenerInitHandler;
import io.choerodon.hap.message.profile.SystemConfigListener;
import io.choerodon.hap.system.dto.GlobalProfile;
import io.choerodon.hap.system.service.IProfileService;
import io.choerodon.hap.system.service.ISysConfigService;
import io.choerodon.message.IMessageConsumer;
import io.choerodon.message.annotation.TopicMonitor;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.core.impl.RequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
@TopicMonitor(channel = {GlobalProfileSubscriber.PROFILE, GlobalProfileSubscriber.CONFIG})
public class GlobalProfileSubscriber implements IMessageConsumer<GlobalProfile>, ListenerInitHandler, AppContextInitListener {

    public static final String PROFILE = "profile";

    public static final String CONFIG = "config";

    private static Map<GlobalProfileListener, List<String>> listenerMap = new HashMap<>();

    @Autowired
    private IProfileService profileService;

    @Autowired
    private ISysConfigService configService;

    private Logger logger = LoggerFactory.getLogger(GlobalProfileSubscriber.class);

    public void addListener(GlobalProfileListener listener) {
        listenerMap.put(listener, listener.getAcceptedProfiles());
        initLoad(listener);
    }

    @Override
    public void initLoad(GlobalProfileListener listener) {
        if (logger.isDebugEnabled()) {
            logger.debug("initial load profile values for:" + listener);
        }
        // 初始化 系统配置
        if (listener instanceof SystemConfigListener) {
            for (String configCode : listener.getAcceptedProfiles()) {
                String configValue = configService.getConfigValue(configCode);
                if (StringUtil.isNotEmpty(configValue)) {
                    listener.updateProfile(configCode, configValue);
                }
            }
        } else {
            IRequest iRequest = RequestHelper.newEmptyRequest();
            iRequest.setUserId(-1L);
            iRequest.setRoleId(-1L);
            for (String profileName : listener.getAcceptedProfiles()) {
                String profileValue = profileService.getProfileValue(iRequest, profileName);
                if (StringUtil.isNotEmpty(profileValue)) {
                    listener.updateProfile(profileName, profileValue);
                }
            }
        }
    }

    @Override
    public void onMessage(GlobalProfile message, String pattern) {
        listenerMap.forEach((k, v) -> {
            // channel=config notify SystemConfig
            if (CONFIG.equalsIgnoreCase(pattern)) {
                if (k instanceof SystemConfigListener) {
                    if (v.contains(message.getProfileName())) {
                        k.updateProfile(message.getProfileName(), message.getProfileValue());
                    }
                }
            } else if (PROFILE.equalsIgnoreCase(pattern)) {
                if (!(k instanceof SystemConfigListener)) {
                    if (v.contains(message.getProfileName())) {
                        k.updateProfile(message.getProfileName(), message.getProfileValue());
                    }
                }
            }
        });
    }

    /**
     * find all GlobalProfileListener .
     */
    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, GlobalProfileListener> listeners = applicationContext.getBeansOfType(GlobalProfileListener.class);
        listeners.forEach((k, v) -> addListener(v));
    }

}

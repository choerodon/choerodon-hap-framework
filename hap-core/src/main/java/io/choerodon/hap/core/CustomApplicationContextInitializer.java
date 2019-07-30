package io.choerodon.hap.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

/**
 * @author njq.niu@hand-china.com
 */
public class CustomApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static Logger LOG = LoggerFactory.getLogger(CustomApplicationContextInitializer.class);

    private final static String PROPERTIES_FILE_LOAD_ORDER_FIRST = "FIRST";

    private final static String PROPERTIES_FILE_LOAD_ORDER_LAST = "LAST";

    /**
     * Reminder: Document this in the applications web.xml or other Application Container.
     */
    private final static String PROPERTIES_FILE_LOAD_ORDER_DEFAULT = PROPERTIES_FILE_LOAD_ORDER_FIRST;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        String propertiesFilePath = environment.getProperty("propertiesFile");
        String propertiesFileLoadOrder = getPropertiesFileLoadOrderFrom(environment);

        try {
            MutablePropertySources propertySources = environment.getPropertySources();
            if (propertiesFileLoadOrder.equalsIgnoreCase(PROPERTIES_FILE_LOAD_ORDER_LAST)) {
                propertySources.addLast(new ResourcePropertySource(propertiesFilePath));
            } else {
                propertySources.addFirst(new ResourcePropertySource(propertiesFilePath));
            }
            LOG.info(String.format("'%s' loaded into ApplicationContext with load order '%s'", propertiesFilePath, propertiesFileLoadOrder));
        } catch (IOException ioException) {
            LOG.info(String.format("Could not load '%s' with load order '%s' while trying to load into the ApplicationContext", propertiesFilePath, propertiesFileLoadOrder));
        }
    }

    private String getPropertiesFileLoadOrderFrom(final ConfigurableEnvironment environment) {
        String propertiesFileLoadOrder = environment.getProperty("propertiesFileLoadOrder");
        if (propertiesFileLoadOrder == null || propertiesFileLoadOrder.isEmpty()) {
            return PROPERTIES_FILE_LOAD_ORDER_DEFAULT;
        }
        if (!propertiesFileLoadOrder.equalsIgnoreCase(PROPERTIES_FILE_LOAD_ORDER_FIRST) &&
                !propertiesFileLoadOrder.equalsIgnoreCase(PROPERTIES_FILE_LOAD_ORDER_LAST)) {
            return PROPERTIES_FILE_LOAD_ORDER_DEFAULT;
        }
        return null;
    }
}

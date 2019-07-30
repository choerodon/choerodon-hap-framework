package io.choerodon.hap.core.components;

import io.choerodon.web.util.TimeZoneUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BeanUtils日期转换器.
 *
 * @author qiang.zeng
 */
@Component
public class CustomDateConverter implements Converter {
    private static final Logger logger = LoggerFactory.getLogger(CustomDateConverter.class);
    private static final String DATE = "yyyy-MM-dd";
    private static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }
        // Handle Date
        if (value instanceof java.util.Date) {
            return value;
        }
        // Handle Calendar
        if (value instanceof java.util.Calendar) {
            return ((java.util.Calendar) value).getTime();
        }
        if (value instanceof Long) {
            Long longObj = (Long) value;
            return type.cast(new Date(longObj));
        }
        if (value instanceof String) {
            String dateValue = StringUtils.trim((String) value);
            if (StringUtils.isEmpty(dateValue)) {
                return null;
            }
            int length = dateValue.length();
            SimpleDateFormat dateFormat = null;
            try {
                if (length <= 10) {
                    dateFormat = new SimpleDateFormat(DATE);
                } else if (length <= 19) {
                    dateFormat = new SimpleDateFormat(DATETIME);
                } else if (length <= 23) {
                    dateFormat = new SimpleDateFormat(TIMESTAMP);
                } else {
                    logger.error("Unsupported date format");
                    return null;
                }
                dateFormat.setTimeZone(TimeZoneUtils.getTimeZone());
                return dateFormat.parse(dateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}

package io.choerodon.hap.core.util;

import java.util.UUID;

/**
 * ID生成器.
 *
 * @author njq.niu@hand-china.com
 */
public class IDGenerator {

    // Singleton
    private static final IDGenerator instance = new IDGenerator();

    public static IDGenerator getInstance() {
        return instance;
    }

    public String generate() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

}

/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.extensible.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class DtoExtension {

    public static final String TARGET ="target";

    public static final String EXTENSION ="extension";

    private String target;

    private String extension;

    private List<ExtendedField> extendedFields = new ArrayList<>();

    public String getTarget() {
        return target;
    }

    public void setTarget(String extendOn) {
        this.target = extendOn;
    }


    public String getExtension() {
        return extension;
    }

    public void setExtension(String interfaceAdd) {
        this.extension = interfaceAdd;
    }

    public List<ExtendedField> getExtendedFields() {
        return extendedFields;
    }

    public void setExtendedFields(List<ExtendedField> extendedFields) {
        this.extendedFields = extendedFields;
    }
}

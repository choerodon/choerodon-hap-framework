package io.choerodon.hap.generator.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jialong.zuo@hand-china.com on 2016/10/24.
 */
public class DBTable {
    private String name;
    private List<DBColumn> columns;
    private boolean isMultiLanguage = false;

    public DBTable() {
        columns = new ArrayList<DBColumn>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMultiLanguage() {
        return isMultiLanguage;
    }

    public void setMultiLanguage(boolean multiLanguage) {
        isMultiLanguage = multiLanguage;
    }

    public List<DBColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DBColumn> columns) {
        this.columns = columns;
    }

}

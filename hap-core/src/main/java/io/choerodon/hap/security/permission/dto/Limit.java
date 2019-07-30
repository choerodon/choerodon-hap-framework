package io.choerodon.hap.security.permission.dto;

/**
 * @author jialong.zuo@hand-china.com
 * @since 2017/9/4.
 */
public class Limit extends net.sf.jsqlparser.statement.select.Limit {

    private net.sf.jsqlparser.statement.select.Limit limit;

    public Limit(net.sf.jsqlparser.statement.select.Limit limit) {
        this.limit = limit;
    }

    @Override
    public long getOffset() {
        return limit.getOffset();
    }

    @Override
    public long getRowCount() {
        return limit.getRowCount();
    }

    @Override
    public void setOffset(long l) {
        limit.setOffset(l);
    }

    @Override
    public void setRowCount(long l) {
        limit.setRowCount(l);
    }

    @Override
    public boolean isOffsetJdbcParameter() {
        return limit.isOffsetJdbcParameter();
    }

    @Override
    public boolean isRowCountJdbcParameter() {
        return limit.isRowCountJdbcParameter();
    }

    @Override
    public void setOffsetJdbcParameter(boolean b) {
        limit.setOffsetJdbcParameter(b);
    }

    @Override
    public void setRowCountJdbcParameter(boolean b) {
        limit.setRowCountJdbcParameter(b);
    }

    @Override
    public boolean isLimitAll() {
        return isLimitAll();
    }

    @Override
    public void setLimitAll(boolean b) {
        setLimitAll(b);
    }

    @Override
    public boolean isLimitNull() {
        return limit.isLimitNull();
    }

    @Override
    public void setLimitNull(boolean b) {
        limit.setLimitNull(b);
    }

    @Override
    public String toString() {
        String retVal = "";
        if (isLimitNull()) {
            retVal += " LIMIT NULL";
        } else if (getRowCount() >= 0 || isRowCountJdbcParameter()) {
            retVal += " LIMIT " + (isRowCountJdbcParameter() ? "?" : Long.toString(getRowCount()));
        }

        if (getOffset() == 0 && isOffsetJdbcParameter()) {
            retVal += ",?";
        } else if (getOffset() > 0 || isOffsetJdbcParameter()) {
            retVal += " OFFSET " + (isOffsetJdbcParameter() ? "?" : Long.toString(getOffset()));
        }
        return retVal;
    }
}

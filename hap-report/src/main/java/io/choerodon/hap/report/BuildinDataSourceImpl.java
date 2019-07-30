package io.choerodon.hap.report;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 报表内置数据源.
 *
 * @author qiang.zeng
 * @since 2017/9/21
 */
@Component
public class BuildinDataSourceImpl implements BuildinDatasource {
    @Autowired
    private DataSource dataSource;

    @Override
    public String name() {
        return "HAP Built-in DataSource";
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

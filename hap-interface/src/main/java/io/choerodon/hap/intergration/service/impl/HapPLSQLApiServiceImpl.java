/**
 * Copyright (c) 2016. Hand Enterprise Solution Company. All right reserved. Project
 * Name:hstaffParent Package Name:hstaff.core.rest.v2.service.impl Date:2016/7/8 0008 Create
 * By:zongyun.zhou@hand-china.com
 */
package io.choerodon.hap.intergration.service.impl;

import io.choerodon.hap.intergration.beans.HapJDBCSqlSessionFactory;
import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.service.IHapApiService;
import io.choerodon.hap.intergration.util.JSONHelper;
import net.sf.json.JSONObject;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Iterator;

@Service(value = "plsqlBean")
public class HapPLSQLApiServiceImpl implements IHapApiService {
    @Autowired
    private HapJDBCSqlSessionFactory hapSqlSessionFactory;
    private Logger logger = LoggerFactory.getLogger(HapPLSQLApiServiceImpl.class);

    @Override
    public JSONObject invoke(HapInterfaceHeader headerAndLineDTO, JSONObject inbound) {
        // 如果需要使用mapper，那么就需要调用mapper将inbound对象映射成所需要的对象，mapper可以自定义开发
        JSONObject jsonResp;
        int index = headerAndLineDTO.getIftUrl().lastIndexOf(".");
        String url = headerAndLineDTO.getIftUrl();

        String packageName = url.substring(0, index);
        String functionName = url.substring(index + 1);

        try {
            JSONObject params = (JSONObject) inbound.get("params");
            logger.info("packageName:{}  functionName:{} ", packageName, functionName);
            logger.info("inbound params is:" + params.toString());
            String executeSql = "";
            if (params != null) {
                executeSql = "{?=call " + url + "(";
                Iterator<String> keys = params.keys();
                while (keys.hasNext()) {
                    String str = keys.next();
                    executeSql += str + "=>'" + params.get(str) + "',";
                }
                executeSql = executeSql.substring(0, executeSql.length() - 1);
                executeSql += ")}";
            } else {
                executeSql = "{?=call " + url + "}";
            }
            logger.info("executeSql:{} ", executeSql);
            // 访问数据库
            // 调用oracle package
            Object resp = hapSqlSessionFactory.getJdbcTemplateObject().execute(executeSql,
                    new CallableStatementCallback() {
                        public Object doInCallableStatement(CallableStatement cs)
                                throws SQLException, DataAccessException {
                            cs.registerOutParameter(1, OracleTypes.CLOB);
                            cs.execute();
                            return this.clobToString(cs.getClob(1));// 获取输出参数的值
                        }

                        private String clobToString(Clob clob) {
                            if (clob == null) {
                                return null;
                            }
                            try {
                                Reader inStreamDoc = clob.getCharacterStream();

                                char[] tempDoc = new char[(int) clob.length()];
                                inStreamDoc.read(tempDoc);
                                inStreamDoc.close();
                                return new String(tempDoc);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (SQLException es) {
                                es.printStackTrace();
                            }
                            return null;
                        }
                    });
            String respStr = resp.toString().replaceAll("null", "\"\"");
            jsonResp = JSONObject.fromObject(respStr);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            jsonResp = JSONHelper.getErrorJSONObject("");
        }
        return jsonResp;
    }
}

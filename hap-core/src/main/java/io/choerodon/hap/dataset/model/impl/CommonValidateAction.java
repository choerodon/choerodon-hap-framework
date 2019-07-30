package io.choerodon.hap.dataset.model.impl;

import io.choerodon.base.helper.ApplicationContextHelper;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.model.Action;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonValidateAction extends Action {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();
    private String statement;
    public CommonValidateAction(String name, String tableName, Set<String> columns, Configuration configuration) {
        this.statement = "dataset." + name + ".validate";
        StringBuilder xml = new StringBuilder();
        xml.append("<script>");
        xml.append("SELECT COUNT(*) FROM ");
        xml.append(tableName);
        xml.append("<trim prefix=\" WHERE \" suffixOverrides=\"AND\">");
        for (String column : columns) {
            xml.append("<if test=\"");
            xml.append(StringUtil.underlineToCamelhump(column.toLowerCase()));
            xml.append(" != null \">");
            xml.append(column);
            xml.append("=");
            xml.append("#{");
            xml.append(StringUtil.underlineToCamelhump(column.toLowerCase()));
            xml.append('}');
            xml.append(" AND ");
            xml.append("</if>");
        }
        xml.append("</trim></script>");
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, statement,
                languageDriver.createSqlSource(configuration, xml.toString(), null), SqlCommandType.SELECT);
        builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, statement, Map.class, Collections.emptyList()).build()));
        configuration.addMappedStatement(builder.build());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object invoke(Map<String, Object> parameter){
        SqlSession session = ApplicationContextHelper.getApplicationContext().getBean(SqlSession.class);
        List<Boolean> result = new ArrayList<>();
        try {
            if (parameter.get("unique") instanceof List) {
                List<Map<String, String>> uniqueGroups = ((List) parameter.get("unique"));
                for (Map<String, String> uniqueGroup : uniqueGroups){
                    Object object = session.selectOne(statement, uniqueGroup);
                    if(((Map<String, Long>)object).get("COUNT(*)").equals(0L)){
                        result.add(Boolean.TRUE);
                    } else {
                        result.add(Boolean.FALSE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatasetException("dataset.error", e);
        }
        return result;
    }
}

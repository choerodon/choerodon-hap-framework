package io.choerodon.hap.flexfield.service;

import io.choerodon.hap.core.ProxySelf;
import io.choerodon.hap.flexfield.dto.FlexRule;
import io.choerodon.mybatis.service.IBaseService;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IFlexRuleService extends IBaseService<FlexRule>, ProxySelf<IFlexRuleService> {

    /**
     * 匹配规则.
     *
     * @param ruleSetCode 规则集代码
     * @param model       进行匹配的数据
     * @param iRequest    IRequest环境
     * @return 匹配成功的弹性域
     */
    ResponseData matchingRule(String ruleSetCode, Set<Map.Entry<String, String>> model, IRequest iRequest);

    /**
     * 删除弹性域规则.
     *
     * @param flexRules 弹性域规则列表
     */
    void deleteRule(List<FlexRule> flexRules);

    /**
     * 获取弹性域LOV字段相关值放入dto.
     *
     * @param ruleSetCode 规则集代码
     * @param model       进行匹配的数据
     * @param o           当前的数据将查询到的LOV值放入该数据中
     * @param iRequest    IRequest环境
     * @throws IllegalAccessException    IllegalAccessException
     * @throws IOException               IOException
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException     NoSuchMethodException
     */
    void matchingLovField(String ruleSetCode, Set<Map.Entry<String, String>> model, Object o, IRequest iRequest) throws IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException;

}
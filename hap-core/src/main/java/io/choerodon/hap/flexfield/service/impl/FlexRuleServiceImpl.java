package io.choerodon.hap.flexfield.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import io.choerodon.hap.flexfield.dto.FlexRule;
import io.choerodon.hap.flexfield.dto.FlexRuleDetail;
import io.choerodon.hap.flexfield.dto.FlexRuleField;
import io.choerodon.hap.flexfield.dto.WarpFlexRuleField;
import io.choerodon.hap.flexfield.mapper.FlexRuleDetailMapper;
import io.choerodon.hap.flexfield.mapper.FlexRuleFieldMapper;
import io.choerodon.hap.flexfield.mapper.FlexRuleMapper;
import io.choerodon.hap.flexfield.service.IFlexRuleService;
import io.choerodon.hap.generator.service.impl.FileUtil;
import io.choerodon.hap.system.dto.DTOStatus;
import io.choerodon.hap.util.dto.Lov;
import io.choerodon.hap.util.service.ILovService;
import io.choerodon.mybatis.service.BaseServiceImpl;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import jodd.util.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Dataset("FlexRule")
public class FlexRuleServiceImpl extends BaseServiceImpl<FlexRule> implements IFlexRuleService, IDatasetService<FlexRule> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private FlexRuleMapper ruleMapper;

    @Autowired
    private io.choerodon.hap.flexfield.mapper.FlexRuleFieldMapper fieldMapper;

    @Autowired
    private FlexRuleDetailMapper flexRuleDetailMapper;

    @Autowired
    private FlexRuleFieldMapper flexRuleFieldMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ILovService lovService;

    @Override
    public ResponseData matchingRule(String ruleSetCode, Set<Map.Entry<String, String>> model, IRequest iRequest) {
        List<FlexRule> flexRules = ruleMapper.matchingRule(ruleSetCode);
        Iterator iterator = flexRules.iterator();
        FlexRule baseFlex = null;
        while (iterator.hasNext()) {
            FlexRule fl = (FlexRule) iterator.next();
            if (fl.getRuleCode().equals("_GLOBAL_FLEX_RULE")) {
                baseFlex = fl;
                iterator.remove();
                break;
            }
        }


        if (!flexRules.isEmpty()) {
            FlexRule flexRule = flexRules.stream()
                    .filter(flexRule1 -> matching(flexRule1, model))
                    .max(Comparator.comparing(flexRule2 -> flexRule2.getFlexRuleDetailList().size())).orElseGet(() -> new FlexRule());

            if (null != flexRule.getRuleId()) {
                FlexRuleField flexRuleField = new FlexRuleField();
                flexRuleField.setRuleId(flexRule.getRuleId());
                List<FlexRuleField> flexRuleFields = fieldMapper.queryFlexField(flexRuleField);
                if (null != baseFlex) {
                    flexRuleField.setRuleId(baseFlex.getRuleId());
                    flexRuleFields.addAll(fieldMapper.queryFlexField(flexRuleField));
                }
                return new ResponseData(classify(flexRuleFields));
            }
        } else if (null != baseFlex) {
            FlexRuleField flexRuleField = new FlexRuleField();
            flexRuleField.setRuleId(baseFlex.getRuleId());
            return new ResponseData(classify(fieldMapper.queryFlexField(flexRuleField)));
        }
        return new ResponseData();
    }

    public void matchingLovField(String ruleSetCode, Set<Map.Entry<String, String>> model, Object o, IRequest iRequest) throws IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException {
        List<FlexRule> flexRules = ruleMapper.matchingRule(ruleSetCode);
        if (!flexRules.isEmpty()) {
            FlexRule flexRule = flexRules.stream()
                    .filter(flexRule1 -> matching(flexRule1, model))
                    .max(Comparator.comparing(flexRule2 -> flexRule2.getFlexRuleDetailList().size())).orElseGet(() -> new FlexRule());
            if (null != flexRule.getRuleId()) {
                FlexRuleField flexRuleField = new FlexRuleField();
                flexRuleField.setRuleId(flexRule.getRuleId());
                List<FlexRuleField> fields = fieldMapper.queryFlexField(flexRuleField);
                for (FlexRuleField field : fields) {
                    getLovField(field, o, iRequest);
                }
            }

        }
    }

    public void getLovField(FlexRuleField flexRuleField, Object o, IRequest iRequest) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ObjectNode description = objectMapper.readValue(flexRuleField.getFieldType(), ObjectNode.class);

        if ("LOV".equals(description.get("type").asText())) {
            String lovCode = description.get("conditionFieldLovCode").asText();
            String text = description.get("conditionFieldSelectTf").asText();
            text = text.substring(text.indexOf(".") + 1);
            String objectValue = BeanUtils.getProperty(o, FileUtil.columnToCamel(flexRuleField.getColumnName()));
            if (null != objectValue) {
                Lov lov = new Lov();
                lov.setCode(lovCode);
                lov = lovService.select(lov, 1, 5).get(0);
                Map map = new HashMap();
                map.put(lov.getValueField(), objectValue);
                List<?> list = lovService.selectDatas(lovCode, map, 1, 10);
                BaseDTO dto = (BaseDTO) o;
                if (list.size() != 0) {
                    Object o1 = list.get(0);
                    String val = BeanUtils.getProperty(o1, lov.getTextField());
                    dto.setAttribute(text, val);
                } else {
                    dto.setAttribute(text, "");
                }

            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(List<FlexRule> flexRules) {
        for (FlexRule flexRule : flexRules) {
            FlexRuleDetail ruleDetail = new FlexRuleDetail();
            FlexRuleField ruleField = new FlexRuleField();
            ruleField.setRuleId(flexRule.getRuleId());
            ruleDetail.setRuleId(flexRule.getRuleId());
            flexRuleDetailMapper.delete(ruleDetail);
            flexRuleFieldMapper.delete(ruleField);
            int updateCount = ruleMapper.delete(flexRule);
            checkOvn(updateCount, flexRule);
        }
    }

    private List<WarpFlexRuleField> classify(List<FlexRuleField> flexRuleFields) {
        List<WarpFlexRuleField> warpFlexRuleFields = new ArrayList<>();
        flexRuleFields.stream()
                .collect(Collectors.groupingBy(FlexRuleField::getFieldColumnNumber))
                .forEach((k, v) -> {
                    WarpFlexRuleField warpFlexRuleField = new WarpFlexRuleField();
                    // warpFlexRuleField.setLocal(local);
                    warpFlexRuleFields.add(warpFlexRuleField.warpField(v));
                });
        return warpFlexRuleFields;
    }

    public static void setPrompt(WarpFlexRuleField warpFlexRuleField, Locale locale, MessageSource messageSource) {
        warpFlexRuleField.getFields().forEach(v -> {
            doSetPrompt(v, locale, messageSource);
        });
    }

    private static void doSetPrompt(FlexRuleField flexRuleField, Locale locale, MessageSource messageSource) {

        try {
            ObjectNode json = (ObjectNode) OBJECT_MAPPER.readTree(flexRuleField.getFieldType());
            String name = messageSource.getMessage(json.get("labelName").toString(), null, locale);
            if (!StringUtil.isEmpty(name)) {
                json.put("labelName", name);
                flexRuleField.setFieldType(json.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean matching(FlexRule flexRule, Set<Map.Entry<String, String>> model) {
        boolean result = false;
        List<FlexRuleDetail> flexRuleDetails = flexRule.getFlexRuleDetailList();
        int sameNumber = 0;
        for (FlexRuleDetail flexRuleDetail1 : flexRuleDetails) {
            Iterator iter = model.iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (null == entry.getValue()) {
                    continue;
                }
                if (flexRuleDetail1.getFieldValue().equalsIgnoreCase(entry.getValue().toString()) && flexRuleDetail1.getFieldName().equalsIgnoreCase(entry.getKey().toString())) {
                    sameNumber++;
                    break;
                }
            }
        }
        if (sameNumber == flexRuleDetails.size()) {
            result = true;
        }
        return result;
    }

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        try {
            FlexRule flexRule = new FlexRule();
            BeanUtils.populate(flexRule, body);
            return select(flexRule, page, pageSize);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DatasetException("dataset.error.FlexRule", e);
        }
    }

    @Override
    public List<FlexRule> mutations(List<FlexRule> objs) {
        for (FlexRule flexRule : objs) {
            switch (flexRule.get__status()) {
                case DTOStatus.ADD:
                    insertSelective(flexRule);
                    break;
                case DTOStatus.UPDATE:
                    updateByPrimaryKeySelective(flexRule);
                    break;
                case DTOStatus.DELETE:
                    deleteByPrimaryKey(flexRule);
                    break;
            }
        }
        return objs;
    }
}
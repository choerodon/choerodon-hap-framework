package io.choerodon.hap.mail.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

/**
 * 消息模板.
 *
 * @author qiang.zeng@hand-china.com
 */
@Table(name = "sys_message_template")
public class MessageTemplate extends BaseDTO {

    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_ACCOUNT_ID = "accountId";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TEMPLATE_TYPE = "templateType";
    public static final String FIELD_PRIORITY_LEVEL = "priorityLevel";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_MEANING = "meaning";
    public static final String FIELD_SEND_TYPE = "sendType";
    public static final String FIELD_USER_NAME = "userName";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;

    private Long accountId;

    @Length(max = 50)
    @NotEmpty
    private String templateCode;

    @Length(max = 240)
    private String description;

    @Length(max = 50)
    private String templateType;

    @Length(max = 50)
    private String priorityLevel;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @Transient
    private String meaning;

    @Length(max = 50)
    private String sendType;

    @Transient
    private String userName;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = StringUtils.trim(templateCode);
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
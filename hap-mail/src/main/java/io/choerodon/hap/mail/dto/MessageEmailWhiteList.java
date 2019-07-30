package io.choerodon.hap.mail.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 邮件白名单.
 *
 * @author Clerifen Li
 */
@Table(name = "sys_message_email_white_lt")
public class MessageEmailWhiteList extends BaseDTO {

    private static final long serialVersionUID = 3293370176048833707L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 240)
    private String description;

    @Length(max = 240)
    private String address;

    private Long configId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

}
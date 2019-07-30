package io.choerodon.hap.system.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 系统的全局配置
 *
 * @author hailin.xu@hand-china.com 
 */

@Table(name = "sys_config")
public class SysConfig extends BaseDTO {

	private static final long serialVersionUID = 1L;
	
	/**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configId;

    /**
     * 配置名称.
     */
	@Length(max=240)
    private String configCode;

    /**
     * 配置值.
     */
	@Length(max=240)
    private String configValue;

    /**
     * 分类.
     */
	@Length(max=240)
    private String category;

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public String getConfigCode() {
		return configCode;
	}

	public void setConfigCode(String configCode) {
	   this.configCode = configCode == null ? null : configCode.trim();

	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue == null ? null : configValue.trim();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category == null ? null : category.trim();
	}



}

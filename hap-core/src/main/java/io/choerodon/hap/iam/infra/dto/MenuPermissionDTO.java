package io.choerodon.hap.iam.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author superlee
 * @since 2019-04-23
 */
@Table(name = "iam_menu_permission")
public class MenuPermissionDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long menuCode;
    private String permissionCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(Long menuCode) {
        this.menuCode = menuCode;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
}

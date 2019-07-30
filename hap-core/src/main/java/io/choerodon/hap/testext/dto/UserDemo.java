/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.testext.dto;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author shengyang.zhou@hand-china.com
 */
@MultiLanguage
@Table(name = "sys_user_demo_b")
public class UserDemo extends BaseDTO {
    public static final String USER_ID = "userId";
    public static final String USER_CODE = "userCode";
    public static final String USER_NAME = "userName";
    public static final String USER_AGE = "userAge";
    public static final String USER_SEX = "userSex";
    public static final String USER_BIRTH = "userBirth";
    public static final String USER_EMAIL = "userEmail";
    //public static final String USER_PHONE = "userPhone";
    public static final String ENABLE_FLAG = "enableFlag";
    public static final String DESCRIPTION = "description";
    public static final String ROLE_ID = "roleId";
    public static final String ROLE_NAME = "roleName";
    public static final String START_ACTIVE_TIME = "startActiveTime";
    //public static final String END_ACTIVE_TIME = "endActiveTime";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getUserId() {
        return innerGet(USER_ID);
    }

    public void setUserId(Long userId) {
        innerSet(USER_ID, userId);
    }

    public String getUserCode() {
        return innerGet(USER_CODE);
    }

    public void setUserCode(String userCode) {
        innerSet(USER_CODE, userCode);
    }

    @MultiLanguageField
    public String getUserName() {
        return innerGet(USER_NAME);
    }

    public void setUserName(String userName) {
        innerSet(USER_NAME, userName);
    }

    public String getUserSex() {
        return innerGet(USER_SEX);
    }

    public void setUserSex(String sex) {
        innerSet(USER_SEX, sex);
    }

    public Integer getUserAge() {
        return innerGet(USER_AGE);
    }

    public void setUserAge(Integer userAge) {
        innerSet(USER_AGE, userAge);
    }

    public Date getUserBirth() {
        return innerGet(USER_BIRTH);
    }

    public void setUserBirth(Date userBirth) {
        innerSet(USER_BIRTH, userBirth);
    }

    public String getUserEmail() {
        return innerGet(USER_EMAIL);
    }

    public void setUserEmail(String userEmail) {
        innerSet(USER_EMAIL, userEmail);
    }

    //    public String getUserPhone() {
    //        return innerGet(USER_PHONE);
    //    }
    //
    //    public void setUserPhone(String userPhone) {
    //        innerSet(USER_PHONE, userPhone);
    //    }

    @MultiLanguageField
    public String getDescription() {
        return innerGet(DESCRIPTION);
    }

    public void setDescription(String description) {
        innerSet(DESCRIPTION, description);
    }

    public String getEnableFlag() {
        return innerGet(ENABLE_FLAG);
    }

    public void setEnableFlag(String enableFlag) {
        innerSet(ENABLE_FLAG, enableFlag);
    }

    public Long getRoleId() {
        return innerGet(ROLE_ID);
    }

    public void setRoleId(Long roleId) {
        innerSet(ROLE_ID, roleId);
    }

    public String getRoleName() {
        return innerGet(ROLE_NAME);
    }

    public void setRoleName(String roleName) {
        innerSet(ROLE_NAME, roleName);
    }

    public Date getStartActiveTime() {
        return innerGet(START_ACTIVE_TIME);
    }

    public void setStartActiveTime(Date date) {
        innerSet(START_ACTIVE_TIME, date);
    }
}

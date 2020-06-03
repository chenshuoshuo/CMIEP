package com.lqkj.web.cmiep.modules.manager.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户
 */
//@Cacheable
@ApiModel(value = "用户")
@Entity
@Table(name = "manage_user",schema = "cmiep")
public class ManageUser implements Serializable, UserDetails {

    @ApiModelProperty(value = "账号id")
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @ApiModelProperty(value = "学校ID")
    @Column(name = "school_id")
    private Integer schoolId;

    @NotBlank(message = "用户Code不能为空")
    @ApiModelProperty(value = "用户Code")
    @Column(name = "user_code")
    private String userCode;

    @ApiModelProperty(value = "账号名")
    @Column(name = "user_name")
    private String userName;

    @ApiModelProperty(value = "密码")
    @Column(name = "pass_word")
    private String passWord;

    @ApiModelProperty(value = "oauth2.0登录id")
    @Column(name = "open_id")
    private String openId;

    @ApiModelProperty(value = "cas登录凭证")
    @Column(name = "cas_ticket")
    private String casTicket;

    @ApiModelProperty(value = "用户角色")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "manage_user_role",schema = "cmiep",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rule_id", referencedColumnName = "rule_id")
    )
    private Set<ManageRole> rules;

    @ApiModelProperty(value = "用户群体")
    @Column(name = "user_group")
    @Enumerated(EnumType.STRING)
    private UserGroupType userGroup;

    @ApiModelProperty(value = "更新时间")
    @Column(name = "update_time")
    @UpdateTimestamp
    private Timestamp updateTime;

    @ApiModelProperty("是否允许登录后台")
    @Column(name = "is_admin")
    private Boolean isAdmin;

    @ApiModelProperty("头像保存路径")
    @Column(name = "head_path")
    private String headPath;

    @ApiModelProperty("头像保存路径")
    private String headUrl;

    public UserGroupType getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroupType userGroup) {
        this.userGroup = userGroup;
    }

    public Set<ManageRole> getRules() {
        return rules;
    }

    public void setRules(Set<ManageRole> rules) {
        this.rules = rules;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getCasTicket() {
        return casTicket;
    }

    public void setCasTicket(String casTicket) {
        this.casTicket = casTicket;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public Collection<ManageResource> getAuthorities() {
        Set<ManageResource> authorities = new HashSet<>();

        if (rules==null) {
            return authorities;
        }

        for (ManageRole rule : this.rules) {
            for (ManageResource authority : rule.getResources()) {
                if (authority.getEnabled()) authorities.add(authority);
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        if (passWord!=null) {
            return passWord;
        } else {
            return casTicket;
        }
    }

    @Override
    public String getUsername() {
        return userCode;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum UserGroupType {
        student, teacher, staff, guest, teacher_staff
    }
}

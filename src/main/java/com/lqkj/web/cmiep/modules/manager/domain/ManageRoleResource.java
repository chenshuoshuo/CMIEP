package com.lqkj.web.cmiep.modules.manager.domain;

import javax.persistence.*;

/**
 * @ClassName CcrRuleToAuthorityEntity
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/23 17:17
 * @Version 1.0
 **/
@Entity
@IdClass(PrimaryKey.class)
@Table(name = "manage_role_resource",schema = "cmiep")
public class ManageRoleResource {

    /***
     * 角色ID
     **/
    @Id
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    /***
     * 权限ID
     **/
    @Id
    @Column(name = "authority_id", nullable = false)
    private Long authorityId;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }
}

package com.lqkj.web.cmiep.modules.manager.domain;

import java.io.Serializable;

/**
 * @ClassName PrimaryKey
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/23 17:23
 * @Version 1.0
 **/

public class PrimaryKey implements Serializable {
    /***
     * 角色ID
     **/
    private Long ruleId;
    /***
     * 权限ID
     **/
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

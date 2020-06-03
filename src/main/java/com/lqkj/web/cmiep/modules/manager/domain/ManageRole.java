package com.lqkj.web.cmiep.modules.manager.domain;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
 * 用户角色
 */
//@Cacheable
@Entity
@Table(name = "manage_role",schema = "cmiep")
public class ManageRole implements Serializable {

    @Id
    @Column(name = "rule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "manage_role_resource",schema = "cmiep",
            joinColumns = @JoinColumn(name = "rule_id", referencedColumnName = "rule_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "authority_id")
    )
    private Set<ManageResource> resources;

    @UpdateTimestamp
    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column
    private String name;

    @Column
    private String content;

    @Column(name = "school_id")
    private Integer schoolId;

    public ManageRole() {
    }

    public ManageRole(Set<ManageResource> resources, String name, String content) {
        this.resources = resources;
        this.name = name;
        this.content = content;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Integer schoolId) {
        this.schoolId = schoolId;
    }

    public Set<ManageResource> getResources() {
        return resources;
    }

    public void setResources(Set<ManageResource> resources) {
        this.resources = resources;
    }
}

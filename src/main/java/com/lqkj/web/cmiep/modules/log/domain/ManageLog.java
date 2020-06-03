package com.lqkj.web.cmiep.modules.log.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

/**
 * 系统日志
 */
@Entity
@Table(name = "manage_log",schema = "cmiep")
public class ManageLog implements Serializable {

    @Id
    @Column(name = "log_id")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID logId;

    @Column(name = "user_name")
    private String userName;

    @Column
    private String source;

    @Column
    private String method;

    @Column
    private String description;

    @Column
    private String rule;

    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;

    public ManageLog() {
        this.logId = UUID.randomUUID();
    }

    public ManageLog(String source, String method, String description) {
        this.logId = UUID.randomUUID();
        this.source = source;
        this.method = method;
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UUID getLogId() {
        return logId;
    }

    public void setLogId(UUID logId) {
        this.logId = logId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManageLog that = (ManageLog) o;
        return Objects.equals(logId, that.logId) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(source, that.source) &&
                Objects.equals(method, that.method) &&
                Objects.equals(description, that.description) &&
                Objects.equals(rule, that.rule) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId, userName, source, method, description, rule, createTime);
    }
}

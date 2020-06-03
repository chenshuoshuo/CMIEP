package com.lqkj.web.cmiep.modules.manager.dao;


import com.lqkj.web.cmiep.modules.manager.domain.ManageRoleResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ManageRoleResourceRepository extends JpaRepository<ManageRoleResource, Long> {
    @Modifying
    @Query(value="DELETE from cmiep.manage_role_resource as crta where crta.rule_id=:ruleId", nativeQuery = true)
    void deleteByRuleId(@Param("ruleId") Long ruleId);
}

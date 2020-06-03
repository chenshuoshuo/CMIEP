package com.lqkj.web.cmiep.modules.manager.dao;

import com.lqkj.web.cmiep.modules.manager.domain.ManageResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManageResourceRepository extends JpaRepository<ManageResource, Long> {

    @Query(nativeQuery = true, value = "select a.* from manage_resource a" +
            " inner join ccr_rule_to_authority ra on a.authority_id = ra.authority_id" +
            " inner join ccr_user_rule r on ra.rule_id = r.rule_id" +
            " inner join ccr_user_to_rule ur on r.rule_id = ur.rule_id" +
            " inner join ccr_user u on ur.user_id = u.user_id" +
            " where u.user_code = :userCode and a.type = :t and a.enabled = true and a.manage = true group by a.authority_id")
    List<ManageResource> findByManageType(@Param("userCode") String userCode,
                                             @Param("t") String type);

    @Query(nativeQuery = true, value = "select a.* from manage_resource a" +
            " inner join ccr_rule_to_authority ra on a.authority_id = ra.authority_id" +
            " inner join ccr_user_rule r on ra.rule_id = r.rule_id" +
            " inner join ccr_user_to_rule ur on r.rule_id = ur.rule_id" +
            " inner join ccr_user u on ur.user_id = u.user_id" +
            " where u.user_code = :userCode and a.type = :t and a.enabled = true group by a.authority_id")
    List<ManageResource> findByType(@Param("userCode") String userCode, @Param("t") String type);


    @Query("select a.name from ManageResource a where upper(a.content)=upper(:content) ")
    String findNameByContent(@Param("content") String content);

    @Modifying
    @Query("update ManageResource a set a.enabled=:enabled where a.authorityId=:authorityId")
    void updateChildState(@Param("authorityId") Long authorityId,
                          @Param("enabled") Boolean enabled);

    @Query(nativeQuery = true, value = "select a.* from cmiep.manage_resource a" +
            " inner join cmiep.manage_role_resource ra on a.authority_id = ra.authority_id " +
            " inner join cmiep.manage_role r on ra.rule_id = r.rule_id " +
            " inner join cmiep.manage_user_role ur on r.rule_id = ur.rule_id " +
            " inner join cmiep.manage_user u on ur.user_id = u.user_id " +
            " where u.user_code=:userName and a.name like :keyword group by a.authority_id")
    Page<ManageResource> findSupportAuthority(@Param("userName") String userName,
                                                 @Param("keyword") String keyword,
                                                 Pageable pageable);
    @Query("select a from ManageResource a where a.parentId=:id")
    List<ManageResource> queryChildAuth(Long id);

//    @Query(value = "select * from ccr_user_authority where target_user_role && ARRAY[?2,'public'] \\:\\:varchar[] or specify_user_id && ARRAY[?1] \\:\\:varchar[] group by authority_id",nativeQuery = true)
//   // @Query("select ua from CcrUserAuthority ua where ua.targetUserRole in ?2 or ua.specifyUserId in ?1 group by ua.authorityId")
//    List<CcrUserAuthority> listQuery(String userId,String roles);
}


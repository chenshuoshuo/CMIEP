package com.lqkj.web.cmiep.modules.manager.dao;

import com.lqkj.web.cmiep.modules.manager.domain.ManageUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface ManageUserRepository extends JpaRepository<ManageUser, Long> {

    @QueryHints(value = {
            @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")
    })
    @Query("select u from ManageUser u where u.userCode=:name")
    ManageUser findByUserName(@Param("name") String name);

    @Query("select u.userGroup,count(u) from ManageUser u group by u.userGroup order by u.userGroup")
    List<Object[]> userStatistics();
    
    /**
     * @Author wells
     * @Description //TODO 根据角色查询用户信息
     * @Date 14:36 2020/2/11
     * @Param 
     * @return 
     **/
    @Query(nativeQuery = true, value = "SELECT u.* FROM cmiep.manage_user as u LEFT JOIN cmiep.manage_user_role as utr on u.user_id = utr.user_id WHERE utr.rule_id = :ruleId")
    List<ManageUser> ruleIdToUser(Long ruleId);
}

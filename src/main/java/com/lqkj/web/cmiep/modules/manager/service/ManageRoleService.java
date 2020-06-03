package com.lqkj.web.cmiep.modules.manager.service;

import com.lqkj.web.cmiep.modules.log.service.ManageLogService;
import com.lqkj.web.cmiep.modules.manager.dao.ManageResourceRepository;
import com.lqkj.web.cmiep.modules.manager.dao.ManageRoleRepository;
import com.lqkj.web.cmiep.modules.manager.dao.ManageRoleResourceRepository;
import com.lqkj.web.cmiep.modules.manager.dao.ManageUserRepository;
import com.lqkj.web.cmiep.modules.manager.domain.ManageRole;
import com.lqkj.web.cmiep.modules.manager.domain.ManageUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * 用户角色服务
 */
@Service
@Transactional
public class ManageRoleService {

    @Autowired
    ManageUserRepository userRepository;

    @Autowired
    ManageRoleRepository roleRepository;

    @Autowired
    ManageResourceRepository resourceRepository;

    @Resource
    ManageRoleResourceRepository roleResourceRepository;

    @Autowired
    ManageLogService logService;

    public ManageRole add(String name, String enName, Long[] authorities, String userCode) {
        logService.addLog("用户角色服务", "add",
                "增加用户角色", userCode);

        //判断用户角色名是否存在
        Boolean exits=roleRepository.findByRuleName(name,enName).isEmpty();
        if(!exits){
            return null;
        }
        ManageRole rule = new ManageRole();

        rule.setName(name);
        rule.setContent(enName);
        rule.setResources(new HashSet<>());

        for (Long authority : authorities) {
            rule.getResources().add(resourceRepository.getOne(authority));
        }

        rule = roleRepository.save(rule);

        appendUserToNowUser(rule,userCode);

        return rule;
    }

    /**
     * 增加角色到当前用户
     */
    private void appendUserToNowUser(ManageRole rule, String userCode) {

        ManageUser user = userRepository.findByUserName(userCode);

        user.getRules().add(rule);

        userRepository.save(user);
    }

    @Transactional
    public Boolean delete(Long[] id) {
        logService.addLog("用户角色服务", "delete",
                "删除用户角色", null);

        for (Long i : id) {
            //查询该角色绑定的用户
            List<ManageUser> users = userRepository.ruleIdToUser(i);
            if(users.size()>1){
                return false;
            }
            //删除绑定的用户
            roleRepository.deleteUserRule(i);

            //先删除权限与角色的关联
            roleResourceRepository.deleteByRuleId(i);
            roleRepository.deleteById(i);
        }
        return true;
    }


    public List<ManageRole> updateAuthorities(String[] roles, Long[] authorities){
        logService.addLog("用户角色服务", "update",
                "更新用户角色对应权限", null);
        List<ManageRole> list = new ArrayList<>();
        if(roles != null && roles.length > 0){
            for (String role : roles) {

                ManageRole gnsManageRole = roleRepository.findByContent(role);

                Optional.ofNullable(gnsManageRole).ifPresent(v ->{
                    Long[] authorityArray = new Long[]{};
                    if (v.getResources() != null) {
                        List<Object> authorityList = new ArrayList<>(Arrays.asList(authorities));

                        v.getResources().forEach(s ->{
                            authorityList.add(s.getAuthorityId());
                        });

                        authorityArray = authorityList.stream().toArray(Long[] :: new);
                    }
                    update(v.getRuleId(),v.getName(),v.getContent(),authorityArray);
                    list.add(v);
                });
            }
        }

        return list;

    }

    public ManageRole update(Long id, String name, String enName, Long[] authorities) {
        logService.addLog("用户角色服务", "update",
                "更新用户角色", null);

        ManageRole rule = roleRepository.getOne(id);

        rule.setUpdateTime(new Timestamp(new Date().getTime()));

        if (rule.getResources()==null) {
            rule.setResources(new HashSet<>());
        } else {
            rule.getResources().clear();
        }

        for (Long authority : authorities) {
            rule.getResources().add(resourceRepository.getOne(authority));
        }

        return roleRepository.save(rule);
    }

    public ManageRole info(Long id) {
        logService.addLog("用户角色服务", "add",
                "查询用户角色", null);

        return roleRepository.findById(id).get();
    }

    public Page<ManageRole> page(String userName, String keyword, Integer page, Integer pageSize) {
        logService.addLog("用户角色服务", "add",
                "分页查询用户角色", null);

        String k = keyword==null ? "" : keyword;

        return roleRepository.findSupportRules(userName, "%" + k + "%",
                PageRequest.of(page, pageSize));
    }

    public List<ManageRole> ruleAll() {
        return roleRepository.findAll();
    }

}

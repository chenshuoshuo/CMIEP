package com.lqkj.web.cmiep.modules.manager.service;

import com.google.common.collect.Lists;
import com.lqkj.web.cmiep.modules.log.service.ManageLogService;
import com.lqkj.web.cmiep.modules.manager.dao.ManageResourceRepository;
import com.lqkj.web.cmiep.modules.manager.dao.ManageResourceSQLDao;
import com.lqkj.web.cmiep.modules.manager.dao.ManageRoleRepository;
import com.lqkj.web.cmiep.modules.manager.domain.ManageResource;
import com.lqkj.web.cmiep.modules.manager.domain.ManageRole;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 用户权限服务
 */
@Service
@Transactional
public class ManageResourceService {

    @Autowired
    private ManageResourceSQLDao resourceSQLDao;

    private ManageResourceRepository resourceRepository;

    private ManageRoleRepository roleRepository;

    private ManageLogService gnsManageLogService;


    public ManageResourceService(ManageResourceRepository resourceRepository,
                                    ManageRoleRepository roleRepository,
                                    ManageLogService gnsManageLogService) {
        this.resourceRepository = resourceRepository;
        this.roleRepository = roleRepository;
        this.gnsManageLogService = gnsManageLogService;
    }

    public ManageResource add(ManageResource authority, String userName) {
        gnsManageLogService.addLog("用户权限服务", "add",
                "增加一个用户权限", userName);

        authority.setEnabled(Boolean.TRUE);
        ManageResource saveAuthority = resourceRepository.save(authority);

        ManageRole saveRule = roleRepository.getOne(1L);
        saveRule.getResources().add(saveAuthority);
        roleRepository.save(saveRule);
        return saveAuthority;
    }

    public void delete(Long[] id, String userName) {
        gnsManageLogService.addLog("用户权限服务", "delete",
                "删除一个用户权限", userName);

        for (Long i : id) {
            resourceRepository.deleteById(i);
        }
    }

    public ManageResource update(Long id, ManageResource authority, String userName) {
        gnsManageLogService.addLog("用户权限服务", "update",
                "更新一个用户权限", userName);

        ManageResource savedAuthority = resourceRepository.getOne(id);

        BeanUtils.copyProperties(authority, savedAuthority);

        HashSet<ManageResource> ccrUserAuthorities = new HashSet<>();

        queryChildAuth(ccrUserAuthorities, id);


        if (ccrUserAuthorities != null && ccrUserAuthorities.size() > 0) {
            Iterator<ManageResource> it = ccrUserAuthorities.iterator();
            while (it.hasNext()) {
                ManageResource auth = it.next();
                resourceRepository.updateChildState(auth.getAuthorityId(), savedAuthority.getEnabled());
            }
        }

        if (savedAuthority.getParentId() != null && savedAuthority.getEnabled()) {
            ccrUserAuthorities.clear();

            queryParentAuth(ccrUserAuthorities, savedAuthority.getParentId());

            if (ccrUserAuthorities != null && ccrUserAuthorities.size() > 0) {
                Iterator<ManageResource> it = ccrUserAuthorities.iterator();
                while (it.hasNext()) {
                    ManageResource auth = it.next();
                    resourceRepository.updateChildState(auth.getAuthorityId(), savedAuthority.getEnabled());
                }
            }
        }

        return resourceRepository.save(savedAuthority);
    }

    public void queryChildAuth(HashSet<ManageResource> ccrUserAuths, Long id) {
        List<ManageResource> ccrUserAuthorities = resourceRepository.queryChildAuth(id);
        if (ccrUserAuthorities != null && ccrUserAuthorities.size() > 0) {
            for (ManageResource gnsManageResource : ccrUserAuthorities) {
                ccrUserAuths.add(gnsManageResource);
                queryChildAuth(ccrUserAuths, gnsManageResource.getAuthorityId());
            }
        }
    }

    public void queryParentAuth(HashSet<ManageResource> ccrUserAuths, Long parentId) {
        if(parentId!=null){
            ManageResource parentAuth = resourceRepository.getOne(parentId);
            if (parentAuth != null) {
                ccrUserAuths.add(parentAuth);
                queryParentAuth(ccrUserAuths, parentAuth.getParentId());
            }
        }
    }

    public ManageResource info(Long id, String userName) {
        gnsManageLogService.addLog("用户权限服务", "info",
                "查询一个用户权限", userName);

        return resourceRepository.findById(id).get();
    }

    public Page<ManageResource> page(String name, String keyword, Integer page, Integer pageSize, String userName) {
        gnsManageLogService.addLog("用户权限服务", "page",
                "分页查询用户权限", userName);
        String k = keyword == null ? "" : keyword;

        return resourceRepository.findSupportAuthority(name, "%" + k + "%",
                PageRequest.of(page, pageSize));
    }

    public List<ManageResource> findByRuleId(Long ruleId, String userName) {
        gnsManageLogService.addLog("用户权限服务", "findByRuleId",
                "根据角色查询权限", userName);

        return Lists.newArrayList(roleRepository.getOne(ruleId).getResources());
    }

    public List<ManageResource> findByType(ManageResource.UserAuthorityType type, String userName) {
        gnsManageLogService.addLog("用户权限服务", "findByType",
                "根据类型查询权限", userName);
        if(type.name().equals("home_menu")){
            return resourceRepository.findByManageType(userName,type.name());
        }
        return resourceRepository.findByType(userName,type.name());
    }

    public void batchUpdateEnabled(Long[] authorities, Boolean enabled, String userName) {
        gnsManageLogService.addLog("用户权限服务", "batchUpdate",
                "批量更新权限状态", userName);

        for (Long authority : authorities) {
            ManageResource userAuthority = resourceRepository.getOne(authority);

            userAuthority.setEnabled(enabled);

            resourceRepository.save(userAuthority);
        }
    }

    public List<ManageResource> findByRoleAndUserId(String userId, String roles, String userName) {
        gnsManageLogService.addLog("用户权限服务", "findByRoleAndUserId",
                "查询更新权限状态列表", userName);

        String sql = "select * from ccr_user_authority " +
                " where 1 = 1";

        if (userId != null && roles != null) {
            sql += " and target_user_role && ARRAY[" + roles + ",'public'] \\:\\:varchar[] or specify_user_id && ARRAY['" + userId + "'] \\:\\:varchar[] group by authority_id";
        }

        return resourceSQLDao.executeSql(sql, ManageResource.class);
    }
}

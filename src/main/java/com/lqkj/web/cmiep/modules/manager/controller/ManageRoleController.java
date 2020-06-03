package com.lqkj.web.cmiep.modules.manager.controller;

import com.lqkj.web.cmiep.message.MessageBean;
import com.lqkj.web.cmiep.message.MessageListBean;
import com.lqkj.web.cmiep.modules.manager.domain.ManageRole;
import com.lqkj.web.cmiep.modules.manager.service.ManageRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户角色")
@RestController
@Validated
public class ManageRoleController {

    @Autowired
    ManageRoleService roleService;

    @ApiOperation("增加角色")
    @PutMapping("/center/user/rule")
    public MessageBean<ManageRole> add(@RequestParam String name,
                                          @RequestParam String enName,
                                          @RequestParam Long[] authorities,
                                          Authentication authentication ) {

        String userCode = "";
        if(authentication != null){
            Jwt jwt =(Jwt)authentication.getPrincipal();
            userCode = (String)jwt.getClaims().get("user_name");
        }

        Object object=roleService.add(name, enName, authorities,userCode);
        if(object!=null){
            return MessageBean.ok((ManageRole) object);
        }
        return MessageBean.error("存在相同角色名");
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/center/user/rule/{id}")
    public MessageBean<Long[]> delete(@PathVariable Long[] id) {
       try {
           Boolean deleteStatus = roleService.delete(id);
           if(!deleteStatus){
               return MessageBean.error("当前角色,已被其它用户绑定,请先删除角色绑定的其它用户！");
           }
       }catch (Exception e){
            return MessageBean.error("当前角色下,存在用户,请先删除角色下的用户！");
        }
        return MessageBean.ok(id);
    }

    @ApiOperation("更新用户角色")
    @PostMapping("/center/user/rule/{id}")
    public MessageBean<ManageRole> update(@RequestParam String name,
                                                                                     @RequestParam String enName,
                                                                                     @RequestParam Long[] authorities,
                                                                                     @PathVariable Long id) {
        return MessageBean.ok(roleService.update(id, name, enName, authorities));
    }

    @ApiOperation("更新角色对应权限")
    @PostMapping("/center/user/rule/authorities")
    public MessageListBean<ManageRole> updateAuthorities(@RequestParam String[] roles,
                                                            @RequestParam Long[] authorities) {
        return MessageListBean.ok(roleService.updateAuthorities(roles, authorities));
    }

    @ApiOperation("查询用户角色信息")
    @GetMapping("/center/user/rule/{id}")
    public MessageBean<ManageRole> info(@PathVariable Long id) {
        return MessageBean.ok(roleService.info(id));
    }

    @ApiOperation("分页查询用户角色")
    @GetMapping("/center/user/rule")
    public MessageBean<Page<ManageRole>> page(@RequestParam(required = false) String keyword,
                                                                                         @RequestParam Integer page,
                                                                                         @RequestParam Integer pageSize,
                                                                                         Authentication authentication) {
        if("".equals(keyword)){
            keyword = null;
        }
        String userCode = "";
        if(authentication != null){
            Jwt jwt =(Jwt)authentication.getPrincipal();
            userCode = (String)jwt.getClaims().get("user_name");
        }
        return MessageBean.ok(roleService.page(userCode,keyword, page, pageSize));
    }

    @ApiOperation("获取角色所有信息")
    @GetMapping("/center/user/ruleAll")
    public MessageListBean<ManageRole> ruleAll() {
        return MessageListBean.ok(roleService.ruleAll());
    }


}

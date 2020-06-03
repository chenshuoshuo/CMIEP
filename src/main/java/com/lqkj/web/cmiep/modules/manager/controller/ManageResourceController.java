package com.lqkj.web.cmiep.modules.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.lqkj.web.cmiep.message.MessageBean;
import com.lqkj.web.cmiep.message.MessageListBean;
import com.lqkj.web.cmiep.modules.manager.domain.ManageResource;
import com.lqkj.web.cmiep.modules.manager.service.ManageResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "用户权限")
@RestController
@Validated
public class ManageResourceController {

    @Autowired
    ManageResourceService resourceService;

    @ApiOperation("增加用户权限")
    @PutMapping("/center/user/authority/")
    public MessageBean<ManageResource> add(ManageResource authority,
                                           @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        return MessageBean.ok(resourceService.add(authority, userName));
    }

    @ApiOperation("删除用户权限")
    @DeleteMapping("/center/user/authority/{id}")
    public MessageBean<Long[]> delete(Long[] id,
                                      @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        resourceService.delete(id, userName);
        return MessageBean.ok(id);
    }

    @ApiOperation("更新用户权限")
    @PostMapping("/center/user/authority/{id}")
    public MessageBean<ManageResource> update(@RequestBody ManageResource authority,
                                                                                         @PathVariable Long id,
                                                                                         @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        return MessageBean.ok(resourceService.update(id, authority, userName));
    }

    @ApiOperation("查询用户权限")
    @GetMapping("/center/user/authority/{id}")
    public MessageBean<ManageResource> info(@PathVariable Long id,
                                                                                       @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        return MessageBean.ok(resourceService.info(id, userName));
    }

    @ApiOperation("分页查询用户权限")
    @GetMapping("/center/user/authority/page")
    public MessageBean<Page<ManageResource>> page(String keyword,
                                                                                             @RequestParam Integer page,
                                                                                             @RequestParam Integer pageSize,
                                                                                             @ApiIgnore Authentication authentication) {
        String userCode = "";
        if(authentication != null){
            Jwt jwt =(Jwt)authentication.getPrincipal();
            userCode = (String)jwt.getClaims().get("user_name");
        }
        return MessageBean.ok(resourceService.page(userCode,keyword, page, pageSize, userCode));
    }

    @ApiOperation("根据角色id查询权限")
    @GetMapping("/center/user/authority")
    public MessageListBean<ManageResource> queryByRule(@RequestParam Long ruleId,
                                                          @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        return MessageListBean.ok(resourceService.findByRuleId(ruleId, userName));
    }

    @ApiOperation("根据类型查询权限")
    @GetMapping("/center/user/authority/type/{type}")
    public MessageListBean<ManageResource> queryByType(@PathVariable ManageResource.UserAuthorityType type,
                                                                                                  @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        return MessageListBean.ok(resourceService.findByType(type, userName));
    }

    @ApiOperation("匹配更新权限是否开启")
    @PostMapping("/center/user/authority/batch/enabled")
    public MessageBean batchUpdateEnabled(@RequestParam Long[] authorities,
                                          @RequestParam Boolean enabled,
                                          @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        this.resourceService.batchUpdateEnabled(authorities, enabled, userName);
        return MessageBean.ok();
    }

    @ApiOperation("获取权限状态列表")
    @PostMapping("/center/user/authority/list")
    public MessageBean<List<ManageResource>> findByRoleAndUserId(
            @ApiIgnore Authentication authentication) {
        String rules = "";
        String userCode = "";
        if(authentication != null){
            Jwt jwt =(Jwt)authentication.getPrincipal();
            JSONArray jsonArray = (JSONArray)jwt.getClaims().get("rules");
            List<String> list  = JSONObject.parseArray(jsonArray.toJSONString(),String.class);
            rules = "'" + list
                    .stream()
                    .collect(Collectors.joining("','")) + "'";
            userCode = (String)jwt.getClaims().get("user_name");
        }
        return MessageBean.ok(resourceService.findByRoleAndUserId(userCode, rules, userCode));
    }
}

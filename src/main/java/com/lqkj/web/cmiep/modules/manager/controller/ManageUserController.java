package com.lqkj.web.cmiep.modules.manager.controller;

import com.lqkj.web.cmiep.message.MessageBean;
import com.lqkj.web.cmiep.message.MessageListBean;
import com.lqkj.web.cmiep.modules.manager.domain.ManageRole;
import com.lqkj.web.cmiep.modules.manager.domain.ManageUser;
import com.lqkj.web.cmiep.modules.manager.service.ManageUserService;
import com.lqkj.web.cmiep.utils.PwdCheckUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;

@Api(tags = "用户管理")
@RestController
@Validated
public class ManageUserController {

    @Autowired
    ManageUserService gnsManageUserService;

    @ApiOperation("注册用户")
    @PutMapping("/center/user/register")
    public MessageBean<ManageUser> register(@RequestBody ManageUser user, @RequestParam Integer adminCode,
                                         @ApiIgnore Authentication authentication) throws Exception {
        //检查密码是否为大于6-20位的长度
        boolean checkPasLength = PwdCheckUtil.checkPasswordLength(user.getPassword(), "6", "20");
        if(!checkPasLength){
            return MessageBean.error("请输入6位以上并且小于20位密码");
        }
        //检查是否包含数字
        boolean checkContainDigit = PwdCheckUtil.checkContainDigit(user.getPassword());
        //检查是否包含大写字母
        boolean checkContainUpperCase = PwdCheckUtil.checkContainUpperCase(user.getPassword());
        if(!checkContainDigit || !checkContainUpperCase){
            return MessageBean.error("密码必须包含大写字母和数字");
        }
        String userName = "guest";
        if(authentication!=null){
            Jwt jwt = (Jwt)authentication.getPrincipal();
            userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        }
        if (gnsManageUserService.registerAdmin(adminCode, user, userName) != null) {
            return MessageBean.ok(gnsManageUserService.registerAdmin(adminCode, user, userName));
        }
        return MessageBean.error("用户已存在");
    }

    @ApiOperation("查询用户信息")
    @GetMapping("/center/user/{id}")
    public MessageBean<ManageUser> info(@PathVariable Long id,
                                     @ApiIgnore Authentication authentication) {
        Jwt jwt = (Jwt)authentication.getPrincipal();
        String userName = (String)jwt.getClaims().get("user_name") == null? "guest" : (String)jwt.getClaims().get("user_name");
        return MessageBean.ok(gnsManageUserService.info(id, userName));
    }

    @ApiOperation("根据用户名查询用户信息")
    @GetMapping("/center/user/name/{username}")
    public MessageBean<ManageUser> info(@PathVariable String username, @ApiIgnore Authentication authentication) throws Exception {

        //String name = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = "";
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            name = (String) jwt.getClaims().get("user_name");
        }

        return MessageBean.ok((ManageUser) gnsManageUserService.loadUserByUsername(name));
    }

    @ApiOperation("更新用户密码")
    @PostMapping("/center/user/{id}")
    public MessageBean<ManageUser> update(@RequestParam(required = false) String password,
                                       @RequestParam(required = false) Boolean admin,
                                       @RequestParam(required = false) String userName,
                                       @RequestParam(required = false) String oldPassword,
                                       @PathVariable Long id,
                                       @ApiParam(value = "头像文件") MultipartFile headFile) throws Exception {

        String headPath = null;
        if (headFile != null) {
            headPath = gnsManageUserService.saveUploadFile(headFile, "png", "jpg");
        }
        ManageUser user = null;
        if(StringUtils.isNotBlank(password)){
            //检查密码是否为大于6-20位的长度
            boolean checkPasLength = PwdCheckUtil.checkPasswordLength(password, "6", "20");
            if(!checkPasLength){
                return MessageBean.error("请输入6位以上并且小于20位密码");
            }
            //检查是否包含数字
            boolean checkContainDigit = PwdCheckUtil.checkContainDigit(password);
            //检查是否包含大写字母
            boolean checkContainUpperCase = PwdCheckUtil.checkContainUpperCase(password);
            if(!checkContainDigit || !checkContainUpperCase){
                return MessageBean.error("密码必须包含大写字母和数字");
            }
        }
        try {
           user = gnsManageUserService.update(id, password, oldPassword, admin, headPath, userName);
        }catch (UserPrincipalNotFoundException e) {
            return MessageBean.error("旧密码输入错误");
        }
        if (user != null) {
            return MessageBean.ok(user);
        } else {
            return MessageBean.error("密码修改失败");
        }
    }

//    @ApiOperation("根据用户id删除用户")
//    @DeleteMapping("/center/user/{id}")
//    public MessageBean<Long> delete(@PathVariable String id) {
//        ccrUserService.delete(Long.valueOf(id));
//        return MessageBean.ok();
//    }

    @ApiOperation("批量删除用户ID")
    @DeleteMapping("/center/user/{ids}")
    public MessageBean<Long> delete(@PathVariable String ids, @ApiIgnore Authentication authentication) {
        String[] idsArr = ids.split(",");
        String name = "";
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            name = (String) jwt.getClaims().get("user_name");
        }
        for (int i = 0; i < idsArr.length; i++){
            gnsManageUserService.delete(Long.parseLong(idsArr[i]), name);
        }
        return MessageBean.ok();
    }


    @ApiOperation("分页查询用户信息")
    @GetMapping("/center/user/")
    public MessageBean<Page<ManageUser>> page(String keyword, Integer page, Integer pageSize, @ApiIgnore Authentication authentication) {
        String name = "";
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            name = (String) jwt.getClaims().get("user_name");
        }
        return MessageBean.ok(gnsManageUserService.page(keyword, page, pageSize, name));
    }

    @ApiOperation("根据用户id查询用户角色")
    @GetMapping("/center/user/{id}/rules")
    public MessageListBean<ManageRole> ruleByUserId(@PathVariable Long id, @ApiIgnore Authentication authentication) {
        String name = "";
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            name = (String) jwt.getClaims().get("user_name");
        }
        return MessageListBean.ok(new ArrayList<>(gnsManageUserService.findRulesByUserId(id, name)));
    }

    @ApiOperation("查询当前登录的用户信息")
    @GetMapping("/center/user/oauth")
    public MessageBean<ManageUser> oauth(@ApiIgnore Authentication authentication) {

        //String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        String userCode = "";
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            userCode = (String) jwt.getClaims().get("user_name");
        }

        return MessageBean.ok((ManageUser) gnsManageUserService.loadUserByUsername(userCode));

    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", allowableValues = "weixin")
    })

    @ApiOperation("用戶組統計")
    @GetMapping("/center/user/group")
    public MessageListBean<Object[]> group(@ApiIgnore Authentication authentication) {
        String userCode = "";
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            userCode = (String) jwt.getClaims().get("user_name");
        }
        return MessageListBean.ok(gnsManageUserService.userGroup(userCode));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "platform", allowableValues = "weixin")
    })
    @ApiOperation("oauth2回调")
    @GetMapping("/center/user/{platform}/callback")
    public void callback(@RequestParam(name = "user_id") Long userId,
                         @RequestParam(name = "code") String code,
                         @RequestParam(name = "state") String state) {

    }

    @ApiOperation("绑定用户角色")
    @PostMapping("/center/user/{userId}/rule/bind")
    public MessageBean bindRules(@PathVariable Long userId,
                                 @RequestParam Long[] rules,
                                 @ApiIgnore Authentication authentication) {
        String userCode = "";
        if (authentication != null) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            userCode = (String) jwt.getClaims().get("user_name");
        }
        this.gnsManageUserService.bindRules(userId, rules, userCode);
        return MessageBean.ok();
    }

    @ApiOperation("退出登录")
    @PostMapping("/center/user/loginout/{userId}")
    public MessageBean loginout(@PathVariable Long userId) {
        this.gnsManageUserService.loginout(userId);
        return MessageBean.ok();
    }
    
}

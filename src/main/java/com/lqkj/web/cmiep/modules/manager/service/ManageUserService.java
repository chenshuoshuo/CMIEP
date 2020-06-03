package com.lqkj.web.cmiep.modules.manager.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lqkj.web.cmiep.modules.log.service.ManageLogService;
import com.lqkj.web.cmiep.modules.manager.dao.ManageRoleRepository;
import com.lqkj.web.cmiep.modules.manager.dao.ManageUserBatchRepository;
import com.lqkj.web.cmiep.modules.manager.dao.ManageUserRepository;
import com.lqkj.web.cmiep.modules.manager.domain.ManageRole;
import com.lqkj.web.cmiep.modules.manager.domain.ManageUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户管理服务
 */
@Service
@Transactional
public class ManageUserService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String UPLOAD_FILE_PATH = "./upload/user/";

    @Autowired
    ManageUserRepository userRepository;

    @Autowired
    ManageRoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ManageLogService logService;

    @Autowired
    ManageUserBatchRepository userBatchRepository;

    @Value("${admin.code}")
    Integer adminCode;

    /**
     * 密码登录
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logService.addLog("用户管理服务", "loadClientByClientId",
                "普通用户查询", username);
            return userRepository.findByUserName(username);

}

    public ManageUser findByUserName(String userName){
        logService.addLog("用户管理服务", "findByUserName",
                "普通用户查询", userName);
        return userRepository.findByUserName(userName);
    }

    /**
     * 注册管理员用户
     */
    public ManageUser registerAdmin(Integer adminCode, ManageUser user, String userName) throws Exception {
        if (!adminCode.equals(this.adminCode)) {
            throw new Exception("授权码不正确");
        }
        if(userRepository.findByUserName(user.getUsername())!=null){
            return null;
        }
        logService.addLog("用户管理服务", "registerAdmin",
                "用户注册", userName);

        user.setAdmin(Boolean.TRUE);
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
        user.setUserGroup(ManageUser.UserGroupType.teacher_staff);
        user.setRules(Sets.newHashSet(roleRepository.getOne(1L)));
        //先根据用户名进行查询，看是否已经存在该用户

        return userRepository.save(user);
    }

    /**
     * 查询用户信息
     */
    public ManageUser info(Long id, String userName) {
        logService.addLog("用户管理服务", "info",
                "用户信息查询", userName);

        return this.setIconURL(userRepository.findById(id).get());
    }

    /**
     * 更新用户密码和头像
     */
    public ManageUser update(Long id, String password,String oldPassword ,Boolean admin,String headPath,String userName) throws UserPrincipalNotFoundException {
        logService.addLog("用户管理服务", "update",
                "更新用户密码和头像", userName);

        ManageUser user = userRepository.findById(id).get();
        //密码验证
        Boolean isUpdate = false;
        if (password!=null) {
            if (passwordEncoder.matches(oldPassword,user.getPassWord())) {
                user.setPassWord(passwordEncoder.encode(password));
                isUpdate = true;
            } else{
                throw new UserPrincipalNotFoundException("free");
            }

        }
        if (admin!=null){
            user.setAdmin(admin);
            isUpdate = true;
        }
        if(StringUtils.isNotBlank(headPath)){
            user.setHeadPath(headPath);
            isUpdate = true;
        }
        if(StringUtils.isNotBlank(userName)){
            user.setHeadPath(userName);
            isUpdate = true;
        }
        if(isUpdate){
            userRepository.save(user);
        }
        return this.setIconURL(user);
    }

    /**
     * 删除用户
     */
    public void delete(Long id, String userName) {
        logService.addLog("用户管理服务", "delete",
                "删除用户", userName);

        userRepository.deleteById(id);
    }

    /**
     * 分页搜索
     */
    public Page<ManageUser> page(String keyword, Integer page, Integer pageSize, String username) {
        logService.addLog("用户管理服务", "page",
                "分页查询用户列表", username);

        ManageUser user = new ManageUser();
        user.setUserCode(keyword);

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("userCode", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnorePaths("userId");
        Page<ManageUser> result = userRepository.findAll(Example.of(user, exampleMatcher),
                PageRequest.of(page, pageSize));
        return result.map(v -> (ManageUser) this.setIconURL(v));
    }

    /**
     * 查询用户角色
     */
    public Set<ManageRole> findRulesByUserId(Long id, String name) {
        logService.addLog("用户管理服务", "findRulesByUserId",
                "查询用户角色", name);

        return userRepository.getOne(id).getRules();
    }

    /**
     * 用戶統計
     */
    public List<Object[]> userGroup(String name) {
        logService.addLog("用户管理服务", "userGroup",
                "用戶統計", name);

        return userRepository.userStatistics();
    }

    /**
     * 绑定角色
     */
    public void bindRules(Long userId, Long[] rules, String userCode) {
        logService.addLog("用户管理服务", "bindRules",
                "绑定用户角色", userCode);

        ManageUser user = userRepository.getOne(userId);

        user.getRules().clear();

        for (Long rule : rules) {
            user.getRules().add(this.roleRepository.getOne(rule));
        }

        userRepository.save(user);
    }

    /**
     * 退出登录
     * @param userId
     */
    public void loginout(Long userId) {
        ManageUser savedUser = userRepository.getOne(userId);

        savedUser.setCasTicket(null);

        userRepository.save(savedUser);

        SecurityContextHolder.clearContext();
    }

    /**
     * 根据userCode获取用户
     * @return
     */
    public ManageUser findByUserCode(String userCode){
        return this.setIconURL(userRepository.findByUserName(userCode));
    }

//    /**
//     * 从CMDBE更新用户
//     */
//    public String updateUserFromCmdbe() {
//        StringBuilder errerUserCode = new StringBuilder();
//        Boolean hasNext = true;
//        int page = 0;
//        // 教职工
//        while (hasNext){
//            //StringBuffer userString = new StringBuffer();
//            //StringBuffer userGroupString = new StringBuffer();
//            //StringBuffer userRuleString = new StringBuffer();
//            String password = passwordEncoder.encode("123456");
//
//            ObjectNode result = cmdbeApi.pageQueryTeachingStaff(null, null, page, 2000);
//            hasNext = !(result.get("last").booleanValue());
//            page += 1;
//
//            Iterator<JsonNode> iterator = result.get("content").iterator();
//            while (iterator.hasNext()){
//                JsonNode jsonNode = iterator.next();
//                try {
//                    String userCode = jsonNode.get("staffNumber").textValue();
//                    ManageUser user = userRepository.findByUserName(userCode);
//                    if(user==null){
//                        user = new ManageUser();
//                        user.setUserCode(userCode);
//                        user.setAdmin(false);
//                        user.setUserGroup(ManageUser.UserGroupType.teacher_staff);
//                        user.setPassWord(password);
//                        user.setUserName(jsonNode.get("realName").textValue());
//                        user.setRules(Sets.newHashSet(ruleRepository.getOne(2L)));
//                        userRepository.save(user);
//                    }
//                }catch (Exception e){
//                    errerUserCode.append(jsonNode.get("staffNumber").textValue()+",");
//                    logger.error(e.getMessage(),e);
//                    continue;
//                }
//            }
//            //
//        }
//
//        hasNext = true;
//        page = 0;
//        // 学生
//        while (hasNext){
//            StringBuffer userString = new StringBuffer();
//            StringBuffer userGroupString = new StringBuffer();
//            StringBuffer userRuleString = new StringBuffer();
//            String password = passwordEncoder.encode("123456");
//            ObjectNode result = cmdbeApi.pageQueryStudentInfo(null, null, page, 2000);
//            hasNext = !(result.get("last").booleanValue());
//            page += 1;
//
//            Iterator<JsonNode> iterator = result.get("content").iterator();
//            while (iterator.hasNext()){
//                JsonNode jsonNode = iterator.next();
//                try {
//                    String userCode = jsonNode.get("studentNo").textValue();
//                    ManageUser user = userRepository.findByUserName(userCode);
//                    if(user==null){
//                        user = new ManageUser();
//                        user.setUserCode(userCode);
//                        user.setAdmin(false);
//                        user.setUserGroup(ManageUser.UserGroupType.student);
//                        user.setUserName(jsonNode.get("realName").textValue());
//                        user.setPassWord(password);
//                        user.setRules(Sets.newHashSet(ruleRepository.getOne(3L)));
//                        userRepository.save(user);
//                    }
//                }catch (Exception e){
//                    errerUserCode.append(jsonNode.get("studentNo").textValue()+",");
//                    logger.error(e.getMessage(),e);
//                    continue;
//                }
//
//            }
//        }
//        return errerUserCode.toString();
//    }

    /*private void executeSql(StringBuffer userCodeString, StringBuffer userGroupString,StringBuffer userRuleString,String password){
        if(userCodeString.length() > 0){
            userCodeString = userCodeString.deleteCharAt(userCodeString.length() - 1);
            userGroupString = userGroupString.deleteCharAt(userGroupString.length() - 1);
            userRuleString =  userRuleString.deleteCharAt(userRuleString.length() - 1);
        }
        StringBuffer sqlString = new StringBuffer();
        sqlString.append("select fun_ccr_update_usr('")
                .append(userCodeString)
                .append("','")
                .append(userGroupString)
                .append("','")
                .append(userRuleString)
                .append("','")
                .append(password)
                .append("');");
        logger.info(sqlString.toString());
        ManageUserBatchRepository.bulkMergeUser(sqlString.toString());
    }*/

    /**
     * 保存上传的文件
     *
     * @return 保存的路径
     */
    public String saveUploadFile(MultipartFile file, String... supportFormats) throws Exception {
        String format = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];

        if (supportFormats.length!=0 && !Lists.newArrayList(supportFormats).contains(format)) {
            throw new Exception("格式不支持:" + format);
        }

        File outPutFile = new File(new StringBuilder().append(UPLOAD_FILE_PATH)
                .append(DigestUtils.md2Hex(String.valueOf(System.currentTimeMillis())))
                .append(".")
                .append(format)
                .toString());

        InputStream is = null;

        try {
            is = file.getInputStream();

            FileUtils.copyInputStreamToFile(is, outPutFile);
        } finally {
            if (is!=null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return outPutFile.getPath();
    }

    /**
     * 设置图片访问地址
     */
    public ManageUser setIconURL(ManageUser user) {
        if (user.getHeadPath()!=null) {
            String url = user.getHeadPath()
                    .replace(".\\upload\\user\\","/upload/user/");

            user.setHeadUrl(url);
        }
        return user;
    }



}

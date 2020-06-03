package com.lqkj.web.cmiep.modules.manager.service;

import com.lqkj.web.cmiep.modules.log.service.ManageLogService;
import com.lqkj.web.cmiep.modules.manager.dao.ManageUserRepository;
import com.lqkj.web.cmiep.modules.manager.domain.ManageUser;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * cas ticket处理服务
 */
@Service
@Transactional
public class CasService {

    @Autowired
    ManageUserRepository userRepository;

    @Autowired
    ManageLogService logService;

    @Autowired
    PasswordEncoder passwordEncoder;


    /**
     * 更新用户ticket
     */
    public ManageUser updateTicket(String username, String ticket) throws DocumentException {
        logService.addLog("cas ticket处理服务", "updateTicket",
                "更新用户ticket", null);

        ManageUser user = userRepository.findByUserName(username);

        user.setCasTicket(passwordEncoder.encode(ticket));

        user.setPassWord(null);

        return userRepository.save(user);
    }
}

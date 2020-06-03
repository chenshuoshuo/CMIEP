package com.lqkj.web.cmiep.modules.manager.controller;

import com.lqkj.web.cmiep.message.MessageBean;
import com.lqkj.web.cmiep.modules.manager.domain.ManageUser;
import com.lqkj.web.cmiep.modules.manager.service.CasService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "cas处理")
@RestController
public class CasController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CasService casService;

    @ApiOperation("cas回调地址")
    @GetMapping("/center/cas/callback")
    public String cas(@RequestParam String ticket) {
        logger.info("收到cas ticket:{}", ticket);
        return ticket;
    }

    @ApiOperation("绑定cas ticket")
    @RequestMapping(value = "/center/cas/bind", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageBean<ManageUser> user(@RequestParam String username,
                                           @RequestParam String ticket) throws DocumentException {
        return MessageBean.ok(casService.updateTicket(username, ticket));
    }
}

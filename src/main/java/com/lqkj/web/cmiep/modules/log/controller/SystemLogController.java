package com.lqkj.web.cmiep.modules.log.controller;

import com.lqkj.web.cmiep.APIVersion;
import com.lqkj.web.cmiep.message.MessageBean;
import com.lqkj.web.cmiep.modules.log.domain.ManageLog;
import com.lqkj.web.cmiep.modules.log.service.ManageLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.sql.Timestamp;

/**
 * 系统日志api
 */
@Api(tags = "系统日志")
@RestController
public class SystemLogController {

    @Autowired
    ManageLogService logService;

    @GetMapping("/center/sys/log/" + APIVersion.V1 + "/list")
    @ApiOperation("分页查询系统日志")
    public MessageBean<Page<ManageLog>> page(Timestamp startTime, Timestamp endTime,
                                                @RequestParam Integer page,
                                                @RequestParam Integer pageSize,
                                                Authentication authentication) {
        return MessageBean.ok(logService.page(startTime, endTime, page, pageSize));
    }

    @GetMapping("/center/sys/log/" + APIVersion.V1 + "/export")
    @ApiOperation("导出系统日志")
    public ResponseEntity<StreamingResponseBody> export(@RequestParam Timestamp startTime,
                                                        @RequestParam Timestamp endTime) {
        StreamingResponseBody body = outputStream -> {
            logService.export(startTime, endTime, outputStream);
        };

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment;filename=logs.xlsx")
                .body(body);
    }
}

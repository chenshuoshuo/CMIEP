package com.lqkj.web.cmiep;


import com.lqkj.web.cmiep.message.MessageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;

import java.util.concurrent.Callable;

/**
 * 异步全局异常处理
 */
public class GlobalAsyncExceptionHandler implements CallableProcessingInterceptor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        return MessageBean.error("请求超时");
    }

    @Override
    public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
        logger.error("收到异常", t);
        return MessageBean.error(t.toString());
    }
}

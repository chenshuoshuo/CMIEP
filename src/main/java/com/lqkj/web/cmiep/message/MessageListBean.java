package com.lqkj.web.cmiep.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务器多个信息bean
 * Created by free on 2017/7/27 0027.
 */
@ApiModel(description = "多个实体返回对象")
public class MessageListBean<T> extends MessageBaseBean implements Serializable {

    public MessageListBean() {
        this.setTime(System.currentTimeMillis());
    }

    public MessageListBean(List<T> data) {
        this.data = data;

        this.setTime(System.currentTimeMillis());
    }

    /**
     * 被包含的多个消息实体
     */
    @ApiModelProperty("包含的对象")
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * 增加一个数据
     *
     * @param data 数据
     */
    public void addData(T data) {
        if (this.data == null) {
            this.data = new ArrayList<T>(5);
        }
        this.data.add(data);
    }

    public static <T> MessageListBean<T> ok(List<T> data) {
        MessageListBean<T> messageListBean = new MessageListBean<>();
        messageListBean.setStatus(true);
        messageListBean.setData(data);
        return messageListBean;
    }

    /**
     * 创建一个失败消息的内容
     * @return 消息
     */
    public static <T> MessageListBean<T> error(String message) {
        MessageListBean<T> messageListBean = new MessageListBean<>();
        messageListBean.setStatus(false);
        messageListBean.setCode(-1);
        messageListBean.setMessage(message);
        return messageListBean;
    }
}

package com.lqkj.web.cmiep.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础消息bean
 * Created by free on 2017/7/27 0027.
 */
@ApiModel(description = "基础返回对象")
public class MessageBaseBean implements Serializable {

    public static final String NEXT_PAGE="nextPage";
    /**
     * 总页数
     */
    public static final String ALL_PAGE="allPage";
    /**
     * 当前页数
     */
    public static final String TOTAL_PAGE="totalPage";

    /**
     * 消息状态
     */
    @ApiModelProperty("消息状态")
    private boolean status;

    /**
     * 消息时间戳
     */
    @ApiModelProperty("消息时间戳")
    private long time;

    /**
     * 消息状态码
     */
    @ApiModelProperty("消息状态码")
    private int code;

    /**
     * 提示信息
     */
    @ApiModelProperty("提示信息")
    private String message;

    /**
     * 附加消息
     */
    @ApiModelProperty("附加消息")
    private Map<String,Object> properties=new HashMap<String, Object>();

    public boolean isStatus() {
        return status;
    }

    public long getTime() {
        return time;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * 增加一个附属信息
     * @param key 附属信息的名称
     * @param value 附属信息
     */
    public void addPropertie(String key,Object value){
        this.properties.put(key,value);
    }

    /**
     * 移除一个附属信息
     * @param key 附属信息的名称
     */
    public void removePropertie(String key){
        this.properties.remove(key);
    }
}

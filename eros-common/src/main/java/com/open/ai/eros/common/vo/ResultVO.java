package com.open.ai.eros.common.vo;


import com.open.ai.eros.common.constants.BaseCode;
import com.open.ai.eros.common.constants.BaseCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * web接口统一返回结果
 */
@ApiModel("结果封装类")
public class ResultVO<T> {

    /**
     * 响应状态码
     */
    @ApiModelProperty("响应状态码")
    private int code;

    /**
     * 响应消息
     */
    @ApiModelProperty("响应消息")
    private String msg;

    /**
     * 响应数据
     */
    @ApiModelProperty("响应数据")
    private T data;

    public static <T> ResultVO<T> build(BaseCode baseCode, T data) {
        return new ResultVO<>(baseCode, data);
    }

    public static <T> ResultVO<T> build(BaseCode baseCode) {
        return new ResultVO<>(baseCode);
    }

    public static <T> ResultVO<T> success() {
        return new ResultVO<>(BaseCodeEnum.SUCCESS);
    }

    public static <T> ResultVO<T> success(String msg) {
        return new ResultVO<>(BaseCodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(BaseCodeEnum.SUCCESS, data);
    }

    public static <T> ResultVO<T> error(BaseCode baseCode) {
        return new ResultVO<>(baseCode.getCode(), baseCode.getMsg());
    }

    public static <T> ResultVO<T> error(int code, String msg) {
        return new ResultVO<>(code, msg);
    }

    public static <T> ResultVO<T> fail(int code, String msg) {
        return new ResultVO<>(code, msg);
    }

    public static <T> ResultVO<T> fail(BaseCodeEnum codeEnum) {
        return new ResultVO<>(codeEnum.getCode(), codeEnum.getMsg());
    }

    public static <T> ResultVO<T> fail(BaseCode baseCode) {
        return new ResultVO<>(baseCode.getCode(), baseCode.getMsg());
    }

    public static <T> ResultVO<T> fail(String msg) {
        return new ResultVO<>(BaseCodeEnum.FAIL.getCode(), msg);
    }

    public static <T> ResultVO<T> fail() {
        return new ResultVO<>(BaseCodeEnum.FAIL.getCode(), null);
    }

    public boolean isOk(){
        return this.code == BaseCodeEnum.SUCCESS.getCode();
    }
    public ResultVO() {
    }

    public ResultVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(BaseCode baseCode) {
        this.code = baseCode.getCode();
        this.msg = baseCode.getMsg();
    }

    public ResultVO(BaseCode baseCode, T data) {
        this.code = baseCode.getCode();
        this.msg = baseCode.getMsg();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

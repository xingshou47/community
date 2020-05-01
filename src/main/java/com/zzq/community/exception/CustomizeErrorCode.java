package com.zzq.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001,"你找到问题不存在了"),
    TARGET_PARAM_NOT_FOUND(2002,"你选择的问题或回复可能已被删除"),
    NO_LOGIN(2003,"当前操作需要登录，请登录后重新尝试"),
    SYS_ERROR(2004,"服务器端出现了错误"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或者不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论可能已被删除");
    private Integer code;
    private String message;

    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}

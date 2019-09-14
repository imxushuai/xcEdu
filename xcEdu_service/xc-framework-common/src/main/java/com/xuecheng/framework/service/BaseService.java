package com.xuecheng.framework.service;

import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.ResultCode;
import org.springframework.util.StringUtils;

/**
 * 基础Service
 */
public abstract class BaseService {


    /**
     * 字符串非空校验并抛出异常
     *
     * @param content    内容
     * @param resultCode 错误码
     */
    public void isNullOrEmpty(String content, ResultCode resultCode) {
        if (StringUtils.isEmpty(content)) {
            ExceptionCast.cast(resultCode);
        }
    }

    /**
     * 字符串非空校验并抛出异常
     *
     * @param content    内容
     * @param resultCode 错误码
     */
    public void isNullOrEmpty(Object content, ResultCode resultCode) {
        if (content == null) {
            ExceptionCast.cast(resultCode);
        }
    }

}

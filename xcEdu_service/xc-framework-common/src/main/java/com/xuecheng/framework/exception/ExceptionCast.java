package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常抛出类
 */
public class ExceptionCast {

    public static void cast(ResultCode resultCode) {
        throw new CustomerException(resultCode);
    }

}

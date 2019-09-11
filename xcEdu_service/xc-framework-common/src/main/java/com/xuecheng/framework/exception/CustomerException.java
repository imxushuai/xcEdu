package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;
import lombok.Getter;

/**
 * 自定义异常类
 */
@Getter
public class CustomerException extends RuntimeException {

    private ResultCode resultCode;

    public CustomerException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

}

package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "用户中心", description = "用户中心管理")
public interface UcenterControllerApi  {

    @ApiOperation("按用户名查询用户信息")
    XcUserExt findByUsername(String username);

}

package com.xuecheng.api.cms;

import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "CMS站点管理接口", description = "CMS页面站点管理接口，提供站点的增删改查")
public interface CmsSiteControllerApi {


    @ApiOperation("查询所有站点信息")
    QueryResponseResult findAll();

}

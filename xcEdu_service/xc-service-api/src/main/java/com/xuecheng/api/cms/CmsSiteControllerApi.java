package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsSiteResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "CMS站点管理接口", description = "CMS页面站点管理接口，提供站点的增删改查")
public interface CmsSiteControllerApi {


    @ApiOperation("查询所有站点信息")
    QueryResponseResult findAll();

    @ApiOperation("分页查询站点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path")
    })
    QueryResponseResult findList(int page, int size);

    @ApiOperation("新增站点")
    CmsSiteResult add(CmsSite cmsSite);

    @ApiOperation("修改站点")
    CmsSiteResult edit(CmsSite cmsSite);

    @ApiOperation("按ID获取站点")
    CmsSiteResult getCmsSite(String siteId);

    @ApiOperation("按ID删除站点")
    CmsSiteResult deleteById(String siteId);

}

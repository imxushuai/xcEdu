package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsTemplateResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "CMS模板管理接口", description = "CMS页面模板管理接口，提供模板的增删改查")
public interface CmsTemplateControllerApi {

    @ApiOperation("查询所有模板信息")
    QueryResponseResult findAll();

    @ApiOperation("分页查询模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path")
    })
    QueryResponseResult findList(int page, int size);

    @ApiOperation("新增模板")
    CmsTemplateResult add(CmsTemplate cmsTemplate);

    @ApiOperation("修改模板")
    CmsTemplateResult edit(CmsTemplate cmsTemplate);

    @ApiOperation("按ID获取模板")
    CmsTemplateResult getCmsTemplate(String templateId);

    @ApiOperation("按ID删除模板")
    CmsTemplateResult deleteById(String templateId);

}

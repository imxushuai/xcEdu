package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程分类管理", description = "课程分类管理，提供课程分类的查询等")
public interface CategoryControllerApi {

    @ApiOperation("查询分类列表")
    CategoryNode findCategoryList();

}

package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程计划管理接口", description = "课程计划管理接口，提供课程计划的增删改查")
public interface CoursePlanControllerApi {

    @ApiOperation("查询指定课程的课程计划")
    TeachplanNode findList(String courseId);

    @ApiOperation("查询指定ID的课程计划")
    Teachplan findById(String teachplanId);

    @ApiOperation("新增课程计划")
    ResponseResult add(Teachplan teachplan);

    @ApiOperation("编辑课程计划")
    ResponseResult edit(Teachplan teachplan);

    @ApiOperation("删除课程计划")
    ResponseResult delete(String teachplanId);

}

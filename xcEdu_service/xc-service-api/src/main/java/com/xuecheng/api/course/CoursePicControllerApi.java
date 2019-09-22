package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程图片管理接口", description = "课程图片管理接口，提供图片的增删改查")
public interface CoursePicControllerApi {

    @ApiOperation("新增课程图片")
    CoursePic saveCoursePic(String courseId, String pic);

    @ApiOperation("查询课程图片")
    CoursePic findById(String courseId);

    @ApiOperation("删除课程图片")
    ResponseResult deleteById(String courseId);

}

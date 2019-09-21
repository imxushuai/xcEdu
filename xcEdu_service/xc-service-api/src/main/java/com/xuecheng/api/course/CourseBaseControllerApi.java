package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseBaseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程基本信息管理接口", description = "课程基本信息管理接口，提供课程信息的增删改查")
public interface CourseBaseControllerApi {

    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页记录数", required = true, paramType = "path")
    })
    QueryResponseResult findList(int page, int size, CourseListRequest queryPageRequest);

    @ApiOperation("新增课程基本信息")
    AddCourseResult addCourse(CourseBase courseBase);

    @ApiOperation("修改课程基本信息")
    AddCourseResult editCourse(CourseBase courseBase);

    @ApiOperation("查询指定课程ID的基本信息")
    CourseBaseResult findById(String courseId);


}

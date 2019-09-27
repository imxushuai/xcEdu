package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程预览", description = "课程预览接口，提供课程预览数据的查询")
public interface CourseViewControllerApi {

    @ApiOperation("课程视图查询")
    CourseView courseview(String id);

    @ApiOperation("课程视图预览")
    CoursePublishResult coursePreview(String id);


}

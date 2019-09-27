package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseViewControllerApi;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("course/courseview")
public class CourseViewController extends BaseController implements CourseViewControllerApi {

    @Autowired
    private CourseService courseService;


    /**
     * 查询课程预览所需数据
     *
     * @param id 课程ID
     * @return CourseView
     */
    @Override
    @GetMapping("{id}")
    public CourseView courseview(@PathVariable String id) {
        return courseService.getCourseView(id);
    }

    /**
     * 预览课程
     *
     * @param id 课程ID
     * @return CoursePublishResult
     */
    @Override
    @PostMapping("preview/{id}")
    public CoursePublishResult coursePreview(@PathVariable String id) {
        String preview = courseService.preview(id);
        if (StringUtils.isBlank(preview)) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        return new CoursePublishResult(CommonCode.SUCCESS, preview);
    }
}

package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseBaseControllerApi;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseBaseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("course/coursebase")
public class CourseBaseController extends BaseController implements CourseBaseControllerApi {

    @Autowired
    private CourseBaseService courseBaseService;


    @Override
//    @GetMapping("list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable int page,
                                        @PathVariable int size,
                                        CourseListRequest queryPageRequest) {
        return courseBaseService.findList(page, size, queryPageRequest);
    }

    /**
     * 新增课程基本信息
     *
     * @param courseBase 课程基本信息
     */
    @Override
    @PostMapping("add")
    public AddCourseResult addCourse(@RequestBody CourseBase courseBase) {
        isNullOrEmpty(courseBase, CommonCode.PARAMS_ERROR);
        CourseBase add = courseBaseService.add(courseBase);
        isNullOrEmpty(add, CommonCode.SERVER_ERROR);
        return new AddCourseResult(CommonCode.SUCCESS, add.getId());
    }

    /**
     * 编辑课程基本信息
     *
     * @param courseBase 课程基本信息
     */
    @Override
    @PutMapping("edit")
    public AddCourseResult editCourse(@RequestBody CourseBase courseBase) {
        isNullOrEmpty(courseBase, CommonCode.PARAMS_ERROR);
        courseBaseService.edit(courseBase);
        return null;
    }

    /**
     * 按课程ID查询课程基本信息
     *
     * @param courseId
     */
    @Override
    @GetMapping("{courseId}")
    public CourseBaseResult findById(@PathVariable String courseId) {
        isNullOrEmpty(courseId, CommonCode.PARAMS_ERROR);
        CourseBase courseBase = courseBaseService.findById(courseId);
        isNullOrEmpty(courseBase, CourseCode.COURSE_NOT_EXIST);
        return CourseBaseResult.SUCCESS(courseBase);
    }

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable int page, @PathVariable int size,
                                              CourseListRequest courseListRequest) {
        // 获取当前用户信息
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        // 使用companyId查询数据
        return courseBaseService.findCourseList(userJwt.getCompanyId(), page, size, courseListRequest);
    }
}

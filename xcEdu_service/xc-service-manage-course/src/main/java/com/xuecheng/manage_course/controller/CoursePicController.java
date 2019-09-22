package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CoursePicControllerApi;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CoursePicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("course/coursepic")
public class CoursePicController extends BaseController implements CoursePicControllerApi {

    @Autowired
    private CoursePicService coursePicService;

    /**
     * 新增/更新课程图片
     *
     * @param courseId 课程ID
     * @param pic      图片ID
     * @return CoursePic
     */
    @Override
    @PostMapping("add")
    public CoursePic saveCoursePic(@RequestParam String courseId,
                                   @RequestParam String pic) {
        isNullOrEmpty(courseId, CommonCode.PARAMS_ERROR);
        isNullOrEmpty(pic, CommonCode.PARAMS_ERROR);
        return coursePicService.save(courseId, pic);
    }

    /**
     * 查询课程图片
     *
     * @param courseId 课程ID
     * @return CoursePic
     */
    @Override
    @GetMapping("list/{courseId}")
    public CoursePic findById(@PathVariable String courseId) {
        isNullOrEmpty(courseId, CommonCode.PARAMS_ERROR);
        return coursePicService.findById(courseId);
    }

    /**
     * 查询课程图片
     *
     * @param courseId 课程ID
     * @return CoursePic
     */
    @Override
    @DeleteMapping("delete")
    public ResponseResult deleteById(@RequestParam String courseId) {
        isNullOrEmpty(courseId, CommonCode.PARAMS_ERROR);
        coursePicService.deleteById(courseId);
        return ResponseResult.SUCCESS();
    }
}

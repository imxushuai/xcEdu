package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.service.BaseService;
import com.xuecheng.manage_course.dao.CoursePicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CoursePicService extends BaseService {

    @Autowired
    private CoursePicRepository coursePicRepository;

    /**
     * 新增/更新课程图片
     *
     * @param courseId 课程ID
     * @param pic      图片ID
     * @return CoursePic
     */
    public CoursePic save(String courseId, String pic) {
        CoursePic result = null;
        // 查询课程图片
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(courseId);
        if (optionalCoursePic.isPresent()) {
            // 更新课程图片
            result = optionalCoursePic.get();
            result.setPic(pic);
        } else {
            // 新增课程图片
            result = new CoursePic();
            result.setCourseid(courseId);
            result.setPic(pic);
        }
        return coursePicRepository.save(result);
    }

    /**
     * 查询课程图片
     *
     * @param courseId 课程ID
     * @return CoursePic
     */
    public CoursePic findById(String courseId) {
        // 查询课程图片
        Optional<CoursePic> optionalCoursePic = coursePicRepository.findById(courseId);
        if (!optionalCoursePic.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOT_EXIST);
        }
        return optionalCoursePic.get();
    }

    /**
     * 删除课程图片
     *
     * @param courseId 课程ID
     * @return
     */
    public void deleteById(String courseId) {
        coursePicRepository.deleteById(courseId);
    }
}

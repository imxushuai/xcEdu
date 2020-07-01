package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.search.EsTeachplanMediaPub;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class LearningService {

    @Autowired
    CourseSearchClient courseSearchClient;

    @Autowired
    private XcLearningCourseRepository xcLearningCourseRepository;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    /**
     * 获取课程视频信息
     *
     * @param courseId    课程ID
     * @param teachplanId 课程计划ID
     * @return GetMediaResult
     */
    public GetMediaResult getMedia(String courseId, String teachplanId) {

        // TODO 校验学生的学习权限, 是否资费

        // 调用搜索服务查询
        EsTeachplanMediaPub teachplanMediaPub = courseSearchClient.getMedia(teachplanId);
        if (teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMedia_url())) {
            //获取视频播放地址出错
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        return new GetMediaResult(CommonCode.SUCCESS, teachplanMediaPub.getMedia_url());
    }

    /**
     * 添加选课记录
     *
     * @param userId    选课用户
     * @param courseId  课程ID
     * @param valid     是否有效
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param xcTask    选课任务
     * @return 选课结果
     */
    @Transactional
    public ResponseResult addcourse(String userId, String courseId, String valid,
                                    Date startTime, Date endTime, XcTask xcTask) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }

        if (StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_USERISNULL);
        }

        if (xcTask == null || StringUtils.isEmpty(xcTask.getId())) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_TASKISNULL);
        }

        //查询历史任务
        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if (optional.isPresent()) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findXcLearningCourseByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse == null) {//没有选课记录则添加
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        } else {//有选课记录则更新日期
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        }

        //向历史任务表播入记录
        Optional<XcTaskHis> optionalXcTaskHis = xcTaskHisRepository.findById(xcTask.getId());
        if (!optionalXcTaskHis.isPresent()) {
            //添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

}
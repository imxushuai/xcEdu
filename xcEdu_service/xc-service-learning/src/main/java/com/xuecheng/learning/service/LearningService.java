package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.search.EsTeachplanMediaPub;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.CourseSearchClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LearningService {

    @Autowired
    CourseSearchClient courseSearchClient;

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
}
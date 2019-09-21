package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CoursePlanMapper {

    /**
     * 查询课程计划列表
     *
     * @param courseId 课程ID
     * @return TeachplanNode
     */
    TeachplanNode findList(String courseId);

}

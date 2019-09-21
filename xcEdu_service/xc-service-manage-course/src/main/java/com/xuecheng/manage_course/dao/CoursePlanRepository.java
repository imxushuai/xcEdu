package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CoursePlanRepository extends CrudRepository<Teachplan, String> {

    /**
     * 查询课程指定父ID的所有节点列表
     *
     * @param courseId 课程ID
     * @param parentId 父ID
     * @return List<Teachplan>
     */
    List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}

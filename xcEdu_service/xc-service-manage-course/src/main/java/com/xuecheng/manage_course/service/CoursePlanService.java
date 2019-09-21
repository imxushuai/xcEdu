package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.service.BaseService;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CoursePlanMapper;
import com.xuecheng.manage_course.dao.CoursePlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 课程计划Service
 */
@Slf4j
@Service
public class CoursePlanService extends BaseService {

    @Autowired
    private CoursePlanRepository coursePlanRepository;

    @Autowired
    private CoursePlanMapper coursePlanMapper;

    @Autowired
    private CourseBaseRepository courseBaseRepository;


    /**
     * 查询指定课程的课程ID
     *
     * @param courseId 课程ID
     * @return TeachPlanNode
     */
    public TeachplanNode findList(String courseId) {
        return coursePlanMapper.findList(courseId);
    }

    /**
     * 新增课程计划
     *
     * @param teachplan 课程计划
     * @return Teachplan
     */
    public Teachplan add(Teachplan teachplan) {
        Teachplan root = getTeachplanRoot(teachplan.getCourseid());

        // 设置父ID
        if (StringUtils.isBlank(teachplan.getParentid())) {
            teachplan.setParentid(root.getId());
        }
        // 设置节点级别
        if (root.getId().equals(teachplan.getParentid())) {// 第二级别
            teachplan.setGrade("2");
        } else {// 第三级别
            teachplan.setGrade("3");
        }

        return coursePlanRepository.save(teachplan);
    }

    /**
     * 编辑课程计划
     *
     * @param teachplan 课程计划
     * @return Teachplan
     */
    public Teachplan edit(Teachplan teachplan) {
        Optional<Teachplan> optionalTeachplan = coursePlanRepository.findById(teachplan.getId());
        if (!optionalTeachplan.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOT_EXIST);
        }
        Teachplan _teachplan = optionalTeachplan.get();
        _teachplan.setGrade(teachplan.getGrade());
        _teachplan.setPname(teachplan.getPname());
        _teachplan.setCourseid(teachplan.getCourseid());
        _teachplan.setDescription(teachplan.getDescription());
        _teachplan.setOrderby(teachplan.getOrderby());
        _teachplan.setStatus(teachplan.getStatus());
        _teachplan.setTimelength(teachplan.getTimelength());

        Teachplan root = getTeachplanRoot(teachplan.getCourseid());

        // 设置父ID
        if (StringUtils.isBlank(teachplan.getParentid())) {
            _teachplan.setParentid(root.getId());
        }

        return coursePlanRepository.save(_teachplan);
    }

    /**
     * 查询指定课程的课程计划根节点
     *
     * @param courseId 课程计划
     * @return Teachplan
     */
    public Teachplan getTeachplanRoot(String courseId) {
        // 查询课程基本信息
        Optional<CourseBase> courseBase = courseBaseRepository.findById(courseId);
        if (!courseBase.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOT_EXIST);
        }

        // 查询根节点下的所有二级节点
        List<Teachplan> secondaryTeachplanList = coursePlanRepository.findByCourseidAndParentid(courseBase.get().getId(), "0");
        if (secondaryTeachplanList == null || secondaryTeachplanList.isEmpty()) {// 若不存在根节点
            // 创建根节点
            Teachplan root = new Teachplan();
            root.setCourseid(courseBase.get().getId());
            root.setPname(courseBase.get().getName());
            root.setParentid("0");// 根节点
            root.setGrade("1");// 1级菜单
            root.setStatus("0");// 未发布

            coursePlanRepository.save(root);
            return root;
        }

        return secondaryTeachplanList.get(0);
    }

    /**
     * 查询指定ID的课程计划
     *
     * @param teachplanId 课程计划ID
     * @return Teachplan
     */
    public Teachplan findById(String teachplanId) {
        Optional<Teachplan> optionalTeachplan = coursePlanRepository.findById(teachplanId);
        if (!optionalTeachplan.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOT_EXIST);
        }
        return optionalTeachplan.get();
    }

    /**
     * 删除指定ID的课程计划
     *
     * @param teachplanId 课程计划ID
     */
    public void deleteById(String teachplanId) {
        coursePlanRepository.deleteById(teachplanId);
    }
}

package com.xuecheng.manage_course.service;

import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.service.BaseService;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CourseBaseService extends BaseService {

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 新增课程基本信息
     *
     * @param courseBase 课程基本信息
     * @return CourseBase
     */
    public CourseBase add(CourseBase courseBase) {
        // 新增
        return courseBaseRepository.save(courseBase);
    }

    /**
     * 编辑课程基本信息
     *
     * @param courseBase 课程基本信息
     * @return CourseBase
     */
    public CourseBase edit(CourseBase courseBase) {
        CourseBase _courseBase = findById(courseBase.getId());
        isNullOrEmpty(_courseBase, CourseCode.COURSE_NOT_EXIST);
        // 更新
        _courseBase.setName(courseBase.getName());
        _courseBase.setMt(courseBase.getMt());
        _courseBase.setSt(courseBase.getSt());
        _courseBase.setGrade(courseBase.getGrade());
        _courseBase.setStudymodel(courseBase.getStudymodel());
        _courseBase.setDescription(courseBase.getDescription());

        return courseBaseRepository.save(_courseBase);
    }

    /**
     * 按ID查询课程基本信息
     *
     * @param courseBaseId 课程ID
     * @return CourseBase
     */
    public CourseBase findById(String courseBaseId) {
        return courseBaseRepository.findById(courseBaseId).orElse(null);
    }

    /**
     * 分页查询课程基本信息
     *
     * @param page             当前页码
     * @param size             每页记录数
     * @param queryPageRequest 查询条件
     * @return QueryResponseResult
     */
    public QueryResponseResult findList(int page, int size, CourseListRequest queryPageRequest) {
        if (page < 0) {
            page = 1;
        }

        // 页码下标从0开始
        page = page - 1;
        CourseBase params = new CourseBase();
        if (StringUtils.isNotBlank(queryPageRequest.getCompanyId())) {
            params.setCompanyId(queryPageRequest.getCompanyId());
        }
        Example<CourseBase> courseBaseExample = Example.of(params);

        // 分页查询
        Page<CourseBase> pageResult = courseBaseRepository.findAll(courseBaseExample, PageRequest.of(page, size));

        QueryResult<CourseBase> queryResult = new QueryResult<>();
        queryResult.setTotal(pageResult.getTotalElements());
        queryResult.setList(pageResult.getContent());

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 查询当前登录用户的课程列表
     *
     * @param companyId         当前用户的所属公司
     * @param page              当前页
     * @param size              每页记录数
     * @param courseListRequest 查询条件
     * @return 课程列表
     */
    public QueryResponseResult findCourseList(String companyId, int page, int size, CourseListRequest courseListRequest) {
        if(courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        // 设置companyid
        courseListRequest.setCompanyId(companyId);
        // 分页
        PageHelper.startPage(page, size);
        // 调用dao
        com.github.pagehelper.Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> list = courseListPage.getResult();
        long total = courseListPage.getTotal();
        QueryResult<CourseInfo> courseInfoQueryResult = new QueryResult<>();
        courseInfoQueryResult.setList(list);
        courseInfoQueryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS,courseInfoQueryResult);
    }
}

package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.service.BaseService;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.config.CoursePublishConfig;
import com.xuecheng.manage_course.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CourseService extends BaseService {

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CoursePlanMapper coursePlanMapper;

    @Autowired
    private CoursePlanRepository coursePlanRepository;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private CoursePublishConfig coursePublishConfig;


    /**
     * 查询课程预览所需数据
     *
     * @param id 课程ID
     * @return CourseView
     */
    public CourseView getCourseView(String id) {
        CourseView result = new CourseView();

        // 查询课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        courseBaseOptional.ifPresent(result::setCourseBase);

        // 查询课程图片
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        coursePicOptional.ifPresent(result::setCoursePic);

        // 查询课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        courseMarketOptional.ifPresent(result::setCourseMarket);

        // 查询课程计划信息
        TeachplanNode teachplanNode = coursePlanMapper.findList(id);
        result.setTeachplanNode(teachplanNode);

        return result;
    }

    /**
     * 课程预览
     *
     * @param id 课程id
     * @return previewUrl
     */
    public String preview(String id) {
        // 查询课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (!courseBaseOptional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOT_EXIST);
        }
        CourseBase courseBase = courseBaseOptional.get();

        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(coursePublishConfig.getSiteId());
        cmsPage.setTemplateId(coursePublishConfig.getTemplateId());
        cmsPage.setPageAliase(courseBase.getName());
        cmsPage.setPageWebPath(coursePublishConfig.getPageWebPath());
        cmsPage.setPagePhysicalPath(coursePublishConfig.getPagePhysicalPath());
        cmsPage.setDataUrl(coursePublishConfig.getDataUrlPre() + courseBase.getId());

        CmsPageResult save = cmsPageClient.save(cmsPage);
        if (save.isSuccess()) {
            return coursePublishConfig.getPreviewUrl() + save.getCmsPage().getPageId();
        }

        return null;
    }
}

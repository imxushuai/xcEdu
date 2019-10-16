package com.xuecheng.search.controller;

import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.domain.search.EsCoursePub;
import com.xuecheng.framework.domain.search.EsTeachplanMediaPub;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("search/course")
public class EsCourseController extends BaseController implements EsCourseControllerApi {

    @Autowired
    private EsCourseService esCourseService;

    @Override
    @GetMapping("list/{page}/{size}")
    public QueryResponseResult list(@PathVariable int page,
                                    @PathVariable int size,
                                    CourseSearchParam courseSearchParam) {
        return esCourseService.findList(page, size, courseSearchParam);
    }

    @Override
    @GetMapping("getall/{id}")
    public Map<String, EsCoursePub> getAll(@PathVariable String id) {
        return esCourseService.getAll(id);
    }

    @Override
    @GetMapping("getmedia/{teachplanId}")
    public EsTeachplanMediaPub getMedia(@PathVariable String teachplanId) {
        //将课程计划id放在数组中，为调用service作准备
        String[] teachplanIds = new String[]{teachplanId};
        //通过service查询ES获取课程媒资信息
        List<EsTeachplanMediaPub> esTeachplanMediaPubList = esCourseService.getMedia(teachplanIds);

        return esTeachplanMediaPubList.isEmpty() ? null : esTeachplanMediaPubList.get(0);
    }
}

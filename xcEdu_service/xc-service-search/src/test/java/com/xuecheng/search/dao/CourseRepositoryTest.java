package com.xuecheng.search.dao;

import com.xuecheng.framework.domain.search.EsCoursePub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 查询
     */
    @Test
    public void testFindOne() {
        Optional<EsCoursePub> coursePub = courseRepository.findById("402885816243d2dd016243f24c030002");
        System.out.println(coursePub.get().toString());
    }

}
package com.xuecheng.search.dao;

import com.xuecheng.framework.domain.search.EsCoursePub;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CourseRepository extends ElasticsearchRepository<EsCoursePub, String> {
}

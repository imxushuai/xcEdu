package com.xuecheng.search.dao;

import com.xuecheng.framework.domain.search.EsTeachplanMediaPub;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsTeachplanMediaPubRepository extends ElasticsearchRepository<EsTeachplanMediaPub, String> {
}

package com.xuecheng.search.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.domain.search.EsCoursePub;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.service.BaseService;
import com.xuecheng.search.config.ElasticsearchConfig;
import com.xuecheng.search.dao.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EsCourseService extends BaseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchConfig elasticsearchConfig;

    /**
     * 查询课程索引
     *
     * @param page              当前页码
     * @param size              每页记录数
     * @param courseSearchParam 查询条件
     * @return QueryResponseResult
     */
    public QueryResponseResult findList(int page, int size, CourseSearchParam courseSearchParam) {
        if (page < 0) {
            page = 1;
        }
        // Spring Data页码都是从0开始
        page = page - 1;

        // 分页查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page, size));

        // 结果过滤
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(elasticsearchConfig.getSourceField().split(","), null));

        // 查询方式
        QueryBuilder queryBuilder = buildBasicQuery(courseSearchParam);
        nativeSearchQueryBuilder.withQuery(queryBuilder);

        // 高亮设置
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name").preTags("<font class='eslight'>").postTags("</font>"));

        AggregatedPage<EsCoursePub> esCoursePubPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), EsCoursePub.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<EsCoursePub> chunk = new ArrayList<>();
                for (SearchHit searchHit : response.getHits()) {
                    if (response.getHits().getHits().length <= 0) {
                        return null;
                    }
                    EsCoursePub coursePub =
                            JSON.parseObject(JSON.toJSONString(searchHit.getSource()), EsCoursePub.class);
                    HighlightField nameField = searchHit.getHighlightFields().get("name");
                    if (nameField != null) {
                        coursePub.setName(nameField.fragments()[0].toString());
                    }

                    chunk.add(coursePub);
                }
                if (chunk.size() > 0) {
                    return new AggregatedPageImpl<>((List<T>) chunk);
                }
                return null;
            }
        });


        // 返回结果
        QueryResult<EsCoursePub> esCoursePubQueryResult = new QueryResult<>(esCoursePubPage.getContent(), esCoursePubPage.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS, esCoursePubQueryResult);
    }

    /**
     * 构建查询
     *
     * @param courseSearchParam 查询条件
     * @return QueryBuilder
     */
    private QueryBuilder buildBasicQuery(CourseSearchParam courseSearchParam) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        // 基础查询
        if (StringUtils.isNotBlank(courseSearchParam.getKeyword())) {
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                    .multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan", "description")
                    .minimumShouldMatch("70%") // 相似度
                    .field("name", 10);// name字段权重占比提高10倍
            queryBuilder.must(multiMatchQueryBuilder);
        }

        // 分类过滤
        if (StringUtils.isNotBlank(courseSearchParam.getMt())) {
            queryBuilder.filter(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        if (StringUtils.isNotBlank(courseSearchParam.getSt())) {
            queryBuilder.filter(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }

        // 难度过滤
        if (StringUtils.isNotBlank(courseSearchParam.getGrade())) {
            queryBuilder.filter(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }

        return queryBuilder;
    }

}

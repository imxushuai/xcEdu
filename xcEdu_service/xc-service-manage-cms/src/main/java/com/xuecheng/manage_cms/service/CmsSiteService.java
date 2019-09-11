package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CmsSiteService {

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    public QueryResponseResult findAll() {
        List<CmsSite> cmsSiteList = cmsSiteRepository.findAll();
        QueryResult<CmsSite> queryResult = new QueryResult<>(cmsSiteList, cmsSiteList.size());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

}

package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CmsPageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    /**
     * 分页查询页面列表
     *
     * @param page 当前页码
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        // 页面别名模糊查询
        ExampleMatcher pageAliaseMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        CmsPage params = new CmsPage();
        // 站点ID
        if (StringUtils.isNotBlank(queryPageRequest.getSiteId())) {
            params.setSiteId(queryPageRequest.getSiteId());
        }
        // 页面别名
        params.setPageAliase(queryPageRequest.getPageAliase());

        // 构建查询条件
        Example<CmsPage> cmsPageExample = Example.of(params, pageAliaseMatcher);

        if (page <= 0) {
            page = 1;
        }
        // 页码从0开始
        page = page - 1;
        // 查询
        Page<CmsPage> pageResult = cmsPageRepository.findAll(cmsPageExample, PageRequest.of(page, size));
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(pageResult.getContent());
        cmsPageQueryResult.setTotal(pageResult.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /**
     * 新增页面
     *
     * @param cmsPage 页面
     */
    public CmsPage add(CmsPage cmsPage) {
        return cmsPageRepository.insert(cmsPage);
    }

    /**
     * 按ID查询页面
     *
     * @param pageId 页面ID
     */
    public CmsPage findByPageId(String pageId) {
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(pageId);
        return cmsPage.orElse(null);
    }

    /**
     * 编辑页面
     *
     * @param cmsPage 页面ID
     */
    public CmsPage edit(CmsPage cmsPage) {
        // 查询
        if (cmsPage != null && StringUtils.isNotBlank(cmsPage.getPageId())) {
            Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(cmsPage.getPageId());
            if (optionalCmsPage.isPresent()) {
                CmsPage one = optionalCmsPage.get();
                // 执行更新
                one.setTemplateId(cmsPage.getTemplateId());
                one.setSiteId(cmsPage.getSiteId());
                one.setPageAliase(cmsPage.getPageAliase());
                one.setPageName(cmsPage.getPageName());
                one.setPageWebPath(cmsPage.getPageWebPath());
                one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
                // 保存
                return cmsPageRepository.save(one);
            }
        }

        return null;
    }

    /**
     * 删除指定ID的页面
     *
     * @param pageId 页面ID
     */
    public void deleteById(String pageId) {
        // 查询
        cmsPageRepository.deleteById(pageId);
    }
}

package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private CmsPageService cmsPageService;

    @Override
    @GetMapping("list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,
                                        @PathVariable("size") int size,
                                        QueryPageRequest queryPageRequest) {
        return cmsPageService.findList(page, size, queryPageRequest);
    }

    @Override
    @PostMapping
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.PARAMS_ERROR);
        }
        // 校验是否已存在
        CmsPage _cmsPage = cmsPageService.findBySiteIdAndPageNameAndPageWebPath(cmsPage.getSiteId(), cmsPage.getPageName(), cmsPage.getPageWebPath());
        if (_cmsPage != null) {
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        return new CmsPageResult(CommonCode.SUCCESS, cmsPageService.add(cmsPage));
    }

    @Override
    @GetMapping("/{pageId}")
    public CmsPageResult getCmsPage(@PathVariable("pageId") String pageId) {
        CmsPage cmsPage = cmsPageService.findByPageId(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_EDITPAGE_NOTEXISTS);
        }
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    @Override
    @DeleteMapping("/{pageId}")
    public CmsPageResult deleteById(@PathVariable("pageId") String pageId) {
        if (StringUtils.isBlank(pageId)) {
            ExceptionCast.cast(CommonCode.PARAMS_ERROR);
        }
        cmsPageService.deleteById(pageId);
        return new CmsPageResult(CommonCode.SUCCESS, null);
    }

    /**
     * 页面发布
     *
     * @param pageId 页面ID
     */
    @Override
    @GetMapping("post/{pageId}")
    public ResponseResult postPage(@PathVariable String pageId) {
        return cmsPageService.postPage(pageId);
    }

    @Override
    @PutMapping
    public CmsPageResult edit(@RequestBody CmsPage cmsPage) {
        if (cmsPage == null) {
            ExceptionCast.cast(CommonCode.PARAMS_ERROR);
        }
        CmsPage edit = cmsPageService.edit(cmsPage);
        if (edit == null) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        return new CmsPageResult(CommonCode.SUCCESS, edit);
    }


}

package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsPageService;
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
        return new CmsPageResult(CommonCode.SUCCESS, cmsPageService.add(cmsPage));
    }

    @Override
    @GetMapping("/{pageId}")
    public CmsPageResult getCmsPage(@PathVariable("pageId") String pageId) {
        CmsPage cmsPage = cmsPageService.findByPageId(pageId);
        if (cmsPage == null) {
            return new CmsPageResult(CmsCode.CMS_EDITPAGE_NOTEXISTS, null);
        }
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    @Override
    @DeleteMapping("/{pageId}")
    public CmsPageResult deleteById(@PathVariable("pageId") String pageId) {
        try {
            cmsPageService.deleteById(pageId);
        } catch (Exception e) {
            return new CmsPageResult(CommonCode.FAIL, null);
        }
        return new CmsPageResult(CommonCode.SUCCESS, null);
    }

    @Override
    @PutMapping
    public CmsPageResult edit(@RequestBody CmsPage cmsPage) {
        CmsPage edit = cmsPageService.edit(cmsPage);
        if (edit != null) {
            return new CmsPageResult(CommonCode.SUCCESS, edit);
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }


}

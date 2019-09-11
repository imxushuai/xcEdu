package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsSiteControllerApi;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.response.CmsSiteResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cms/site")
public class CmsSiteController implements CmsSiteControllerApi {

    @Autowired
    private CmsSiteService cmsSiteService;

    @Override
    @GetMapping
    public QueryResponseResult findAll() {
        return cmsSiteService.findAll();
    }

    @Override
    @GetMapping("list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,
                                        @PathVariable("size") int size) {
        return cmsSiteService.findList(page, size);
    }

    @Override
    @PostMapping
    public CmsSiteResult add(@RequestBody CmsSite cmsSite) {
        return new CmsSiteResult(CommonCode.SUCCESS, cmsSiteService.add(cmsSite));
    }

    @Override
    @GetMapping("/{siteId}")
    public CmsSiteResult getCmsSite(@PathVariable("siteId") String siteId) {
        CmsSite cmsSite = cmsSiteService.findBySiteId(siteId);
        if (cmsSite == null) {
            return new CmsSiteResult(CommonCode.FAIL, null);
        }
        return new CmsSiteResult(CommonCode.SUCCESS, cmsSite);
    }

    @Override
    @DeleteMapping("/{siteId}")
    public CmsSiteResult deleteById(@PathVariable("siteId") String siteId) {
        try {
            cmsSiteService.deleteById(siteId);
        } catch (Exception e) {
            return new CmsSiteResult(CommonCode.FAIL, null);
        }
        return new CmsSiteResult(CommonCode.SUCCESS, null);
    }

    @Override
    @PutMapping
    public CmsSiteResult edit(@RequestBody CmsSite cmsSite) {
        CmsSite edit = cmsSiteService.edit(cmsSite);
        if (edit != null) {
            return new CmsSiteResult(CommonCode.SUCCESS, edit);
        }
        return new CmsSiteResult(CommonCode.FAIL, null);
    }


}

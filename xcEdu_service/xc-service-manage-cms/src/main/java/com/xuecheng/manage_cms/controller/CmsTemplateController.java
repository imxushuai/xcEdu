package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsTemplateResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {

    @Autowired
    private CmsTemplateService cmsTemplateService;

    @Override
    @GetMapping
    public QueryResponseResult findAll() {
        return cmsTemplateService.findAll();
    }

    @Override
    @GetMapping("list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,
                                        @PathVariable("size") int size) {
        return cmsTemplateService.findList(page, size);
    }

    @Override
    @PostMapping
    public CmsTemplateResult add(@RequestBody CmsTemplate cmsTemplate) {
        return new CmsTemplateResult(CommonCode.SUCCESS, cmsTemplateService.add(cmsTemplate));
    }

    @Override
    @GetMapping("/{templateId}")
    public CmsTemplateResult getCmsTemplate(@PathVariable("templateId") String templateId) {
        CmsTemplate cmsTemplate = cmsTemplateService.findBySiteId(templateId);
        if (cmsTemplate == null) {
            return new CmsTemplateResult(CommonCode.FAIL, null);
        }
        return new CmsTemplateResult(CommonCode.SUCCESS, cmsTemplate);
    }

    @Override
    @DeleteMapping("/{templateId}")
    public CmsTemplateResult deleteById(@PathVariable("templateId") String templateId) {
        try {
            cmsTemplateService.deleteById(templateId);
        } catch (Exception e) {
            return new CmsTemplateResult(CommonCode.FAIL, null);
        }
        return new CmsTemplateResult(CommonCode.SUCCESS, null);
    }

    @Override
    @PutMapping
    public CmsTemplateResult edit(@RequestBody CmsTemplate cmsTemplate) {
        CmsTemplate edit = cmsTemplateService.edit(cmsTemplate);
        if (edit != null) {
            return new CmsTemplateResult(CommonCode.SUCCESS, edit);
        }
        return new CmsTemplateResult(CommonCode.FAIL, null);
    }

}

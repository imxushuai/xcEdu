package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsTemplateResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.CmsTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        CmsTemplate cmsTemplate = cmsTemplateService.findByTemplateId(templateId);
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
    @PostMapping("upload")
    public String uploadTemplate(@RequestParam("file") MultipartFile file) {
        // 上传文件
        String templateFileId = cmsTemplateService.uploadTemplateFile(file);
        if (StringUtils.isBlank(templateFileId)) {
            ExceptionCast.cast(CmsCode.CMS_TEMPLATE_FILE_UPLOAD_ERROR);
        }
        return templateFileId;
    }

    @Override
    @DeleteMapping("file/remove/{templateFileId}")
    public void removeTemplateFile(@PathVariable String templateFileId) {
        cmsTemplateService.removeTemplateFile(templateFileId);
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

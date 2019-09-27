package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsPageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    private CmsPageService cmsPageService;

    /**
     * CMS页面预览
     *
     * @param pageId 预览的页面ID
     */
    @RequestMapping("cms/preview/{pageId}")
    public void preview(@PathVariable String pageId) {
        // 获取页面内容
        String htmlContent = cmsPageService.genHtml(pageId);
        isNullOrEmpty(htmlContent, CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        // 输出到页面返回
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-type","text/html;charset=utf-8");
            outputStream.write(htmlContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("[CMS页面预览] 预览页面失败，异常信息：{}", e);
        }
    }

}

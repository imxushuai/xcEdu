package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * CMS PAGE API
 */
@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS)
public interface CmsPageClient {

    /**
     * cms page保存
     *
     * @param cmsPage CMS PAGE信息
     * @return CmsPageResult
     */
    @PostMapping("cms/page/save")
    CmsPageResult save(@RequestBody CmsPage cmsPage);

    /**
     * cms page一键发布
     *
     * @param cmsPage CMS PAGE信息
     * @return CmsPostPageResult
     */
    @PostMapping("cms/page/postPageQuick")
    CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);

}

package com.xuecheng.cms_manage_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.cms_manage_client.dao.CmsPageRepository;
import com.xuecheng.cms_manage_client.dao.CmsSiteRepository;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Slf4j
@Service
public class CmsPageService extends BaseService {

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * 保存指定页面ID的html到服务器
     *
     * @param pageId 页面ID
     */
    public void savePageToServerPath(String pageId) {
        CmsPage cmsPage = null;
        // 查询CmsPage
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (!optionalCmsPage.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_EDITPAGE_NOTEXISTS);
        }
        cmsPage = optionalCmsPage.get();

        // 下载文件
        InputStream inputStream = downloadFileFromMongoDB(cmsPage.getHtmlFileId());
        if (inputStream == null) {
            log.error("[页面发布消费方] 下载页面失败, 流对象为null, fileId = [{}]", cmsPage.getHtmlFileId());
            return ;
        }

        // 写入文件
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(new File(cmsPage.getPagePhysicalPath()));
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (FileNotFoundException e) {
            log.error("[页面发布消费方] 文件未找到, 文件路径 = [{}]", cmsPage.getPagePhysicalPath());
        } catch (IOException e) {
            log.error("[页面发布消费方] 将文件写入服务器失败, 文件路径 = [{}]", cmsPage.getPagePhysicalPath());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                inputStream.close();
            } catch (IOException e) {
                log.error("[页面发布消费方] 流对象关闭失败, 错误信息 = ", e);
            }

        }
    }

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件内容
     */
    private InputStream downloadFileFromMongoDB(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        if (gridFSFile == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //获取流中的数据
        try {
            return gridFsResource.getInputStream();
        } catch (IOException ignored) { }
        return null;
    }

}

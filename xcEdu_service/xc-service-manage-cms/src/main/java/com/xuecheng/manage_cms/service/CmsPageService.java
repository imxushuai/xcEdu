package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.service.BaseService;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CmsPageService extends BaseService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CmsTemplateService cmsTemplateService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * 分页查询页面列表
     *
     * @param page             当前页码
     * @param size             每页记录数
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
                one.setDataUrl(cmsPage.getDataUrl());
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

    /**
     * 按站点ID、页面名称以及页面访问路径查询
     *
     * @param siteId      站点ID
     * @param pageName    页面名称
     * @param pageWebPath 访问路径
     */
    public CmsPage findBySiteIdAndPageNameAndPageWebPath(String siteId, String pageName, String pageWebPath) {
        return cmsPageRepository.findBySiteIdAndPageNameAndPageWebPath(siteId, pageName, pageWebPath);
    }

    /**
     * 根据页面ID生成html
     * 流程：
     * 1、静态化程序获取页面的DataUrl
     * 2、静态化程序远程请求DataUrl获取数据模型。
     * 3、静态化程序获取页面的模板信息
     * 4、执行页面静态化
     *
     * @param pageId 页面ID
     */
    public String genHtml(String pageId) {
        String html = null;

        // 获取数据模型
        Map model = getModel(pageId);

        // 获取模板信息
        String templateContent = getTemplate(pageId);

        // 执行静态化
        try {
            // 配置类
            Configuration configuration = new Configuration(Configuration.getVersion());

            // 模板加载器
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("template", templateContent);

            // 配置
            configuration.setTemplateLoader(templateLoader);

            // 获取模板
            Template template = configuration.getTemplate("template");

            // 静态化
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        } catch (IOException e) {
            // 获取模板失败
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        } catch (TemplateException e) {
            // 静态化失败
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_SAVEHTMLERROR);
        }
        return html;
    }

    /**
     * 获取模板内容
     *
     * @param pageId 页面ID
     * @return 模板内容
     */
    private String getTemplate(String pageId) {
        // 查询页面信息
        CmsPage cmsPage = this.findByPageId(pageId);
        isNullOrEmpty(cmsPage, CmsCode.CMS_EDITPAGE_NOTEXISTS);
        isNullOrEmpty(cmsPage.getTemplateId(), CmsCode.CMS_EDITPAGE_NOTEXISTS);

        // 查询模板数据
        CmsTemplate cmsTemplate = cmsTemplateService.findByTemplateId(cmsPage.getTemplateId());
        isNullOrEmpty(cmsTemplate, CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        // 查询模板文件信息
        isNullOrEmpty(cmsTemplate.getTemplateFileId(), CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        // 下载文件
        String fileContent = downloadFileFromMongoDB(cmsTemplate.getTemplateFileId());
        isNullOrEmpty(fileContent, CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);

        return fileContent;
    }

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件内容
     */
    private String downloadFileFromMongoDB(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        if (gridFSFile == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建gridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //获取流中的数据
        String content = null;
        try {
            content = IOUtils.toString(gridFsResource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
        return content;
    }

    /**
     * 根据pageId获取模型数据
     *
     * @param pageId 页面ID
     * @return 模型数据
     */
    private Map getModel(String pageId) {
        // 查询页面信息
        CmsPage cmsPage = this.findByPageId(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_EDITPAGE_NOTEXISTS);
        }
        if (StringUtils.isBlank(cmsPage.getDataUrl())) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        // 获取模型数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(cmsPage.getDataUrl(), Map.class);
        if (forEntity.getBody() == null) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        return forEntity.getBody();
    }

    /**
     * 页面发布
     *
     * @param pageId 页面ID
     */
    @Transactional
    public ResponseResult postPage(String pageId) {
        // 执行静态化
        String htmlContent = genHtml(pageId);
        isNullOrEmpty(htmlContent, CmsCode.CMS_GENERATEHTML_HTMLISNULL);

        // 保存页面
        CmsPage cmsPage = saveHtml(pageId, htmlContent);

        // 发送消息到MQ
        sendMessage(cmsPage.getPageId());

        return ResponseResult.SUCCESS();
    }

    /**
     * 发送消息到MQ
     *
     * @param pageId 页面ID
     */
    private void sendMessage(String pageId) {
        // 查询CmsPage
        CmsPage cmsPage = findByPageId(pageId);
        isNullOrEmpty(cmsPage, CmsCode.CMS_EDITPAGE_NOTEXISTS);

        // 构造消息
        JSONObject message = new JSONObject();
        message.put("pageId", pageId);

        // 发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, cmsPage.getSiteId(), message.toJSONString());
    }

    /**
     * 保存静态页面
     *
     * @param pageId      页面ID
     * @param htmlContent 页面内容
     */
    private CmsPage saveHtml(String pageId, String htmlContent) {
        // 查询CmsPage
        CmsPage cmsPage = findByPageId(pageId);
        isNullOrEmpty(cmsPage, CmsCode.CMS_EDITPAGE_NOTEXISTS);

        // 删除原有html文件
        String htmlFileId = cmsPage.getHtmlFileId();
        if(StringUtils.isNotEmpty(htmlFileId)){
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }

        // 保存生成的html
        InputStream inputStream = IOUtils.toInputStream(htmlContent, StandardCharsets.UTF_8);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());

        // 保存文件ID到CmsPage
        cmsPage.setHtmlFileId(objectId.toString());
        return cmsPageRepository.save(cmsPage);
    }

    /**
     * 保存Cms Page
     *
     * @param cmsPage cmsPage
     * @return CmsPage
     */
    public CmsPage save(CmsPage cmsPage) {
        CmsPage _cmsPage = cmsPageRepository
                .findBySiteIdAndPageNameAndPageWebPath(cmsPage.getSiteId(), cmsPage.getPageName(), cmsPage.getPageWebPath());
        if (_cmsPage == null) {
            // 新增
            cmsPage = add(cmsPage);
        } else {
            // 更新
            cmsPage.setPageId(_cmsPage.getPageId());
            cmsPage = edit(cmsPage);
        }

        return cmsPage;
    }

    /**
     * 静态页面一键发布
     *
     * @param cmsPage 页面信息
     * @return 页面路径url
     */
    public String postPageQuick(CmsPage cmsPage) {
        // 保存Cms Page数据
        CmsPage save = save(cmsPage);
        isNullOrEmpty(save, CommonCode.FAIL);

        // 静态化并保存页面文件
        ResponseResult postPage = postPage(save.getPageId());
        if (!postPage.isSuccess()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }

        // 生成url
        StringBuffer buffer = new StringBuffer();
        Optional<CmsSite> cmsSiteOptional = cmsSiteRepository.findById(save.getSiteId());
        CmsSite cmsSite = cmsSiteOptional.orElse(null);
        isNullOrEmpty(cmsSite, CommonCode.FAIL);

        assert cmsSite != null;
        String siteDomain = cmsSite.getSiteDomain();
        String siteWebPath = cmsSite.getSiteWebPath();
        String pageWebPath = cmsPage.getPageWebPath();
        String pageName = cmsPage.getPageName();

        return buffer
                .append(siteDomain)
                .append(siteWebPath)
                .append(pageWebPath)
                .append(pageName)
                .toString();
    }
}

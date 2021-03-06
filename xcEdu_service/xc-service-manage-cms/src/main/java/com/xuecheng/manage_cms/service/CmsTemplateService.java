package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CmsTemplateService {

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;


    @Autowired
    private GridFsTemplate gridFsTemplate;

    public QueryResponseResult findAll() {
        List<CmsTemplate> cmsTemplateList = cmsTemplateRepository.findAll();
        QueryResult<CmsTemplate> queryResult = new QueryResult<>(cmsTemplateList, cmsTemplateList.size());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 分页查询页面列表
     *
     * @param page 当前页码
     * @param size 每页记录数
     */
    public QueryResponseResult findList(int page, int size) {
        if (page <= 0) {
            page = 1;
        }
        // 页码从0开始
        page = page - 1;
        // 查询
        Page<CmsTemplate> templateResult = cmsTemplateRepository.findAll(PageRequest.of(page, size));
        QueryResult<CmsTemplate> templateQueryResult = new QueryResult<>();
        templateQueryResult.setList(templateResult.getContent());
        templateQueryResult.setTotal(templateResult.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS, templateQueryResult);
    }


    /**
     * 新增模板
     *
     * @param cmsTemplate 模板
     */
    public CmsTemplate add(CmsTemplate cmsTemplate) {
        return cmsTemplateRepository.insert(cmsTemplate);
    }

    /**
     * 按ID查询模板
     *
     * @param templateId 模板ID
     */
    public CmsTemplate findByTemplateId(String templateId) {
        Optional<CmsTemplate> cmsTemplate = cmsTemplateRepository.findById(templateId);
        return cmsTemplate.orElse(null);
    }

    /**
     * 编辑模板
     *
     * @param cmsTemplate 模板
     */
    public CmsTemplate edit(CmsTemplate cmsTemplate) {
        // 查询
        if (cmsTemplate != null && StringUtils.isNotBlank(cmsTemplate.getTemplateId())) {
            Optional<CmsTemplate> optionalCmsTemplate = cmsTemplateRepository.findById(cmsTemplate.getTemplateId());
            if (optionalCmsTemplate.isPresent()) {
                CmsTemplate one = optionalCmsTemplate.get();
                // 执行更新
                one.setSiteId(cmsTemplate.getSiteId());
                one.setTemplateFileId(cmsTemplate.getTemplateFileId());
                one.setTemplateName(cmsTemplate.getTemplateName());
                one.setTemplateParameter(cmsTemplate.getTemplateParameter());
                // 保存
                return cmsTemplateRepository.save(one);
            }
        }

        return null;
    }

    /**
     * 删除指定ID的模板
     *
     * @param templateId 模板ID
     */
    public void deleteById(String templateId) {
        // 删除模板文件
        Optional<CmsTemplate> templateOptional = cmsTemplateRepository.findById(templateId);
        if (templateOptional.isPresent()) {
            Query query = new Query(Criteria.where("_id").is(templateOptional.get().getTemplateFileId()));
            // 删除文件
            gridFsTemplate.delete(query);
            // 删除模板
            cmsTemplateRepository.deleteById(templateId);
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件
     */
    public String uploadTemplateFile(MultipartFile file) {
        try {
            return gridFsTemplate.store(file.getInputStream(), "template").toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 移除文件
     *
     * @param templateFileId 模板文件ID
     */
    public void removeTemplateFile(String templateFileId) {
        Query query = new Query(Criteria.where("_id").is(templateFileId));
        gridFsTemplate.delete(query);
    }
}

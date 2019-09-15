package com.xuecheng.cms_manage_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.cms_manage_client.dao.CmsPageRepository;
import com.xuecheng.cms_manage_client.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ConsumerPostPage {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg) {
        // 解析消息
        Map map = JSON.parseObject(msg, Map.class);
        log.info("[页面发布消费方] 收到消息, 消息内容为: [{}]", map.toString());

        // 获取pageId
        String pageId = (String) map.get("pageId");
        if (StringUtils.isBlank(pageId)) {
            log.error("[页面发布消费方] 收到的pageId为空");
            return ;
        }
        // 下载页面到服务器
        pageService.savePageToServerPath(pageId);
    }

}

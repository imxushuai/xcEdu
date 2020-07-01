package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaskService {

    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询指定时间的前 N 条任务
     *
     * @param updateTime 时间
     * @param n          前N条记录
     * @return task list
     */
    public List<XcTask> findTaskList(Date updateTime, int n) {
        Page<XcTask> taskPage = xcTaskRepository.findByUpdateTimeBefore(updateTime, PageRequest.of(0, n));
        return taskPage.getContent();
    }

    /**
     * 发送消息
     *
     * @param xcTask     任务对象
     * @param ex         交换机id
     * @param routingKey 路由key
     */
    @Transactional
    public void publish(XcTask xcTask, String ex, String routingKey) {
        //查询任务
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if (taskOptional.isPresent()) {
            XcTask task = taskOptional.get();
            //String exchange, String routingKey, Object object
            rabbitTemplate.convertAndSend(ex, routingKey, task);
            //更新任务时间为当前时间
            task.setUpdateTime(new Date());
            xcTaskRepository.save(task);
        }
    }

    /**
     * 获取最新的版本号
     *
     * @param taskId  任务ID
     * @param version 当前版本
     * @return 新的版本号
     */
    @Transactional
    public int getTask(String taskId, int version) {
        return xcTaskRepository.updateTaskVersion(taskId, version);
    }

    /**
     * 删除指定ID的任务并添加到历史任务中
     *
     * @param taskId 任务id
     */
    @Transactional
    public void finishTask(String taskId) {
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            XcTask xcTask = taskOptional.get();
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }


}

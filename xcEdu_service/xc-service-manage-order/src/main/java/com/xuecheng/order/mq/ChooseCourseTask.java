package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 定时发送消息到MQ
 */
@Slf4j
@Component
public class ChooseCourseTask {

    @Autowired
    private TaskService taskService;

    /**
     * 每分钟扫描消息表，发送到MQ
     */
    @Scheduled(fixedDelay = 60000)
    public void sendChooseCourseTask() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        List<XcTask> taskList = taskService.findTaskList(time, 1000);

        // 发送消息
        taskList.forEach(task -> {
            // 校验版本号
            if (taskService.getTask(task.getId(), task.getVersion()) > 0) {
                taskService.publish(task, task.getMqExchange(), task.getMqRoutingkey());
                log.info("[订单微服务] 发送选课消息到MQ, task id :[{}]", task.getId());
            }
        });
    }

    /**
     * 接收选课响应结果
     */
    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE})
    public void receiveFinishChoosecourseTask(XcTask task) throws IOException {
        log.info("receiveChoosecourseTask...{}",task.getId());
        //接收到 的消息id
        String id = task.getId();
        //删除任务，添加历史任务
        taskService.finishTask(id);
    }



}

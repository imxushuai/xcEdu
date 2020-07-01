package com.xuecheng.order.dao;

import com.xuecheng.framework.domain.task.XcTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface XcTaskRepository extends JpaRepository<XcTask, String> {

    Page<XcTask> findByUpdateTimeBefore(Date updateTime, Pageable pageable);

    //更新任务处理时间
    @Modifying
    @Query("update XcTask t set t.updateTime = :updateTime where t.id = :id ")
    int updateTaskTime(@Param(value = "id") String id, @Param(value = "updateTime") Date updateTime);

    //使用乐观锁方式校验任务id和版本号是否匹配，匹配则版本号加1
    @Modifying
    @Query("update XcTask t set t.version = :version+1 where t.id = :id and t.version = :version")
    int updateTaskVersion(@Param(value = "id") String id, @Param(value = "version") int version);

}

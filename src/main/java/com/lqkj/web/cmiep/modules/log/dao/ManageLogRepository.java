package com.lqkj.web.cmiep.modules.log.dao;

import com.lqkj.web.cmiep.modules.log.domain.ManageLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface ManageLogRepository extends JpaRepository<ManageLog, UUID> {

    @Query("select log from ManageLog log where log.createTime>:startTime and log.createTime<:endTime order by log.createTime desc")
    Page<ManageLog> pageByTime(@Param("startTime") Timestamp startTime,
                                  @Param("endTime") Timestamp endTime,
                                  Pageable pageable);

    @Query("select log from ManageLog log where log.createTime>:startTime and log.createTime<:endTime")
    List<ManageLog> findAllByTime(@Param("startTime") Timestamp startTime,
                                     @Param("endTime") Timestamp endTime);

    @Query("select log from ManageLog log order by log.createTime desc")
    Page<ManageLog> findAllByTimeDesc(Pageable pageable);
}

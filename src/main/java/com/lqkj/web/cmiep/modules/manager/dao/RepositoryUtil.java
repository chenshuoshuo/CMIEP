package com.lqkj.web.cmiep.modules.manager.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RepositoryUtil<T>{

    /**
     * 查询 返回集合
     * @param sql
     * @param objects   占位符参数数组
     * @return
     */
    List<T> execSql(String sql, Object[] objects);
    /**
     * 查询 返回集合
     * @param sql
     * @param clazz  返回对象实体类
     * @return
     */
    List<T> executeSql(String sql, Class clazz);
    /**
     * 查询 返回集合
     * @param sql
     * @return
     */
    List<T> executeMapSql(String sql);
    /**
     * 查询单个结果集
     * @param sql
     * @param objects 占位符参数数组
     * @return
     * @throws Exception
     */
    T execSqlSingleResult(String sql, Object[] objects);

    /**
     * 其他查询，不返回对象
     * @param sql
     * @return
     * @throws Exception
     */
    Object execSqlQuery(String sql);

    /**
     * 查询分页集合
     * @param pageable 分页对象  Pageable pageable = new PageRequest(0,10);
     * @param sql
     * @param objects 占位符参数数组
     * @return
     */
    Page<T> execQuerySqlPage(Pageable pageable, String sql, Object[] objects, Class clazz);
}

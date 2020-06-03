package com.lqkj.web.cmiep.modules.manager.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class RepositoryUtilImpl<T> implements RepositoryUtil<T> {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * 查询 返回集合
     * @param sql
     * @param objects   占位符参数数组
     * @return
     */
    public List<T> execSql(String sql, Object[] objects) {
        Query q = this.entityManager.createNativeQuery(sql);
        if (objects != null && objects.length > 0) {
            for(int i = 0; i < objects.length; ++i) {
                q.setParameter(i + 1, objects[i]);
            }
        }

        return q.getResultList();
    }

    @Override
    public List<T> executeSql(String sql, Class clazz) {
        Query q = this.entityManager.createNativeQuery(sql,clazz);
        return q.getResultList();
    }

    @Override
    public List<T> executeMapSql(String sql) {
        Query q = this.entityManager.createNativeQuery(sql);
        return q.getResultList();
    }


    /**
     * 查询单个结果集
     * @param sql
     * @param objects 占位符参数数组
     * @return
     */
    public T execSqlSingleResult(String sql, Object[] objects) {
        Query q = this.entityManager.createNativeQuery(sql);
        if (objects != null && objects.length > 0) {
            for(int i = 0; i < objects.length; ++i) {
                q.setParameter(i + 1, objects[i]);
            }
        }

        return (T)q.getSingleResult();
    }

    @Override
    public Object execSqlQuery(String sql) {
        System.out.println(sql);
        return this.entityManager
                .createNativeQuery(sql, String.class)
                .getResultList()
                .get(0);
    }

    /**
     * 查询分页集合
     * @param pageable 分页对象  Pageable pageable = new PageRequest(0,10);
     * @param sql
     * @param objects 占位符参数数组
     * @return
     */
    public Page<T> execQuerySqlPage(Pageable pageable, String sql, Object[] objects, Class clazz) {
//        NativeQuery nativeQuery = this.entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).openSession().createSQLQuery(sql);
//        NativeQuery countQuery = this.entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).openSession().createSQLQuery("select count(*) from (" + sql + ") foo");

        Query q = this.entityManager.createNativeQuery(sql, clazz);
        Query sizeQuery = this.entityManager.createNativeQuery("select count(*) from (" + sql + ") foo");
        if (objects != null && objects.length > 0) {
            for(int i = 0; i < objects.length; ++i) {
                q.setParameter(i + 1, objects[i]);
                sizeQuery.setParameter(i + 1, objects[i]);
            }
        }

        long size = 0L;
        if (pageable != null) {
            size = Long.valueOf(sizeQuery.getResultList().get(0).toString());
            q.setFirstResult(Integer.parseInt(String.valueOf(pageable.getOffset())));
            q.setMaxResults(pageable.getPageSize());
        }

        return pageable == null ? new PageImpl(q.getResultList()) : new PageImpl(q.getResultList(), pageable, size);
    }
}

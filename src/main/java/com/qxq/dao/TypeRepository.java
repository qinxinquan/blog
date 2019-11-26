package com.qxq.dao;

import com.qxq.po.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type,Long> {

    //通过名字查询分类
    Type findByName(String name);

    //查询分类，按照分类所对应的博客数目倒序
    @Query("select t from Type t")
    List<Type> findTop(Pageable pageable);
}

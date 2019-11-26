package com.qxq.dao;

import com.qxq.po.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {

    //通过名字查询签
    Tag findByName(String name);

    //查询标签，按照标签所对应的博客数目倒序
    @Query("select t from Tag t")
    List<Tag> findTop(Pageable pageable);
}

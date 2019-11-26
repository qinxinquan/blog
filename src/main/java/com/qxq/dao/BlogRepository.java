package com.qxq.dao;

import com.qxq.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog,Long>, JpaSpecificationExecutor<Blog> {

    //查询最新推荐博客
    @Query("select b from Blog b where b.recommend=true")
    List<Blog> findTop(Pageable pageable);

    //根据博客标题或内容进行全局模糊查询
    @Query("select b from Blog b where b.title like ?1 or b.content like ?1")
    Page<Blog> findByQuery(String query,Pageable pageable);

    //更新博客浏览次数
    @Transactional
    @Modifying
    @Query("update Blog b set b.views=b.views+1 where b.id=?1")
    int updateViews(Long id);

    //查询所有的博客年份
    @Query("select function('date_format',b.updateTime,'%Y') as year from Blog b group by function('date_format',b.updateTime,'%Y') order by year desc")
    List<String> findGroupYear();

    //通过年份查询博客
    @Query("select b from Blog b where function('date_format',b.updateTime,'%Y') =?1")
    List<Blog> findByYear(String year);
}

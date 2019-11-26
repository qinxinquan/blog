package com.qxq.dao;

import com.qxq.po.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    //通过一条评论的博客id查询不为子评论的评论
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);
}

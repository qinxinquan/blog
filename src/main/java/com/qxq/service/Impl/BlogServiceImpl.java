package com.qxq.service.Impl;

import com.qxq.NotFoundException;
import com.qxq.dao.BlogRepository;
import com.qxq.po.Blog;
import com.qxq.po.Type;
import com.qxq.service.BlogService;
import com.qxq.util.MarkdownUtils;
import com.qxq.util.MyBeanUtils;
import com.qxq.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).get();
    }

    //查询博客，并将mrakdown文本格式的内容转成HTML问
    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.findById(id).get();
        if(blog==null){
            throw new NotFoundException("该博客不存在");
        }
        Blog b = new Blog();
        //为了不影响数据库中的数据，将查询到的数据复制到新的博客对象中再转换文本格式
        BeanUtils.copyProperties(blog,b);
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(b.getContent()));
        //每浏览一次博客便增加浏览次数
        blogRepository.updateViews(id);
        return b;
    }

    //在博客编辑页面，通过标题，分类，是否推荐进行组合模糊查询
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates=new ArrayList<>();
                if(!"".equals(blog.getTitle())&&blog.getTitle()!=null){
                    predicates.add(cb.like(root.<String>get("title"),"%"+blog.getTitle()+"%"));
                }
                if(blog.getTypeId()!=null){
                    predicates.add(cb.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                if(blog.isRecommend()){
                    predicates.add(cb.equal(root.<Boolean>get("recommend"),blog.isRecommend()));
                }
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    //通过标签查询对应的所有博客
    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join=root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    //根据博客标题或内容进行全局模糊查询
    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }

    //查询最新推荐的博客，按更新时间倒序，条数为size所指定的数量
    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0,size,sort);
        return blogRepository.findTop(pageable);
    }

    //查询博客归档，key为年份，value为年份对应的所有博客
    @Override
    public Map<String, List<Blog>> archiveBlog() {
        Map<String, List<Blog>> map=new HashMap<>();
        List<String> years=blogRepository.findGroupYear();
        for (String year : years) {
            map.put(year,blogRepository.findByYear(year));
        }
        return map;
    }

    //查询博客总数
    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    //新增博客，初始化创建时间、更新时间和浏览次数
    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if(blog.getId()==null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    //更新博客
    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.findById(id).get();
        if(b==null){
            throw new NotFoundException("该博客不存在");
        }
        //将修改页面的表单所提交的数据更新到对应的博客中，没有提交的数据不更新
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        //更新修改时间
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}

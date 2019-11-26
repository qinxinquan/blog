package com.qxq.web;

import com.qxq.NotFoundException;
import com.qxq.service.BlogService;
import com.qxq.service.TagService;
import com.qxq.service.TypeService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    //首页显示
    @GetMapping("/")
    public String index(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC)
                        Pageable pageable,Model model) {
        model.addAttribute("page",blogService.listBlog(pageable));
        model.addAttribute("types",typeService.listTypeTop(6));
        model.addAttribute("tags",tagService.listTagTop(10));
        model.addAttribute("recommendBlogs",blogService.listRecommendBlogTop(8));
//        int i = 9/0;
//        String blog = null;
//        if (blog == null) {
//            throw  new NotFoundException("博客不存在");
//        }
        return "index";
    }

    //搜索博客
    @PostMapping("/search")
    public String search(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC)
                         Pageable pageable,@RequestParam String query, Model model) {
        model.addAttribute("page",blogService.listBlog("%"+query+"%",pageable));
        model.addAttribute("query",query);
        return "search";
    }

    //浏览博客
    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id,Model model) {
        model.addAttribute("blog",blogService.getAndConvert(id));
        return "blog";
    }

    //局部刷新页面底部的最新博客
    @GetMapping("/footer/newblogs")
    public String newblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }
}

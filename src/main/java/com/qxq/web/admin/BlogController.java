package com.qxq.web.admin;

import com.qxq.po.Blog;
import com.qxq.po.User;
import com.qxq.service.BlogService;
import com.qxq.service.TagService;
import com.qxq.service.TypeService;
import com.qxq.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String LIST="admin/blogs";
    private static final String INPUT="admin/blogs-input";
    private static final String REDIRECT_LIST="redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    //博客管理页面
    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return LIST;
    }

    //根据条件查询博客，在博客管理页面局部刷新
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 5,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model){
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return "admin/blogs :: blogList";
    }

    //博客新增
    @GetMapping("/blogs/input")
    public String input(Model model){
        model.addAttribute("blog",new Blog());
        setTypeAndTag(model);
        return INPUT;
    }

    private void setTypeAndTag(Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());
    }

    //博客修改
    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        Blog blog = blogService.getBlog(id);
        //将tags转换成tagIds
        blog.init();
        model.addAttribute("blog",blog);
        setTypeAndTag(model);
        return INPUT;
    }

    //新增或保存博客
    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session){
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
        Blog b;
        if(blog.getId()==null){
            b=blogService.saveBlog(blog);
        }else {
            b=blogService.updateBlog(blog.getId(),blog);
        }
        if(b==null){
            attributes.addFlashAttribute("message","操作失败");
        }else {
            attributes.addFlashAttribute("message","操作成功");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message","删除成功");
        return REDIRECT_LIST;
    }
}

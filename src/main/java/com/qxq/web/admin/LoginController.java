package com.qxq.web.admin;

import com.qxq.po.User;
import com.qxq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    UserService userService;

    //跳转到登录页面
    @GetMapping
    public String loginPage(){
        return "admin/login";
    }

    //登录
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes attributes){
        User user = userService.checkUser(username, password);
        if(user!=null){
            user.setPassword(null);
            session.setAttribute("user",user);
            //重定向，防止表单重复提交
            return "redirect:/admin/index.html";
        }else{
            attributes.addFlashAttribute("message","用户名或密码错误");
            return "redirect:/admin";
        }
    }

    //注销
    @GetMapping("/logout")
    public String logout(HttpSession session){
        //会话中删除用户
        session.removeAttribute("user");
        return "redirect:/admin";
    }
}

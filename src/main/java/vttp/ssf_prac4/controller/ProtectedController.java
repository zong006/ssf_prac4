package vttp.ssf_prac4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import vttp.ssf_prac4.service.LoginUserService;

@Controller
@RequestMapping("/protected")
public class ProtectedController {
    
    @Autowired
    LoginUserService loginUserService;

    @GetMapping("")
    public String displayView1(HttpSession httpSession){
        if (httpSession.getAttribute("authenticated")==null){
            return "redirect:/";
        }
        if ((boolean) httpSession.getAttribute("authenticated")){
            return "/protected/view1";
        }
        return "redirect:/";

    }
}

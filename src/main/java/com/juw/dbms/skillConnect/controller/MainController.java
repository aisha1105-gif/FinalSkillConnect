package com.juw.dbms.skillConnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String showHomePg() {
        return "home"; // This will render home.html
    }

    @GetMapping("/client-sign-in")
    public String showClientPg() {
        return "/client/client-sign-in";
    }

    @GetMapping("/freelancer-sign-in")
    public String showFreelancerPg() {
        return "/freelancer/freelancer-sign-in";
    }

    @GetMapping("/home")
    public String Home() {
        return "home";
    }

    @GetMapping("/aboutUs")
    public String showAboutUs() {
        return "aboutUs";
    }

}

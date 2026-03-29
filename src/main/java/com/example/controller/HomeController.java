package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("message", "Hello from Pure Spring Configuration!");
        model.addAttribute("title", "Pure Spring Example");
        return "home";
    }
    
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about(Model model) {
        model.addAttribute("message", "This is a Pure Spring Application without Spring Boot");
        model.addAttribute("title", "About Page");
        return "about";
    }
}
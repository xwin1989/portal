package com.nx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Neal on 2014-10-09.
 */
@Controller
public class DefaultController {
//    @RequestMapping("/**")
//    public String notFound() {
//        return "errors/404";
//    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

}


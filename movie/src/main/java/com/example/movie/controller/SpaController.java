package com.example.movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 前端頁面網址（不是 /api）一律交給 React 的 index.html
@Controller
public class SpaController {

    @GetMapping({ "/movie/**", "/booking/**", "/orders", "/login", "/admin", "/admin/**" })
    public String forward() {
        return "forward:/index.html";
    }
}

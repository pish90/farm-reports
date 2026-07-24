package com.farmreports.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsoleController {

    @GetMapping({"/console", "/console/"})
    public String redirectToConsole() {
        return "redirect:/console/index.html";
    }
}

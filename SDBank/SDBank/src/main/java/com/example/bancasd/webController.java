package com.example.bancasd;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class webController {

    @GetMapping("/")
    public String home() {
        return "home.html";
    }

    @GetMapping("/transfer")
    public String transfer() {
        return "transfer.html";
    }

}

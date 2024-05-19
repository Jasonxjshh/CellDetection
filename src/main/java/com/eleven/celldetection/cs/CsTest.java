package com.eleven.celldetection.cs;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class CsTest {
    public static void main(String[] args) {
        System.out.println("12");
    }

    @RequestMapping("test")
    @ResponseBody
    public String s1(){
        return "cs1";
    }

    @RequestMapping("login")
    @ResponseBody
    public String s2(){
        System.out.println(1);
        return "cs1";
    }
}


package uga.menik.csx370.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/lots")
public class LotsController {

    @GetMapping
    public ModelAndView page() {
        ModelAndView mv = new ModelAndView("lots_page");
        return mv;
    }
}

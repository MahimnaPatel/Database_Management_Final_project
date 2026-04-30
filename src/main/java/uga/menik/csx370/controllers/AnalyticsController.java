package uga.menik.csx370.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.services.AnalyticsService;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ModelAndView page() {
        ModelAndView mv = new ModelAndView("analytics_page");
        try {
            mv.addObject("busyHours",     analyticsService.getBusiestHours());
            mv.addObject("availableLots", analyticsService.getMostAvailableLots());
            mv.addObject("topReporters",  analyticsService.getTopReporters());
        } catch (SQLException e) {
            mv.addObject("errorMessage", "Could not load analytics data. Please try again.");
        }
        return mv;
    }
}

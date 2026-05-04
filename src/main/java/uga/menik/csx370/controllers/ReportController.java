package uga.menik.csx370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.services.ReportService;
import uga.menik.csx370.services.UserService;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @Autowired
    public ReportController(ReportService reportService, UserService userService) {
        this.reportService = reportService;
        this.userService = userService;
    }

    @GetMapping("/report")
    public ModelAndView reportPage(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "success", required = false) String success) {
        ModelAndView mv = new ModelAndView("report_page");
        try {
            mv.addObject("lots", reportService.getAllLots());
        } catch (Exception e) {
            mv.addObject("errorMessage", "Could not load lot list. Please try again.");
            return mv;
        }
        if (error != null) mv.addObject("errorMessage", error);
        if (success != null) mv.addObject("successMessage", success);
        return mv;
    }

    @PostMapping("/report")
    public String submitReport(
            @RequestParam("lotId") int lotId,
            @RequestParam("packedLevel") String packedLevel,
            @RequestParam(name = "hasOpenSpots", defaultValue = "false") boolean hasOpenSpots,
            @RequestParam(name = "notes", defaultValue = "") String notes) {
        try {
            int userId = userService.getLoggedInUser().getUserId();
            reportService.submitReport(userId, lotId, packedLevel, hasOpenSpots, notes);
            return "redirect:/lots/" + lotId + "/reports";
        } catch (Exception e) {
            String msg = URLEncoder.encode(
                "Could not submit report: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/report?error=" + msg;
        }
    }

    @GetMapping("/lots/{id}/reports")
    public ModelAndView lotReportsPage(@PathVariable("id") int lotId) {
        ModelAndView mv = new ModelAndView("lot_reports_page");
        try {
            String lotName = reportService.getLotName(lotId);
            if (lotName == null) {
                mv.addObject("errorMessage", "Lot not found.");
                return mv;
            }
            mv.addObject("lotName", lotName);
            mv.addObject("lotId", lotId);
            mv.addObject("reports", reportService.getReportsByLot(lotId));
        } catch (Exception e) {
            mv.addObject("errorMessage", "Could not load report history. Please try again.");
        }
        return mv;
    }
}

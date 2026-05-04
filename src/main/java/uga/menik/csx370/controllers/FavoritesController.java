package uga.menik.csx370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.services.FavoritesService;
import uga.menik.csx370.services.UserService;

@Controller
public class FavoritesController {
    private final FavoritesService favoritesService;
    private final UserService userService;

    @Autowired
    public FavoritesController(FavoritesService favoritesService, UserService userService) {
        this.favoritesService = favoritesService;
        this.userService = userService;
    }

    @GetMapping("/favorites")
    public ModelAndView favoritesPage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView modelView = new ModelAndView("favorites_page");
        modelView.addObject("errorMessage", error);

        try {
            int userId = userService.getLoggedInUser().getUserId();
            modelView.addObject("favoriteLots", favoritesService.getFavoriteLots(userId));
        } catch (SQLException e) {
            modelView.addObject("errorMessage", "Failed to load favorites. Please try again.");
        }
        return modelView;
    }

    @PostMapping("/favorites/add")
    public String addFavorite(@RequestParam("lotId") int lotId, @RequestParam(name = "redirect", defaultValue = "/favorites") String redirect) {
        try {
            int userId = userService.getLoggedInUser().getUserId();
            favoritesService.addFavorite(userId, lotId);
        } catch (SQLException e) {
            String message = URLEncoder.encode("Could not add to favorites.", StandardCharsets.UTF_8);
            return "redirect:/favorites?error=" + message;
        }
        return "redirect:" + redirect;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboardPage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView modelView = new ModelAndView("dashboard_page");
        modelView.addObject("errorMessage", error);

        try {
            int userId = userService.getLoggedInUser().getUserId();
            modelView.addObject("recentActivity", favoritesService.getRecentActivity(userId));
            modelView.addObject("packedTrend", favoritesService.getAvgPackedTrend(userId));
        } catch (SQLException e) {
            modelView.addObject("errorMessage", "Failed to load dashboard. Please try again.");
        }

        return modelView;
    }

    @PostMapping("/favorites/remove")
    public String removeFavorite(@RequestParam("lotId") int lotId, @RequestParam(name = "redirect", defaultValue = "/favorites") String redirect) {
        try {
            int userId = userService.getLoggedInUser().getUserId();
            favoritesService.removeFavorite(userId, lotId);
        } catch (SQLException e) {
            String message = URLEncoder.encode("Could not remove from favorites.", StandardCharsets.UTF_8);
            return "redirect:/favorites?error=" + message;
        }

        return "redirect:" + redirect;
    }
}

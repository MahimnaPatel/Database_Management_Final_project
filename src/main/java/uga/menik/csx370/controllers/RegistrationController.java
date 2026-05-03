package uga.menik.csx370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.csx370.services.UserService;
import uga.menik.csx370.models.User;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // --- REGISTRATION LOGIC ---

    @GetMapping("/register")
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("registration_page");
        mv.addObject("errorMessage", error);
        return mv;
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("passwordRepeat") String passwordRepeat,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName) {
        if (password.trim().length() < 3) {
            String msg = URLEncoder.encode("Password must be at least 3 characters.", StandardCharsets.UTF_8);
            return "redirect:/register?error=" + msg;
        }
        if (!password.equals(passwordRepeat)) {
            String msg = URLEncoder.encode("Passwords do not match.", StandardCharsets.UTF_8);
            return "redirect:/register?error=" + msg;
        }
        try {
            boolean ok = userService.registerUser(username, password, firstName, lastName);
            if (ok) {
                return "redirect:/login";
            }
            String msg = URLEncoder.encode("Registration failed. Please try again.", StandardCharsets.UTF_8);
            return "redirect:/register?error=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("An error occurred: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/register?error=" + msg;
        }
    }

    // --- PROFILE LOGIC ---

    @GetMapping("/profile")
    public ModelAndView profilePage(@RequestParam(name = "error", required = false) String error,
                                    @RequestParam(name = "success", required = false) String success) {
        ModelAndView mv = new ModelAndView("profile_page"); // Matches your mustache file name
        
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            return new ModelAndView("redirect:/login");
        }

        mv.addObject("user", currentUser);
        mv.addObject("errorMessage", error);
        mv.addObject("successMessage", success);
        return mv;
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName) {
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            boolean ok = userService.updateProfile(currentUser.getUserId(), firstName, lastName);
            if (ok) {
                String msg = URLEncoder.encode("Profile updated successfully!", StandardCharsets.UTF_8);
                return "redirect:/profile?success=" + msg;
            }
            String msg = URLEncoder.encode("Update failed.", StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("Error: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        }
    }
}
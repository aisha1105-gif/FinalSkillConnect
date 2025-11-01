package com.juw.dbms.skillConnect.controller;

import com.juw.dbms.skillConnect.service.ClientService;
import com.juw.dbms.skillConnect.service.FreelancerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    @Autowired 
    private ClientService clientService;
    @Autowired 
    private FreelancerService freelancerService;

    @GetMapping("/admin-login")
    public String showAdminLogin() {
        return "admin/admin-login";
    }

    @PostMapping("/admin-login")
    public String processAdminLogin(@RequestParam String email,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {
        // Hard-coded credentials
        if ("admin@skillconnect.com".equals(email) && "admin123".equals(password)) {
            session.setAttribute("admin", true);
            return "redirect:/admin-dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "admin/admin-login";
        }
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/admin-login";
        }

        long totalClients = clientService.countAllClients();
        long totalFreelancers = freelancerService.countAllFreelancers();

        model.addAttribute("totalClients", totalClients);
        model.addAttribute("totalFreelancers", totalFreelancers);
        return "admin/admin-dashboard";
    }

    @GetMapping("/admin-logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
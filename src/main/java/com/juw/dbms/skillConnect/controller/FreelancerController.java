package com.juw.dbms.skillConnect.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.juw.dbms.skillConnect.entity.Bidding;
import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.entity.FreelancerSkill;
import com.juw.dbms.skillConnect.entity.Payment;
import com.juw.dbms.skillConnect.entity.Project;
import com.juw.dbms.skillConnect.entity.ProjectStatus;
import com.juw.dbms.skillConnect.entity.SkillCategory;
import com.juw.dbms.skillConnect.repository.FreelancerRepository;
import com.juw.dbms.skillConnect.repository.ProjectStatusRepository;
import com.juw.dbms.skillConnect.service.BiddingService;
import com.juw.dbms.skillConnect.service.FreelancerService;
import com.juw.dbms.skillConnect.service.FreelancerSkillService;
import com.juw.dbms.skillConnect.service.PaymentService;
import com.juw.dbms.skillConnect.service.ProjectService;
import com.juw.dbms.skillConnect.service.SkillCategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class FreelancerController {
    @Autowired
    FreelancerSkillService freelancerSkillService;
    @Autowired
    SkillCategoryService skillCategoryService;
    @Autowired
    ProjectService projectService;
    @Autowired
    BiddingService biddingService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    FreelancerService freelancerService;
    @Autowired
    FreelancerRepository freelancerRepository;
    @Autowired
    ProjectStatusRepository projectStatusRepository;

    @GetMapping("/freelancer-signup")
    public String showSignUpPg(Model model) {
        List<SkillCategory> skills = skillCategoryService.getAllSkillCategories();
        model.addAttribute("skillsList", skills);
        model.addAttribute("freelancer", new Freelancer());
        return "/freelancer/freelancer-signup";
    }

    @PostMapping("/freelancer-signup")
    public String registerFreelancer(@Valid @ModelAttribute Freelancer freelancer, BindingResult result, 
                                    Model model, HttpSession session, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("freelancer", freelancer);
            model.addAttribute("skillsList", skillCategoryService.getAllSkillCategories());
            return "/freelancer/freelancer-signup";
        }

        Optional<Freelancer> savedFreelancerOpt = freelancerService.saveFreelancer(freelancer);

        if (!savedFreelancerOpt.isPresent()) {
            model.addAttribute("error", "Freelancer with that email already exists.");
            model.addAttribute("skillsList", skillCategoryService.getAllSkillCategories());
            return "/freelancer/freelancer-signup";
        }

        Freelancer savedFreelancer = savedFreelancerOpt.get();

        String[] skillIds = request.getParameterValues("skillIds");

        if (skillIds != null) {
            for (String skillIdStr : skillIds) {
                try {
                    Long skillId = Long.parseLong(skillIdStr);
                    SkillCategory skill = skillCategoryService.getSkillCategoryById(skillId);
                    if (skill == null) {
                        System.out.println("SkillCategory not found for ID: " + skillId);
                        continue;
                    }

                    String priceStr = request.getParameter("prices[" + skillId + "]");
                    if (priceStr != null && !priceStr.isEmpty()) {
                        float price = Float.parseFloat(priceStr);
                        FreelancerSkill fs = new FreelancerSkill();
                        fs.setFreelancer(savedFreelancer);
                        fs.setSkillCategory(skill);
                        fs.setPrice(price);
                        freelancerSkillService.saveFreelancerSkill(fs);
                        System.out.println("Saved FreelancerSkill for freelancer ID: " + savedFreelancer.getId() + ", skill ID: " + skillId);
                    } else {
                        System.out.println("Price not provided for skill ID: " + skillId);
                    }
                } catch (Exception e) {
                    System.out.println("Error saving FreelancerSkill for skill ID: " + skillIdStr + " - " + e.getMessage());
                }
            }
        } else {
            System.out.println("No skills selected for freelancer: " + savedFreelancer.getEmail());
        }

        session.setAttribute("freelancer", savedFreelancer);
        return "redirect:/freelancer-home";       
    }

    @GetMapping("/freelancer-login")
    public String showLoginPage(Model model) {
        model.addAttribute("freelancer", new Freelancer());
        return "/freelancer/freelancer-login";  
    }

    @PostMapping("/freelancer-login")
    public String freelancerLogin(@ModelAttribute Freelancer freelancer, HttpSession session, Model model) {
        Optional<Freelancer> freelancerRecord = freelancerService.authenticateFreelancer(freelancer.getEmail(), freelancer.getPassword());
        
        if (freelancerRecord.isPresent()) {
            session.setAttribute("freelancer", freelancerRecord.get());
            return "redirect:/freelancer-home";
        } else {
            model.addAttribute("error", "Invalid password or email");
            return "/freelancer/freelancer-login"; 
        }
    }

    @GetMapping("/freelancer-home")
    public String showFreelancerHomePg(HttpSession session, Model model, HttpServletRequest request) {
        HttpSession currentSession = request.getSession(false);
        
        if (currentSession == null || currentSession.getAttribute("freelancer") == null) {
            return "redirect:/home";
        }

        Freelancer freelancer = (Freelancer) session.getAttribute("freelancer");
        String fname = freelancer.getFirst_name();
        model.addAttribute("fname", fname);
        model.addAttribute("freelancer", freelancer);
        return "/freelancer/freelancer-home";
    }

    @GetMapping("/freelancer-profile")
    public String showFreelancerProfile(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("freelancer") == null) {
            return "redirect:/home";
        }
        
        Freelancer freelancer = (Freelancer) session.getAttribute("freelancer");
        model.addAttribute("freelancer", freelancer);
        return "/freelancer/freelancer-profile";
    } 

    @PostMapping("/freelancer-profile/update")
    public String updateFreelancerProfile(@Valid @ModelAttribute Freelancer updatedFreelancerRecord, BindingResult result,
                                      HttpSession session, RedirectAttributes redirectAttributes,  
                                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("freelancer", updatedFreelancerRecord);
            return "/freelancer/freelancer-profile";
        }
        Freelancer freelancer = (Freelancer) session.getAttribute("freelancer");
        Optional<String> freelancerUpdatedError = freelancerService.updateFreelancer(freelancer, updatedFreelancerRecord);

        if (freelancerUpdatedError.isPresent()) {
            model.addAttribute("error", freelancerUpdatedError.get());
            return "/freelancer/freelancer-profile";
        } else {
            session.setAttribute("freelancer", freelancerService.findById(freelancer).get());
            redirectAttributes.addFlashAttribute("message", "Profile successfully updated!");
            return "redirect:/freelancer-profile";
        }
    }

    @PostMapping("/freelancer-profile/delete")
    public String deletefreelancer(HttpSession session, Model model) {
        Freelancer freelancerToDelete = (Freelancer) session.getAttribute("freelancer");
        Optional<String> deletedFreelancer = freelancerService.deleteFreelancer(freelancerToDelete);

        if (deletedFreelancer.isPresent()) {
            model.addAttribute("error", deletedFreelancer.get());
            return "/freelancer/freelancer-profile";
        } else {
            model.addAttribute("success", "Your Profile was deleted successfully.");
            return "redirect:/home";
        }
    }

    @GetMapping("/freelancer/view-requests")
    public String showFreelancerRequests(Model model, HttpSession session) {
        Freelancer freelancer = (Freelancer) session.getAttribute("freelancer");
        
        if (freelancer == null) {
            return "redirect:/freelancer-login"; 
        }
        
        List<Bidding> bids = biddingService.getAllBidsByFreelancer(freelancer);
        model.addAttribute("bids", bids);
        return "/freelancer/freelancer-requests";
    }

    @PostMapping("/freelancer/complete-project")
    public String completeProject(@RequestParam Long projectId, HttpSession session, RedirectAttributes redirectAttributes) {
        Freelancer freelancer = (Freelancer) session.getAttribute("freelancer");
        if (freelancer == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to complete the project.");
            return "redirect:/freelancer-login";
        }

        Project project = projectService.getProjectById(projectId);
        if (project == null || !project.getFreelancer().getId().equals(freelancer.getId())) {
            redirectAttributes.addFlashAttribute("error", "Invalid project or you are not assigned to it.");
            return "redirect:/freelancer/view-requests";
        }

        if (!"Accepted".equalsIgnoreCase(project.getProjectStatus().getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Project is not in accepted state.");
            return "redirect:/freelancer/view-requests";
        }

        // Set completion date
        Timestamp completionDate = new Timestamp(System.currentTimeMillis());
        project.setCompletion_date(completionDate);

        // Check if project is overdue and calculate penalty
        Timestamp startDate = project.getStart_date();
        String deliveryTime = project.getDelivery_time();
        boolean isOverdue = false;
        long overdueHours = calculateOverdueHours(startDate, completionDate, deliveryTime);

        if (overdueHours > 0) {
            isOverdue = true;
        }

        // Update project status
        ProjectStatus status = new ProjectStatus();
        status.updateStatus(isOverdue ? "Overdue" : "Completed");
        projectStatusRepository.save(status);
        project.setProjectStatus(status);
        projectService.saveProject(project);

        // Update payment
        Payment payment = paymentService.getPaymentByProjectId(projectId);
        if (payment != null) {
            int finalPayment = Integer.parseInt(payment.getFinal_payment());
            if (isOverdue) {
                int penalty = (int) (overdueHours * 10);
                finalPayment = Math.max(0, finalPayment - penalty);
            }
            payment.setFinal_payment(String.valueOf(finalPayment));
            payment.setPayment_status("Awaiting Payment");
            payment.setPaymentDate(LocalDate.now().toString());
            paymentService.savePayment(payment);
        }

        project.setFreelancer(freelancer);
        project.setFreelancerSkill(project.getFreelancerSkill());
        
        // Update freelancer's works completed and ranking
        freelancer.setWorks_completed(freelancer.getWorks_completed() + 1);
        updateFreelancerRanking(freelancer);
        freelancerService.saveFreelancer(freelancer);

        redirectAttributes.addFlashAttribute("message", "Project marked as " + (isOverdue ? "Overdue" : "Completed") + "!");
        return "redirect:/freelancer/view-requests";
    }

    private long calculateOverdueHours(Timestamp startDate, Timestamp completionDate, String deliveryTime) {
        if (startDate == null || completionDate == null || deliveryTime == null) {
            return 0;
        }

        long allowedHours;
        switch (deliveryTime.toLowerCase()) {
            case "3 hours":
                allowedHours = 3;
                break;
            case "2 days":
                allowedHours = 2 * 24;
                break;
            case "anytime":
                return 0;
            default:
                return 0;
        }

        long timeDiffMillis = completionDate.getTime() - startDate.getTime();
        long timeDiffHours = timeDiffMillis / (1000 * 60 * 60);
        return Math.max(0, timeDiffHours - allowedHours);
    }

    private void updateFreelancerRanking(Freelancer freelancer) {
        int worksCompleted = freelancer.getWorks_completed();
        if (worksCompleted <= 5) {
            freelancer.setRanking("Beginner");
        } else if (worksCompleted <= 10) {
            freelancer.setRanking("Amateur");
        } else {
            freelancer.setRanking("Professional");
        }
    }
}
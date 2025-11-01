package com.juw.dbms.skillConnect.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.juw.dbms.skillConnect.entity.Bidding;
import com.juw.dbms.skillConnect.entity.Client;
import com.juw.dbms.skillConnect.entity.Freelancer;
import com.juw.dbms.skillConnect.entity.FreelancerSkill;
import com.juw.dbms.skillConnect.entity.Payment;
import com.juw.dbms.skillConnect.entity.Project;
import com.juw.dbms.skillConnect.entity.ProjectStatus;
import com.juw.dbms.skillConnect.repository.ProjectStatusRepository;
import com.juw.dbms.skillConnect.service.BiddingService;
import com.juw.dbms.skillConnect.service.FreelancerService;
import com.juw.dbms.skillConnect.service.FreelancerSkillService;
import com.juw.dbms.skillConnect.service.PaymentService;
import com.juw.dbms.skillConnect.service.ProjectService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BiddingController {
    @Autowired
    BiddingService biddingService;
    @Autowired
    ProjectService projectService;
    @Autowired
    FreelancerService freelancerService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    FreelancerSkillService freelancerSkillService;
    @Autowired
    ProjectStatusRepository projectStatusRepository;

    @PostMapping("/client/send-project-request")
    public String sendProjectRequest(
            @RequestParam Long projectId,
            @RequestParam(value = "freelancerIds", required = false) List<Long> freelancerIds,
            RedirectAttributes redirectAttributes) {
        
        if (freelancerIds == null || freelancerIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one freelancer");
            return "redirect:/client/send-project-request/" + projectId;
        }
        
        Project project = projectService.getProjectById(projectId);
        
        // Update project status to "Pending"
        ProjectStatus pendingStatus = new ProjectStatus();
        pendingStatus.updateStatus("Pending");
        projectStatusRepository.save(pendingStatus);
        project.setProjectStatus(pendingStatus);
        projectService.saveProject(project);
        
        for (Long freelancerId : freelancerIds) {
            Freelancer freelancer = freelancerService.getFreelancerById(freelancerId);
            
            if (!biddingService.existsByProjectAndFreelancer(project, freelancer)) {
                Bidding bid = new Bidding();
                bid.setProject(project);
                bid.setFreelancer(freelancer);
                bid.setStatus("Pending");
                bid.setBidDate(LocalDateTime.now().toString());
                biddingService.saveBid(bid);
            } else {
                redirectAttributes.addFlashAttribute("error", "A bid has already been sent to " + freelancer.getFirst_name());
                return "redirect:/client/send-project-request/" + projectId; 
            }
        }
        
        redirectAttributes.addFlashAttribute("message", "Project requests sent successfully!");
        return "redirect:/client/view-requests";
    }

    @PostMapping("/client/select-freelancer")
    public String selectFreelancer(@RequestParam Long projectId, @RequestParam Long bidId, HttpSession session, RedirectAttributes redirectAttributes) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to select a freelancer.");
            return "redirect:/client-login";
        }

        Project project = projectService.getProjectById(projectId);
        Bidding bid = biddingService.getBidById(bidId);

        // Validate project and bid
        if (project == null || bid == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid project or bid.");
            return "redirect:/client/view-requests";
        }

        // Check if the project belongs to the client
        if (!project.getClient().getId().equals(client.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to select a freelancer for this project.");
            return "redirect:/client/view-requests";
        }

        // Check if project is in Pending state
        if (!"Pending".equalsIgnoreCase(project.getProjectStatus().getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Cannot select freelancer for a project that is not pending.");
            return "redirect:/client/view-requests";
        }

        // Check if bid is in Accepted state
        if (!"Accepted".equalsIgnoreCase(bid.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Cannot select a freelancer who has not accepted the project.");
            return "redirect:/client/view-requests";
        }

        // Fetch FreelancerSkill for the selected freelancer and project's skill category
        Optional<FreelancerSkill> freelancerSkill = freelancerSkillService.findByFreelancerIdAndSkillCategoryId(
            bid.getFreelancer().getId(), project.getSkillCategory().getId());

        if (!freelancerSkill.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Freelancer skill not found for the selected freelancer.");
            return "redirect:/client/view-requests";
        }

        // Assign freelancer and update project status to "Accepted"
        project.setFreelancer(bid.getFreelancer());
        ProjectStatus acceptedStatus = new ProjectStatus();
        acceptedStatus.updateStatus("Accepted");
        projectStatusRepository.save(acceptedStatus);
        project.setProjectStatus(acceptedStatus);
        project.setSelectedBid(bid);
        project.setStart_date(new java.sql.Timestamp(System.currentTimeMillis()));
        project.setFreelancerSkill(freelancerSkill.get());
        projectService.saveProject(project);

        // Update bid status to 'Selected'
        bid.setStatus("Selected");
        biddingService.saveBid(bid);

        // Update other bids to 'Not Selected'
        List<Bidding> otherBids = biddingService.getBidsByProjectId(projectId);
        for (Bidding otherBid : otherBids) {
            if (!otherBid.getBidId().equals(bidId) && "Accepted".equalsIgnoreCase(otherBid.getStatus())) {
                otherBid.setStatus("Not Selected");
                biddingService.saveBid(otherBid);
            }
        }

        // Create Payment entry
        Payment payment = new Payment();
        payment.setFreelancer(bid.getFreelancer());
        payment.setProject(project);
        payment.setPayment_status("Pending");
        payment.setPaymentDate(LocalDate.now().toString());
        payment.setFinal_payment(String.valueOf(bid.getBidAmount()));
        paymentService.savePayment(payment);

        redirectAttributes.addFlashAttribute("message", "Freelancer selected successfully!");
        return "redirect:/client/view-requests";
    }

    @PostMapping("/freelancer/respond-to-project")
    public String respondToProject(@RequestParam Long bidId,
                                  @RequestParam String response,
                                  @RequestParam(required = false) Integer bidAmount,
                                  RedirectAttributes redirectAttributes) {
        Bidding bid = biddingService.getBidById(bidId);
        
        if (bid == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid bid.");
            return "redirect:/freelancer/freelancer-requests";
        }

        if ("accept".equalsIgnoreCase(response)) {
            if (bidAmount == null || bidAmount <= 0) {
                redirectAttributes.addFlashAttribute("error", "Please enter a valid bid amount");
                return "redirect:/freelancer/view-requests";
            }
            
            bid.setStatus("Accepted");
            bid.setBidAmount(bidAmount);
            bid.setBidDate(LocalDate.now().toString());
        } else {
            bid.setStatus("Rejected");
        }
        
        biddingService.saveBid(bid);
        redirectAttributes.addFlashAttribute("message", "Response submitted successfully!");
        return "redirect:/freelancer/view-requests";
    }
}
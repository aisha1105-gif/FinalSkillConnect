package com.juw.dbms.skillConnect.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.juw.dbms.skillConnect.entity.Bidding;
import com.juw.dbms.skillConnect.entity.Client;
import com.juw.dbms.skillConnect.entity.Payment;
import com.juw.dbms.skillConnect.entity.Project;
import com.juw.dbms.skillConnect.entity.ProjectStatus;
import com.juw.dbms.skillConnect.repository.ProjectStatusRepository;
import com.juw.dbms.skillConnect.service.BiddingService;
import com.juw.dbms.skillConnect.service.ClientService;
import com.juw.dbms.skillConnect.service.FreelancerService;
import com.juw.dbms.skillConnect.service.FreelancerSkillService;
import com.juw.dbms.skillConnect.service.PaymentService;
import com.juw.dbms.skillConnect.service.ProjectService;
import com.juw.dbms.skillConnect.service.SkillCategoryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ClientController {
    @Autowired
    ProjectService projectService;
    @Autowired
    SkillCategoryService skillCategoryService;
    @Autowired
    ClientService clientService;
    @Autowired
    FreelancerSkillService freelancerSkillService;
    @Autowired
    FreelancerService freelancerService;
    @Autowired
    BiddingService biddingService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    ProjectStatusRepository projectStatusRepository;

    // Show Sign Up page
    @GetMapping("/client-signup")
    public String showSignUpPage(Model model) {
        model.addAttribute("client", new Client());
        return "/client/client-signup";
    }

    // Save data from Sign Up page
    @PostMapping("/client-signup")
    public String registerClient(@Valid @ModelAttribute Client client, 
                                BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("client", client); 
            return "/client/client-signup";
        }

        Optional<String> clientExistsError = clientService.saveClient(client);

        if (clientExistsError.isPresent()) {
            model.addAttribute("error", clientExistsError.get()); 
            return "/client/client-signup"; 
        }

        session.setAttribute("client", client);
        return "redirect:/client-home";
    }
    
    // Show Login Page
    @GetMapping("/client-login")
    public String showLoginPage(Model model) {
        model.addAttribute("client", new Client());
        return "/client/client-login";  
    }

    @PostMapping("/client-login")
    public String clientLogin(@ModelAttribute Client client, HttpSession session, Model model) {
        Optional<Client> clientRecord = clientService.authenticateClient(client.getEmail(), client.getPassword());
        
        if (clientRecord.isPresent()) {
            Client existingClientInSession = (Client) session.getAttribute("client");

            if (existingClientInSession != null) {
                existingClientInSession.setId(clientRecord.get().getId());
                existingClientInSession.setFirst_name(clientRecord.get().getFirst_name());
                existingClientInSession.setLast_name(clientRecord.get().getLast_name());
                existingClientInSession.setEmail(clientRecord.get().getEmail());
                existingClientInSession.setPassword(clientRecord.get().getPassword());
                session.setAttribute("client", existingClientInSession);
            } else {
                session.setAttribute("client", clientRecord.get());
            }
            return "redirect:/client-home";
        } else {
            model.addAttribute("error", "Invalid password or email");
            return "/client/client-login";
        }
    }
    
    // Show client dashboard
    @GetMapping("/client-home")
    public String showClientHomePg(HttpSession session, Model model, HttpServletRequest request) {
        HttpSession currentSession = request.getSession(false);

        if (currentSession == null || currentSession.getAttribute("client") == null) {
            return "redirect:/home";
        }

        Client client = (Client) session.getAttribute("client");
        String ClientFname = client.getFirst_name();
        Boolean nullClientName = ClientFname == null;
        model.addAttribute("client", client);
        model.addAttribute("nullClient", nullClientName);
        return "/client/client-home";
    }
        
    @GetMapping("/client-profile")
    public String showClientProfile(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("client") == null) {
            return "redirect:/home";
        }
        
        Client client = (Client) session.getAttribute("client");
        model.addAttribute("client", client);
        return "client/client-profile";
    } 

    @PostMapping("/client-profile/update")
    public String updateClientProfile(@Valid @ModelAttribute Client updatedClientRecord, BindingResult result,
                                      HttpSession session, RedirectAttributes redirectAttributes,  
                                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("client", updatedClientRecord);
            return "/client/client-profile";
        }
        Client client = (Client) session.getAttribute("client");
        Optional<String> clientUpdatedError = clientService.updateClient(client, updatedClientRecord);

        if (clientUpdatedError.isPresent()) {
            model.addAttribute("error", clientUpdatedError.get());
            return "/client/client-profile";
        } else {
            session.setAttribute("client", clientService.findById(client.getId()));
            redirectAttributes.addFlashAttribute("message", "Profile successfully updated!");
            return "redirect:/client-profile";
        }
    }

    @PostMapping("/client-profile/delete")
    public String deleteClient(HttpSession session, Model model) {
        Client clientToDelete = (Client) session.getAttribute("client");
        Optional<String> deletedClient = clientService.deleteClient(clientToDelete);

        if (deletedClient.isPresent()) {
            model.addAttribute("error", deletedClient.get());
            return "/client/client-profile";
        } else {
            model.addAttribute("success", "Your Profile was deleted successfully.");
            return "redirect:/home";
        }
    }

    @GetMapping("/client/view-requests")
    public String showViewRequestsPage(Model model, HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        List<Project> requests = projectService.getProjectsByClientId(client.getId());

        Map<Long, List<Bidding>> bidsMap = new HashMap<>();
        for (Project p : requests) {
            List<Bidding> bids = biddingService.getBidsByProjectId(p.getId());
            bidsMap.put(p.getId(), bids != null ? bids : new ArrayList<>());
        }

        model.addAttribute("requests", requests);
        model.addAttribute("bidsMap", bidsMap);

        return "/client/view-requests";
    }

    @GetMapping("/client/create-request")
    public String showCreateProjectRequestPage(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("skills", skillCategoryService.getAllSkillCategories());
        return "/client/create-project-request";
    }

    @PostMapping("/client/create-request")
    public String handleProjectCreation(@ModelAttribute Project project, HttpSession session) {
        Client client = (Client) session.getAttribute("client");

        Long skillCategoryId = project.getSkillCategory().getId();
        project.setSkillCategory(skillCategoryService.findById(skillCategoryId));
        project.setClient(client);
        ProjectStatus createdStatus = new ProjectStatus();
        createdStatus.updateStatus("Created");
        projectStatusRepository.save(createdStatus);
        project.setProjectStatus(createdStatus);
        projectService.saveProject(project);
        return "redirect:/client/send-project-request/" + project.getId();
    }

    @GetMapping("/client/send-project-request/{projectId}")
    public String showSendProjectPage(@PathVariable Long projectId, Model model) {
        Project project = projectService.getProjectById(projectId);

        List<Object[]> freelancerData = freelancerSkillService
                .getFreelancersAndPricesBySkillCategory(project.getSkillCategory().getId());

        model.addAttribute("project", project);
        model.addAttribute("freelancerData", freelancerData);
        return "/client/send-project-request";
    }

    @PostMapping("/client/pay-project")
    public String payProject(@RequestParam Long projectId, HttpSession session, RedirectAttributes redirectAttributes) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to make a payment.");
            return "redirect:/client-login";
        }

        Project project = projectService.getProjectById(projectId);
        if (project == null || !project.getClient().getId().equals(client.getId())) {
            redirectAttributes.addFlashAttribute("error", "Invalid project or you are not authorized.");
            return "redirect:/client/view-requests";
        }

        if (!"Completed".equalsIgnoreCase(project.getProjectStatus().getStatus()) && 
            !"Overdue".equalsIgnoreCase(project.getProjectStatus().getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Project is not ready for payment.");
            return "redirect:/client/view-requests";
        }

        ProjectStatus deliveredStatus = new ProjectStatus();
        deliveredStatus.updateStatus("Delivered");
        projectStatusRepository.save(deliveredStatus);
        project.setProjectStatus(deliveredStatus);
        projectService.saveProject(project);

        Payment payment = paymentService.getPaymentByProjectId(projectId);
        if (payment != null) {
            payment.setPayment_status("Paid");
            payment.setPaymentDate(LocalDate.now().toString());
            paymentService.savePayment(payment);
        }

        redirectAttributes.addFlashAttribute("message", "Payment completed successfully!");
        return "redirect:/client/view-requests";
    }
}
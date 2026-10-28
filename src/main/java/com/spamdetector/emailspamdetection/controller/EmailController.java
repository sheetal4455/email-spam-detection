
package com.spamdetector.emailspamdetection.controller;

import com.spamdetector.emailspamdetection.entity.Email;
import com.spamdetector.emailspamdetection.entity.User;
import com.spamdetector.emailspamdetection.repository.EmailRepository;
import com.spamdetector.emailspamdetection.repository.UserRepository;
import com.spamdetector.emailspamdetection.service.SpamDetectionService;
import com.spamdetector.emailspamdetection.service.PdfExportService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EmailController {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final SpamDetectionService spamDetectionService;
    private final PdfExportService pdfExportService;

    public EmailController(
            UserRepository userRepository,
            EmailRepository emailRepository,
            SpamDetectionService spamDetectionService,
            PdfExportService pdfExportService) {

        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.spamDetectionService = spamDetectionService;
        this.pdfExportService = pdfExportService;
    }

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Registration page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Register user
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute(
                    "error",
                    "Email is already registered.");
            return "register";
        }

        User user = new User(name, email, password);
        userRepository.save(user);

        return "redirect:/login";
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Login user
    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/dashboard";
        }

        model.addAttribute(
                "error",
                "Invalid email or password.");

        return "login";
    }

    // Dashboard page
    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        addDashboardStatistics(model);
        return "dashboard";
    }

    // Check email for spam
    @PostMapping("/check")
    public String checkEmail(
            @RequestParam String emailContent,
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String subject,
            Model model) {

        if (emailContent == null || emailContent.isBlank()) {
            addDashboardStatistics(model);
            model.addAttribute(
                    "error",
                    "Please enter an email message.");
            return "dashboard";
        }

        // Get detection result
        String result = spamDetectionService.checkSpam(emailContent);

        // Calculate spam score
        double spamScore = spamDetectionService.calculateSpamScore(emailContent);

        // Standardize result labels
        if ("SPAM".equalsIgnoreCase(result)) {
            result = "SPAM";
        } else if ("NOT SPAM".equalsIgnoreCase(result)
                || "Not Spam".equalsIgnoreCase(result)) {
            result = "NOT SPAM";
        }

        // Create email record
        Email email = new Email();
        email.setContent(emailContent);
        email.setResult(result);
        email.setSpamScore(spamScore);

        // Save record in database
        emailRepository.save(email);

        // Update dashboard statistics
        addDashboardStatistics(model);

        // Show result on dashboard
        model.addAttribute("emailContent", emailContent);
        model.addAttribute("sender", sender);
        model.addAttribute("subject", subject);
        model.addAttribute("result", result);
        model.addAttribute("score", spamScore);

        return "dashboard";
    }

    // Email history page
    @GetMapping("/history")
    public String historyPage(Model model) {

        List<Email> emails = emailRepository.findAll();

        model.addAttribute("emails", emails);
        model.addAttribute("totalRecords", emails.size());

        return "history";
    }

    // Export history to PDF
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportHistoryPdf()
            throws Exception {

        List<Email> emails = emailRepository.findAll();

        byte[] pdfBytes = pdfExportService.exportHistory(emails);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=email-detection-history.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Dashboard statistics
    private void addDashboardStatistics(Model model) {

        long totalEmails = emailRepository.count();

        long spamEmails = emailRepository.countByResultIgnoreCase("SPAM");

        long safeEmails = emailRepository.countByResultIgnoreCase("NOT SPAM");

        model.addAttribute("totalEmails", totalEmails);
        model.addAttribute("spamEmails", spamEmails);
        model.addAttribute("safeEmails", safeEmails);
    }
}


package com.spamdetector.emailspamdetection.service;

import org.springframework.stereotype.Service;

@Service
public class SpamDetectionService {

    private final String[] spamWords = {
            "lottery",
            "winner",
            "free",
            "money",
            "offer",
            "click here",
            "cash prize",
            "claim",
            "congratulations",
            "urgent",
            "won"
    };

    public String checkSpam(String emailContent) {
        if (emailContent == null || emailContent.isBlank()) {
            return "Invalid Email";
        }

        return calculateSpamScore(emailContent) >= 2
                ? "SPAM"
                : "NOT SPAM";
    }

    public double calculateSpamScore(String emailContent) {
        if (emailContent == null || emailContent.isBlank()) {
            return 0.0;
        }

        String content = emailContent.toLowerCase();
        double score = 0.0;

        for (String word : spamWords) {
            if (content.contains(word)) {
                score += 1.0;
            }
        }

        return score;
    }
}

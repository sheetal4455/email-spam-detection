
package com.spamdetector.emailspamdetection.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String content;

    private String result;

    @Column(name = "spam_score", nullable = false)
    private double spamScore = 0.0;

    public Email() {
    }

    public Email(String content, String result, double spamScore) {
        this.content = content;
        this.result = result;
        this.spamScore = spamScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public double getSpamScore() {
        return spamScore;
    }

    public void setSpamScore(double spamScore) {
        this.spamScore = spamScore;
    }
}

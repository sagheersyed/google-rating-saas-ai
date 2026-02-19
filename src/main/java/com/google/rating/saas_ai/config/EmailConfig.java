package com.google.rating.saas_ai.config;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "email")
@Validated
public class EmailConfig {

    private String smtpHost = "smtp.gmail.com"; // default
    private Integer smtpPort = 587; // default

    @Email(message = "From email must be a valid email address")
    private String fromEmail = "sagheersyed333@gmail.com"; // dev default

    private String username = "sagheersyed333@gmail.com"; // dev default
    private String password = "uspmoanufgjtjemj"; // dev default

    private boolean enableTls = true;

    // Getters and setters
    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }

    public Integer getSmtpPort() { return smtpPort; }
    public void setSmtpPort(Integer smtpPort) { this.smtpPort = smtpPort; }

    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnableTls() { return enableTls; }
    public void setEnableTls(boolean enableTls) { this.enableTls = enableTls; }
}

// NEW_FILE_CODE
package com.google.rating.saas_ai.service;

import com.google.rating.saas_ai.entity.Business;
import com.google.rating.saas_ai.entity.Review;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:#{null}}")
    private String fromEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReviewResponseNotification(Business business, Review review) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(business.getBusinessEmail());
            helper.setSubject("New Response to Customer Review - " + business.getBusinessName());

            String htmlContent = buildReviewResponseEmailContent(business, review);
            helper.setText(htmlContent, true);

            if (fromEmail != null) {
                helper.setFrom(fromEmail);
            }

            mailSender.send(message);
            logger.info("Review response notification sent to: {}", business.getBusinessEmail());
        } catch (Exception e) {
            logger.error("Failed to send review response notification to: " + business.getBusinessEmail(), e);
        }
    }

    public void sendSubscriptionReminder(Business business) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(business.getBusinessEmail());
            helper.setSubject("Subscription Renewal Reminder - " + business.getBusinessName());

            String htmlContent = buildSubscriptionReminderEmailContent(business);
            helper.setText(htmlContent, true);

            if (fromEmail != null) {
                helper.setFrom(fromEmail);
            }

            mailSender.send(message);
            logger.info("Subscription reminder sent to: {}", business.getBusinessEmail());
        } catch (Exception e) {
            logger.error("Failed to send subscription reminder to: " + business.getBusinessEmail(), e);
        }
    }

    public void sendWelcomeEmail(Business business) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(business.getBusinessEmail());
            helper.setSubject("Welcome to Google Review Auto-Responder - " + business.getBusinessName());

            String htmlContent = buildWelcomeEmailContent(business);
            helper.setText(htmlContent, true);

            if (fromEmail != null) {
                helper.setFrom(fromEmail);
            }

            mailSender.send(message);
            logger.info("Welcome email sent to: {}", business.getBusinessEmail());
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: " + business.getBusinessEmail(), e);
        }
    }

    private String buildReviewResponseEmailContent(Business business, Review review) {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .review-section { background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 15px 0; }
                    .response-section { background-color: #e8f5e8; padding: 15px; border-left: 4px solid #4CAF50; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>New Response to Customer Review</h2>
                    </div>
                    <div class="content">
                        <p>Hello <strong>%s</strong>,</p>
                        
                        <p>We've automatically responded to a new customer review for your business:</p>
                        
                        <div class="review-section">
                            <h3>Customer Review (%d/5 stars)</h3>
                            <p><em>"%s"</em></p>
                        </div>
                        
                        <div class="response-section">
                            <h3>Our Response</h3>
                            <p>%s</p>
                        </div>
                        
                        <p>This response was generated using our AI service to ensure timely engagement with your customers.</p>
                        
                        <p>Thank you for using our Google Review Auto-Responder service!</p>
                        
                        <br>
                        <p>Best regards,<br>
                        The Google Review Auto-Responder Team</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(business.getBusinessName(), review.getRating(), review.getComment(), review.getAiReply());
    }

    private String buildSubscriptionReminderEmailContent(Business business) {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .warning { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Subscription Renewal Reminder</h2>
                    </div>
                    <div class="content">
                        <p>Hello <strong>%s</strong>,</p>
                        
                        <div class="warning">
                            <p>Your subscription to our Google Review Auto-Responder service is expiring soon.</p>
                            <p>Please renew your subscription to continue receiving automatic responses to customer reviews.</p>
                        </div>
                        
                        <p>To renew your subscription, please log in to your account and update your payment information.</p>
                        
                        <p>If you have any questions, please contact our support team.</p>
                        
                        <br>
                        <p>Best regards,<br>
                        The Google Review Auto-Responder Team</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(business.getBusinessName());
    }

    private String buildWelcomeEmailContent(Business business) {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .feature-list { list-style-type: disc; margin-left: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Welcome to Google Review Auto-Responder</h2>
                    </div>
                    <div class="content">
                        <p>Hello <strong>%s</strong>,</p>
                        
                        <p>Welcome to our Google Review Auto-Responder service! We're excited to help you maintain an excellent online presence.</p>
                        
                        <h3>How our service works:</h3>
                        <ul class="feature-list">
                            <li>We monitor Google for new reviews of your business</li>
                            <li>Our AI creates professional, personalized responses</li>
                            <li>Responses are posted automatically to maintain engagement</li>
                            <li>You stay on top of customer feedback without manual effort</li>
                        </ul>
                        
                        <p>Your business is now enrolled in our monitoring system. When new reviews come in, we'll automatically generate and post appropriate responses.</p>
                        
                        <p>Thank you for choosing our service!</p>
                        
                        <br>
                        <p>Best regards,<br>
                        The Google Review Auto-Responder Team</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(business.getBusinessName());
    }
}

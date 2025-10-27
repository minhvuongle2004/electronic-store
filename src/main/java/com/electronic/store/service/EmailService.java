package com.electronic.store.service;

import com.electronic.store.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.verification.url}")
    private String verificationBaseUrl;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.email.from-address}")
    private String fromAddress;

    /**
     * Send email verification
     */
    public void sendEmailVerification(User user) {
        try {
            String verificationUrl = verificationBaseUrl + "?token=" + user.getVerificationToken();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(user.getEmail());
            helper.setSubject("Xác thực tài khoản - Electronic Store");

            String htmlContent = buildVerificationEmailContent(user.getFullName(), verificationUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Verification email sent successfully to: {}", user.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to send verification email to: {}", user.getEmail(), e);
            throw new RuntimeException("Không thể gửi email xác thực", e);
        } catch (Exception e) {
            logger.error("Unexpected error sending email to: {}", user.getEmail(), e);
            throw new RuntimeException("Lỗi không xác định khi gửi email", e);
        }
    }

    /**
     * Build HTML content for verification email
     */
    private String buildVerificationEmailContent(String fullName, String verificationUrl) {
        return """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Xác thực tài khoản</title>
        </head>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
            <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                <h1 style="color: white; margin: 0; font-size: 28px;">
                    <i style="font-size: 32px;">💻</i><br>
                    Electronic Store
                </h1>
            </div>

            <div style="background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; border: 1px solid #e9ecef;">
                <h2 style="color: #495057; margin-bottom: 20px;">Xin chào %s!</h2>

                <p style="margin-bottom: 20px;">Cảm ơn bạn đã đăng ký tài khoản tại Electronic Store. Để hoàn tất quá trình đăng ký, vui lòng xác thực địa chỉ email của bạn.</p>

                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s"
                       style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                              color: white;
                              padding: 15px 30px;
                              text-decoration: none;
                              border-radius: 25px;
                              font-weight: bold;
                              font-size: 16px;
                              display: inline-block;">
                        ✅ Xác thực tài khoản
                    </a>
                </div>

                <p style="color: #6c757d; font-size: 14px; margin-bottom: 15px;">
                    <strong>Lưu ý:</strong> Link xác thực này sẽ hết hạn sau 15 phút.
                </p>

                <p style="color: #6c757d; font-size: 14px; margin-bottom: 20px;">
                    Nếu bạn không thể nhấp vào nút trên, hãy copy và paste đường link sau vào trình duyệt:
                </p>
                <p style="word-break: break-all; background: #e9ecef; padding: 10px; border-radius: 5px; font-size: 14px;">
                    %s
                </p>

                <hr style="border: none; border-top: 1px solid #dee2e6; margin: 30px 0;">

                <p style="color: #6c757d; font-size: 12px; text-align: center; margin: 0;">
                    Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.<br>
                    Email này được gửi tự động, vui lòng không trả lời.
                </p>
            </div>
        </body>
        </html>
        """.formatted(fullName, verificationUrl, verificationUrl);
    }

    /**
     * Send resend verification email (same as original but with different subject)
     */
    public void resendEmailVerification(User user) {
        try {
            String verificationUrl = verificationBaseUrl + "?token=" + user.getVerificationToken();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(user.getEmail());
            helper.setSubject("Gửi lại link xác thực - Electronic Store");

            String htmlContent = buildVerificationEmailContent(user.getFullName(), verificationUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Resend verification email sent successfully to: {}", user.getEmail());

        } catch (MessagingException e) {
            logger.error("Failed to resend verification email to: {}", user.getEmail(), e);
            throw new RuntimeException("Không thể gửi lại email xác thực", e);
        } catch (Exception e) {
            logger.error("Unexpected error resending email to: {}", user.getEmail(), e);
            throw new RuntimeException("Lỗi không xác định khi gửi lại email", e);
        }
    }
}
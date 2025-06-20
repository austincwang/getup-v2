package com.austincwang.getup.auth;

import com.austincwang.getup.email.EmailService;
import com.austincwang.getup.email.EmailTemplateName;
import com.austincwang.getup.role.RoleRepository;
import com.austincwang.getup.user.Token;
import com.austincwang.getup.user.TokenRepository;
import com.austincwang.getup.user.User;
import com.austincwang.getup.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;


    public void register(RegistrationRequest req) throws MessagingException {
        var role = roleRepo.findbyName("USER")
                .orElseThrow(() -> new IllegalStateException("role 'USER' not initialized"));
        var user = User.builder()
                .firstname(req.getFirstname())
                .lastname(req.getLastname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(role))
                .build();
        userRepo.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        // send email
        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        // generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        tokenRepo.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String nums = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(nums.length());
            codeBuilder.append(nums.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}

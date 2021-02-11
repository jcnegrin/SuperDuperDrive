package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

@Controller
@RequestMapping("/credentials")
public class CredentialController {

    private final CredentialService credentialService;
    private final UserService userService;
    private final EncryptionService encryptionService;

    public CredentialController(CredentialService credentialService, UserService userService, EncryptionService encryptionService) {
        this.credentialService =  credentialService;
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    @GetMapping(value = "/{credentialId}")
    public Credential getCredential(@PathVariable Integer credentialId) {
        return credentialService.getCredential(credentialId);
    }

    @PostMapping()
    public String createCredential(
            Authentication authentication,
            @ModelAttribute("newCredential") CredentialForm credentialForm,
            Model model) {

        String userName = authentication.getName();
        User user = userService.getUser(userName);

        if (user != null) {

            String url = credentialForm.getUrl();
            String credentialId = credentialForm.getCredentialId();
            String password = credentialForm.getPassword();

            SecureRandom random = new SecureRandom();
            byte[] key = new byte[16];
            random.nextBytes(key);
            String encodedKey = Base64.getEncoder().encodeToString(key);
            String encryptedPassword = encryptionService.encryptValue(password, encodedKey);

            if (credentialId.isEmpty()) {
                credentialService.addCredential(url, userName, credentialForm.getUserName(), encodedKey, encryptedPassword);
            } else {
                Credential existingCredential = getCredential(Integer.parseInt(credentialId));
                credentialService.updateCredential(existingCredential.getCredentialId(), credentialForm.getUserName(), url, encodedKey, encryptedPassword);
            }

            model.addAttribute("credentials", credentialService.getAllCredentialsByUser(user.getUserId()));
            model.addAttribute("encryptionService", encryptionService);
            model.addAttribute("result", "success");

        }

        return "result";
    }

    @GetMapping(value = "/delete-credential/{credentialId}")
    public String deleteCredential(
            Authentication authentication,
            @PathVariable Integer credentialId,
            Model model) {

        Credential credentialToBeDeleted = credentialService.getCredential(credentialId);

        if (credentialToBeDeleted != null) {
            credentialService.deleteCredential(credentialId);
        }

        String userName = authentication.getName();
        User user = userService.getUser(userName);
        model.addAttribute("credentials", credentialService.getAllCredentialsByUser(user.getUserId()));
        model.addAttribute("result", "success");

        return "result";
    }

}

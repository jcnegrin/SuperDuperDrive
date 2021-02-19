package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FileService fileService;
    private final UserService userService;
    private final NoteService noteService;
    private final CredentialService credentialService;
    private final EncryptionService encryptionService;

    public HomeController(FileService fileService, UserService userService, NoteService noteService,
            CredentialService credentialService, EncryptionService encryptionService) {
        this.fileService = fileService;
        this.userService = userService;
        this.noteService = noteService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @GetMapping()
    public String getHomePage(Authentication authentication,
                              @ModelAttribute("newFile") FileForm newFile,
                              @ModelAttribute("newNote") NoteForm newNote,
                              @ModelAttribute("newCredential") CredentialForm newCredential,
                              Model model) {

        String userName = authentication.getName();
        User user = userService.getUser(userName);
        Integer userId = user.getUserId();
        model.addAttribute("files", this.fileService.getFilesByUser(userId));
        model.addAttribute("notes", this.noteService.getAllNotes(user));
        model.addAttribute("credentials", this.credentialService.getAllCredentialsByUser(userId));
        model.addAttribute("encryptionService", this.encryptionService);
        return "home";
    }

    @PostMapping()
    public String newFile(
            Authentication authentication,
            @ModelAttribute("newFile") FileForm newFile,
            Model model) throws IOException {

        String userName = authentication.getName();
        User user = userService.getUser(userName);
        Integer userId = user.getUserId();

        ArrayList<String> listOfFiles = fileService.getFilesByUser(userId);
        MultipartFile multipartFile = newFile.getFile();
        String fileName = multipartFile.getOriginalFilename();

        if (multipartFile.getOriginalFilename().isEmpty()) {
            model.addAttribute("result", "error");
            model.addAttribute("message", "No file is selected");
            return "result";
        }

        boolean fileIsDuplicate = false;

        for (String file: listOfFiles) {
            if (file.equals(fileName)) {
                fileIsDuplicate = true;
                break;
            }
        }

        if (!fileIsDuplicate) {
            fileService.addFile(multipartFile, userName);
            model.addAttribute("result", "success");
        } else {
            model.addAttribute("result", "error");
            model.addAttribute("message", "You have tried to add a duplicate file.");
        }
        model.addAttribute("files", fileService.getFilesByUser(userId));

        return "result";
    }

    @GetMapping(value = "/get-file/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] getFile(@PathVariable String fileName) {
        return fileService.getFile(fileName).getFileData();
    }

    @GetMapping(value = "/delete-file/{fileName}")
    public String deleteUserFile(Authentication authentication,
                                 @PathVariable String fileName,
                                 Model model) {
        fileService.deleteFile(fileName);
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        Integer userId = user.getUserId();
        model.addAttribute("files", fileService.getFilesByUser(userId));
        model.addAttribute("result", "success");
        return "result";
    }

     /*@DeleteMapping(value = "/delete-file/{fileName}")
    public String deleteUserFile(Authentication authentication,
                                 @PathVariable String fileName,
                                 Model model) {
        fileService.deleteFile(fileName);
        String userName = authentication.getName();
        User user = userService.getUser(userName);
        Integer userId = user.getUserId();
        model.addAttribute("files", fileService.getFilesByUser(userId));
        model.addAttribute("result", "success");
        return "result";
    }*/
}

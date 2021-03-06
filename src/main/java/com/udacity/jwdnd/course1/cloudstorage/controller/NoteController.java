package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @PostMapping("/")
    public String addNote(Authentication authentication,
                          @ModelAttribute("newNote") NoteForm noteForm,
                          Model model) {

        String id = noteForm.getId();
        String title = noteForm.getTitle();
        String description = noteForm.getDescription();
        String userName = authentication.getName();

        if (id.isEmpty()) {
            noteService.addNote(title, description, userName);
        } else {
            Note existingNote = this.getNote(Integer.parseInt(id));
            noteService.updateNote(existingNote.getNoteId(), title, description);
        }

        User user = userService.getUser(userName);
        model.addAttribute("notes", noteService.getAllNotes(user));
        model.addAttribute("result", "success");

        return "result";
    }


    @GetMapping("/{noteId}")
    public Note getNote(@PathVariable Integer noteId) {
        return noteService.getNote(noteId);
    }



    @GetMapping("/delete-note/{noteId}")
    public String deleteOneNote(Authentication authentication,
                                @PathVariable Integer noteId,
                                Model model) {

        Note note = noteService.getNote(noteId);
        if (note != null) {
            noteService.deleteNote(note.getNoteId());
        }

        String userName = authentication.getName();
        User user = userService.getUser(userName);

        model.addAttribute("notes", noteService.getAllNotes(user));
        model.addAttribute("result", "success");

        return "result";

    }


}

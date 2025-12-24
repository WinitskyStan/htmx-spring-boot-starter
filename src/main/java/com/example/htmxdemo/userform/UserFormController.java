package com.example.htmxdemo.userform;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("userForm")
public class UserFormController {

    private final UserFormService userFormService;

    public UserFormController(UserFormService userFormService) {
        this.userFormService = userFormService;
    }

    @ModelAttribute("userForm")
    public UserForm userForm() {
        return userFormService.initializeForm();
    }

    @GetMapping("/userform")
    public String index(@ModelAttribute("userForm") UserForm userForm, Model model) {
        return "userform/userform";
    }

    @PostMapping("/userform/validate")
    public String validateForm(@Valid @ModelAttribute("userForm") UserForm form,
                               BindingResult bindingResult,
                               Model model) {
        return "userform/userform :: form-section";
    }

    @PostMapping("/userform/add-tag")
    public String addTag(@ModelAttribute("userForm") UserForm form,
                         @RequestParam(value = "newTag", required = false) String newTag,
                         Model model) {
        if (newTag != null && !newTag.trim().isEmpty()) {
            userFormService.addTag(form, newTag);
        }
        return "userform/userform :: tag-list";
    }

    @PostMapping("/userform/remove-tag")
    public String removeTag(@ModelAttribute("userForm") UserForm form,
                            @RequestParam("index") int index,
                            Model model) {
        userFormService.removeTag(form, index);
        return "userform/userform :: tag-list";
    }

    @PostMapping("/userform/submit")
    public String submitForm(@Valid @ModelAttribute("userForm") UserForm form,
                             BindingResult bindingResult,
                             SessionStatus sessionStatus,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "userform/userform :: form-section";
        }
        sessionStatus.setComplete();
        return "userform/success :: form-section-success";
    }
}

package com.example.htmxdemo.userform;

import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class UserFormService {

    public UserForm initializeForm() {
        UserForm form = new UserForm();
        form.setName("");
        form.setEmail("");
        form.setPhone("");
        form.setTags(new java.util.ArrayList<>(Arrays.asList("htmx-enthusiast", "spring-boot-dev")));
        return form;
    }

    public void addTag(UserForm form, String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            form.getTags().add(tag.trim());
        }
    }

    public void removeTag(UserForm form, int index) {
        if (index >= 0 && index < form.getTags().size()) {
            form.getTags().remove(index);
        }
    }
}

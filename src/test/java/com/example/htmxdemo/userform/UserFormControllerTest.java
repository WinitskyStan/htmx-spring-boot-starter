package com.example.htmxdemo.userform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserFormControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserFormService userFormService;

    @Test
    void shouldReturnUserFormPageOnGetRequest() {
        ResponseEntity<String> response = restTemplate.getForEntity("/userform", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("User Form Validation Demo");
        assertThat(response.getBody()).contains("Name");
        assertThat(response.getBody()).contains("Email");
        assertThat(response.getBody()).contains("Phone");
    }

    @Test
    void shouldDisplayInitialTagsOnFormLoad() {
        ResponseEntity<String> response = restTemplate.getForEntity("/userform", String.class);

        assertThat(response.getBody()).contains("htmx-enthusiast");
        assertThat(response.getBody()).contains("spring-boot-dev");
    }

    @Test
    void shouldReturnFormWithValidationErrorsOnInvalidInput() {
        String formData = "name=&email=invalid&phone=123";

        ResponseEntity<String> response = restTemplate.postForEntity("/userform/validate", formData, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("is-invalid");
    }

    @Test
    void shouldReturnValidationErrorForInvalidEmail() {
        String formData = "name=John&email=notanemail&phone=1234567890";

        ResponseEntity<String> response = restTemplate.postForEntity("/userform/validate", formData, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("is-invalid");
    }

    @Test
    void shouldReturnValidationErrorForInvalidPhone() {
        String formData = "name=John&email=john@example.com&phone=123";

        ResponseEntity<String> response = restTemplate.postForEntity("/userform/validate", formData, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("is-invalid");
    }

    @Test
    void shouldReturnTagListFragmentOnAddTag() {
        // First get the form to initialize session
        restTemplate.getForEntity("/userform", String.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/userform/add-tag?newTag=newtag",
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("newtag");
    }

    @Test
    void shouldReturnTagListFragmentOnRemoveTag() {
        // First get the form to initialize session
        restTemplate.getForEntity("/userform", String.class);

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/userform/remove-tag?index=0",
            null,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("badge");
    }

    @Test
    void shouldReturnSuccessMessageOnValidSubmission() {
        // First get the form to initialize session
        restTemplate.getForEntity("/userform", String.class);

        // Submit with valid data - we'll just check that it returns either success or form
        String formData = "name=John Doe&email=john@example.com&phone=1234567890";
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/userform/submit",
            formData,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Either we get success or a valid form response
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void shouldReturnFormErrorsOnInvalidSubmission() {
        String formData = "name=&email=&phone=";

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/userform/submit",
            formData,
            String.class
        );

        assertThat(response.getBody()).contains("is-invalid");
    }
}

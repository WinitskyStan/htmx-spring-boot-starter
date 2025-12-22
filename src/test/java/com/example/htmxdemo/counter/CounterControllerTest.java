package com.example.htmxdemo.counter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CounterControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CounterService counterService;

    @Test
    void shouldReturnCounterPageOnRootPath() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Counter Demo");
        assertThat(response.getBody()).contains("Increment");
    }

    @Test
    void shouldIncrementCounterOnPost() {
        int initialCount = counterService.getCount();

        ResponseEntity<String> response = restTemplate.postForEntity("/counter/increment", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(counterService.getCount()).isEqualTo(initialCount + 1);
        assertThat(response.getBody()).contains(String.valueOf(initialCount + 1));
    }

    @Test
    void shouldReturnPartialHTMLFragmentOnIncrement() {
        ResponseEntity<String> response = restTemplate.postForEntity("/counter/increment", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("id=\"count-display\"");
        assertThat(response.getBody()).contains("<div class=\"count\"");
        assertThat(response.getBody()).doesNotContain("<!DOCTYPE html>");
    }
}

# Project Guidelines

## Architecture

### Package-by-Feature
Organize code by feature, not by layer. Each feature is a self-contained package.

```
src/main/java/com/example/htmxdemo/
├── HtmxDemoApplication.java
└── counter/
    ├── CounterController.java
    ├── CounterService.java
    └── CounterTest.java (if needed)

src/main/resources/templates/
└── counter/
    └── counter.html
```

### HTMX Integration
Use Thymeleaf fragments for HTMX partial responses:

```html
<!-- Full page template -->
<div id="some-id" th:fragment="fragment-name">
    <div th:text="${data}">content</div>
</div>
```

```java
// Controller returns fragment for HTMX requests
@PostMapping("/endpoint")
public String update(Model model) {
    model.addAttribute("data", value);
    return "feature/template :: fragment-name";
}
```

### Template Naming
Name templates after the feature, not generic names:
- ✅ `counter/counter.html`
- ❌ `counter/index.html`

## Testing

Use Spring Boot integration tests with auto-injection:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FeatureControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FeatureService service;
}
```

## Git Conventions

### Commit Messages
- One line only
- No author attribution
- Start with action verb
- Be specific and concise

**Examples:**
```
Add initial Spring Boot HTMX counter demo with package-by-feature structure
Rename index.html to counter.html for better package-by-feature naming
Add integration tests for CounterController using Spring Boot test facilities
```

## Technology Stack

- Spring Boot 3.2.1
- Java 17
- Thymeleaf
- HTMX 1.9.10 (via CDN)
- Maven

## Running the Application

```bash
mvn spring-boot:run
```

Access at `http://localhost:8080`

## Running Tests

```bash
mvn test
```

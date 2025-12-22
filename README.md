# HTMX Spring Boot Starter

A minimal, well-structured starter project demonstrating how to build interactive web applications using **HTMX** with **Spring Boot 3.2.1** and **Thymeleaf**.

## Overview

This project showcases a simple yet powerful pattern: instead of building single-page applications (SPAs) with client-side frameworks, use HTMX to send HTTP requests and replace small portions of your HTML dynamically. The server renders only the fragments that changed, keeping your application lightweight and maintainable.

The demo implements an interactive counter that increments on button click without a full page reload.

## Tech Stack

- **Spring Boot** 3.2.1 — lightweight Java web framework
- **Java** 17 — modern language features
- **Thymeleaf** — server-side template engine with fragment support
- **HTMX** 1.9.10 — low JavaScript dependency for dynamic updates
- **Maven** — build and dependency management

## Project Structure

The project follows a **package-by-feature** architecture for clean, maintainable code organization:

```
src/main/java/com/example/htmxdemo/
├── HtmxDemoApplication.java      # Spring Boot entry point
└── counter/                       # Feature package
    ├── CounterController.java    # HTTP request handlers
    └── CounterService.java       # Business logic

src/main/resources/
├── application.properties        # Configuration
└── templates/counter/
    └── counter.html              # Thymeleaf template with HTMX

src/test/java/com/example/htmxdemo/counter/
└── CounterControllerTest.java   # Integration tests
```

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.6+

### Run the Application

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**

### Run Tests

```bash
mvn test
```

## How It Works

### The Counter Feature

1. **User Action**: User clicks the "Increment" button
2. **HTMX Request**: HTMX intercepts the click and sends a POST request to `/counter/increment`
3. **Server Processing**: Spring Boot controller increments the counter via the service
4. **Fragment Response**: Server responds with only the updated count HTML fragment
5. **DOM Update**: HTMX replaces the target element with the new HTML

### Key Components

**CounterController** (`counter/CounterController.java`)
- `GET /` — Returns the full counter page
- `POST /counter/increment` — Returns only the updated count fragment for HTMX

**CounterService** (`counter/CounterService.java`)
- Maintains counter state
- Provides `getCount()` and `increment()` methods

**counter.html** Template
- Full page with embedded styling
- HTMX library loaded from CDN
- Thymeleaf fragment `count-display` for the dynamic count value
- Button with HTMX attributes:
  ```html
  <button hx-post="/counter/increment"
          hx-target="#count-display"
          hx-swap="outerHTML">
    Increment
  </button>
  ```

## Architecture Principles

This project demonstrates key practices for building maintainable HTMX + Spring Boot applications:

### Package-by-Feature
Code is organized by feature (not by layer). Each feature is a self-contained package with its controller, service, and tests.

### HTMX Fragment Pattern
Controllers return Thymeleaf fragments for HTMX requests:
```java
@PostMapping("/counter/increment")
public String increment(Model model) {
    counterService.increment();
    model.addAttribute("count", counterService.getCount());
    return "counter/counter :: count-display";  // Fragment reference
}
```

### Server-Side Rendering
HTML is rendered on the server, reducing client-side complexity and improving SEO.

## Configuration

Application settings in `application.properties`:

- **Server Port**: 8080 (public)
- **Thymeleaf Caching**: Disabled for development
- **DevTools**: Enabled for automatic reload on code changes

## Next Steps

Use this starter as a foundation to:

- Add more features following the package-by-feature pattern
- Extend the counter with features like reset, decrement, or persistence
- Replace the simple in-memory counter with a database-backed service
- Add form validation and error handling
- Implement user authentication and authorization

## Resources

- [HTMX Documentation](https://htmx.org)
- [Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)
- [Thymeleaf Documentation](https://www.thymeleaf.org)
- [Project Guidelines](CLAUDE.md) — Development conventions and patterns

## License

MIT

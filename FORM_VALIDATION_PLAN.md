# Form Validation Feature - Implementation Plan

## Overview
Create a user form validation feature following package-by-feature architecture with Spring Boot, Thymeleaf, HTMX, and Base Styles CSS.

## Architecture

### Package Structure
```
src/main/java/com/example/htmxdemo/userform/
├── UserFormController.java
├── UserFormService.java
└── UserForm.java

src/test/java/com/example/htmxdemo/userform/
└── UserFormControllerTest.java

src/main/resources/templates/userform/
└── userform.html
```

## Component Details

### 1. Form Model (UserForm.java)

**Purpose:** Spring form model with validation annotations

**Fields:**
- `name` - String (user's name)
- `email` - String (user's email)
- `phone` - String (user's phone number)
- `tags` - List<String> (simple tag list, user can add/remove)

**Validation Annotations:**
```java
@NotBlank(message = "Name is required")
@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
private String name;

@NotBlank(message = "Email is required")
@Email(message = "Please enter a valid email address")
private String email;

@NotBlank(message = "Phone is required")
@Pattern(regexp = "^\\d{10}$", message = "Phone must be exactly 10 digits")
private String phone;

private List<String> tags = new ArrayList<>();
```

### 2. Service Layer (UserFormService.java)

**Responsibilities:**
- Manage session-based form data persistence
- Initialize form with default data on first load
- Handle tag list operations (add, remove)
- Provide business logic for form operations

**Key Methods:**
- `UserForm initializeForm()` - Create form with default data (includes 2 initial tags: "htmx-enthusiast" and "spring-boot-dev")
- `void addTag(UserForm form, String tag)` - Add tag to list
- `void removeTag(UserForm form, int index)` - Remove tag from list

### 3. Controller (UserFormController.java)

**Session Management:**
- Use `@SessionAttributes("userForm")` to persist form across requests

**Endpoints:**

| Method | Path | Purpose | Returns |
|--------|------|---------|---------|
| GET | `/userform` | Display initial form | Full page template |
| POST | `/userform/validate` | Validate form via HTMX | Form fragment with errors |
| POST | `/userform/add-tag` | Add tag to list | Tag list fragment |
| POST | `/userform/remove-tag/{index}` | Remove tag | Tag list fragment |
| POST | `/userform/submit` | Final submission | Success message fragment |

**Validation Handling:**
```java
@PostMapping("/userform/validate")
public String validateForm(@Valid @ModelAttribute("userForm") UserForm form,
                          BindingResult bindingResult,
                          Model model) {
    // BindingResult captures all validation errors
    return "userform/userform :: form-section";
}
```

### 4. Template (userform.html)

**Styling Framework:**
- **Base Styles** from Really Good Software (https://unpkg.com/@rgs/base-styles@latest/base.css)
- No build required, drop-in CSS
- Professional, minimal design for server-rendered apps

**CSS Structure:**
```html
<head>
    <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    <link rel="stylesheet" href="https://unpkg.com/@rgs/base-styles@latest/base.css">
    <style>
        /* Minimal custom CSS for validation errors */
        input.error, select.error, textarea.error {
            border-color: #dc3545;
            background-color: #fff5f5;
        }
        .error-message {
            color: #dc3545;
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
    </style>
</head>
```

**Form Field Pattern:**
```html
<label for="name">Name</label>
<input type="text"
       id="name"
       th:field="*{name}"
       th:classappend="${#fields.hasErrors('name')} ? 'error' : ''"
       aria-invalid="${#fields.hasErrors('name')}">
<small th:if="${#fields.hasErrors('name')}"
       th:errors="*{name}"
       class="error-message">
</small>
```

**Thymeleaf Fragments:**
- `form-section` - Main form area (for validation updates)
- `tag-list` - Tags display section (for add/remove updates)
- `success-message` - Success state after submission

**HTMX Integration:**
```html
<!-- Real-time validation on change -->
<form hx-post="/userform/validate"
      hx-trigger="change from:input delay:500ms"
      hx-target="#form-section"
      hx-swap="outerHTML">

<!-- Add tag button -->
<button hx-post="/userform/add-tag"
        hx-target="#tag-list"
        hx-swap="outerHTML">
    Add Tag
</button>

<!-- Remove tag button -->
<button hx-post="/userform/remove-tag/0"
        hx-target="#tag-list"
        hx-swap="outerHTML">
    Remove
</button>
```

**Tag List Display:**
- Simple list/table showing current tags
- Each tag has a remove button
- Add new tag input field with button
- Updates via HTMX without page reload

### 5. Validation Features

**Visual Feedback:**
- ✅ Input fields turn red when invalid (red border + light red background)
- ✅ Error messages display below each field
- ✅ Messages configured via annotation (centralized)
- ✅ Real-time validation via HTMX on blur/change
- ✅ Accessibility support (aria-invalid attribute)
- ✅ Modern, polished look with Base Styles

**Validation Flow:**
1. User types in field
2. After 500ms delay, HTMX posts to `/userform/validate`
3. Server validates with `@Valid` annotation
4. Returns form fragment with error styling
5. HTMX swaps in updated form with red fields and error messages

### 6. Session Management

**Strategy:**
- Store `UserForm` in session using `@SessionAttributes`
- Initialize with default data on GET `/userform` (includes 2 starter tags)
- Persist changes as user interacts (validate, add/remove tags)
- Clear session on successful submission

**Initial Data:**
- Name: empty
- Email: empty
- Phone: empty
- Tags: `["htmx-enthusiast", "spring-boot-dev"]` (pre-populated to demonstrate list functionality)

**Benefits:**
- Form data preserved across requests
- Tags list maintained during interaction
- No database needed for demo
- Users can see tag functionality immediately with examples

### 7. Testing (UserFormControllerTest.java)

**Location:** `src/test/java/com/example/htmxdemo/userform/UserFormControllerTest.java`

**Test Cases:**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserFormControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserFormService service;

    // Test form display with initial data
    // Test validation error responses
    // Test tag add operation
    // Test tag remove operation
    // Test successful submission
}
```

## Implementation Checklist

- [ ] Create UserForm.java with validation annotations
- [ ] Create UserFormService.java with tag management
- [ ] Create UserFormController.java with session and endpoints
- [ ] Create userform.html with Base Styles and HTMX
- [ ] Add validation styling (red borders, error messages)
- [ ] Implement tag list display and add/remove functionality
- [ ] Add success message on submission
- [ ] Write integration tests in src/test/java
- [ ] Test validation flow end-to-end
- [ ] Verify session persistence works correctly

## Key Design Decisions

1. **Base Styles CSS** - No build, professional styling for server-rendered apps
2. **Simple tags (List<String>)** - Avoid complexity of nested objects
3. **Session storage** - Simple persistence without database
4. **HTMX partial updates** - Modern UX without full page reloads
5. **Annotation-based validation** - Centralized validation logic
6. **Thymeleaf fragments** - Efficient partial rendering
7. **Package-by-feature** - All related code in one package
8. **Standard test location** - Tests in src/test/java mirroring main structure

## Expected Result

A professional-looking form validation demo with:
- Clean, modern UI (Base Styles)
- Real-time validation feedback
- Red borders and error messages on invalid fields
- Dynamic tag list management
- Session-based data persistence
- No page reloads (HTMX)
- Following project guidelines and conventions

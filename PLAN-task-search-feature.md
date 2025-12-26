# Plan: Task Search Feature with Detail View

## Overview
Add a dynamic search input filter feature that allows users to type in a search field and get real-time filtered results from the server. The feature will display a list of tasks with names and descriptions, filtering based on the task name. When a task is selected from the list, it will display the full task details.

## Structure (Package-by-Feature)

```
src/main/java/com/example/htmxdemo/
└── tasksearch/
    ├── TaskSearchController.java
    ├── TaskSearchService.java
    └── Task.java (record)

src/main/resources/
├── templates/tasksearch/
│   └── tasksearch.html
└── tasks.json  (hardcoded task data)
```

## Implementation Steps

### 1. Create Task Model
**File**: `src/main/java/com/example/htmxdemo/tasksearch/Task.java`
- Simple Java record with `id`, `name`, and `description` fields
- `id` needed for selecting individual tasks
- Immutable data structure for representing a task

### 2. Create Sample Task Data
**File**: `src/main/resources/tasks.json`
- JSON array with 20-30 sample tasks
- Each task has `id`, `name`, and `description` properties
- Loaded at application startup via Jackson

### 3. Create TaskSearchService
**File**: `src/main/java/com/example/htmxdemo/tasksearch/TaskSearchService.java`
- Annotated with `@Service`
- Load tasks from JSON file on startup using `@PostConstruct`
- Method: `List<Task> getAllTasks()` - returns all tasks
- Method: `List<Task> search(String query)` - filters tasks where name contains query (case-insensitive)
- Method: `Optional<Task> getTaskById(Long id)` - retrieves specific task for detail view
- Empty/null query returns all tasks

### 4. Create TaskSearchController
**File**: `src/main/java/com/example/htmxdemo/tasksearch/TaskSearchController.java`
- Annotated with `@Controller`
- Inject `TaskSearchService`
- `GET /tasksearch` - returns full page template with all tasks initially displayed, empty detail section
- `POST /tasksearch/search` - accepts `query` parameter, returns `task-list` fragment with filtered results
- `GET /tasksearch/{id}` - accepts task ID, returns `task-detail` fragment with full task information

### 5. Create Template with HTMX Integration
**File**: `src/main/resources/templates/tasksearch/tasksearch.html`

**Page Layout**:
- Two-column layout (or top/bottom on mobile)
- Left/top: Search input and task list
- Right/bottom: Task detail section

**Components**:

**Search Input**:
- HTMX attributes:
  - `hx-post="/tasksearch/search"` - endpoint for search
  - `hx-trigger="input changed delay:300ms"` - debounce typing (300ms delay)
  - `hx-target="#task-list"` - target the task list div
  - `hx-swap="outerHTML"` - replace entire task list

**Task List Container**:
- `id="task-list"` - target for HTMX swaps
- `th:fragment="task-list"` - Thymeleaf fragment for partial responses
- Each task is clickable/tappable
- Task items have HTMX attributes:
  - `hx-get="/tasksearch/{id}"` - load task detail
  - `hx-target="#task-detail"` - target detail section
  - `hx-swap="innerHTML"` - replace detail content
- Shows task name only in list (description in detail view)
- Empty state message when no results found

**Task Detail Section**:
- `id="task-detail"` - target for HTMX swaps
- `th:fragment="task-detail"` - Thymeleaf fragment for partial responses
- Initially shows "Select a task to view details" message
- When populated, shows:
  - Task name (heading)
  - Task description (full text)
  - Any additional task metadata

### 6. Styling
- Use existing Bootstrap 5.3.3 for styling
- Two-column grid layout using Bootstrap grid system
- Task list items styled as clickable list-group items
- Highlight selected task in list
- Task detail section with card styling
- Search input with proper form control styling
- Responsive design that stacks on mobile

## HTMX Pattern Example

```html
<!-- Search Input -->
<input type="text"
       name="query"
       class="form-control"
       placeholder="Search tasks..."
       hx-post="/tasksearch/search"
       hx-trigger="input changed delay:300ms"
       hx-target="#task-list"
       hx-swap="outerHTML">

<!-- Task List -->
<div id="task-list" th:fragment="task-list">
    <div class="list-group">
        <a th:each="task : ${tasks}"
           th:href="@{/tasksearch/{id}(id=${task.id})}"
           class="list-group-item list-group-item-action"
           hx-get="/tasksearch/{id}"
           th:hx-get="@{/tasksearch/{id}(id=${task.id})}"
           hx-target="#task-detail"
           hx-swap="innerHTML">
            <span th:text="${task.name}">Task Name</span>
        </a>
    </div>
    <div th:if="${#lists.isEmpty(tasks)}" class="alert alert-info mt-3">
        No tasks found matching your search.
    </div>
</div>

<!-- Task Detail Section -->
<div id="task-detail">
    <div th:fragment="task-detail" th:if="${task != null}">
        <div class="card">
            <div class="card-body">
                <h3 class="card-title" th:text="${task.name}">Task Name</h3>
                <p class="card-text" th:text="${task.description}">Description</p>
            </div>
        </div>
    </div>
    <div th:if="${task == null}" class="text-muted">
        Select a task to view details
    </div>
</div>
```

## Controller Return Pattern

```java
// Full page
@GetMapping("/tasksearch")
public String index(Model model) {
    model.addAttribute("tasks", service.getAllTasks());
    return "tasksearch/tasksearch";
}

// Fragment for search
@PostMapping("/tasksearch/search")
public String search(@RequestParam(defaultValue = "") String query, Model model) {
    model.addAttribute("tasks", service.search(query));
    return "tasksearch/tasksearch :: task-list";
}

// Fragment for task detail
@GetMapping("/tasksearch/{id}")
public String taskDetail(@PathVariable Long id, Model model) {
    service.getTaskById(id).ifPresent(task -> model.addAttribute("task", task));
    return "tasksearch/tasksearch :: task-detail";
}
```

## Key Technical Decisions

1. **JSON file for data** - Fastest implementation without database setup
2. **300ms debounce** - Reduces server requests while typing
3. **Case-insensitive search** - Better user experience
4. **Search on task name only** - Simpler implementation (can extend to description later)
5. **Bootstrap list-group** - Clean, clickable task list
6. **Two separate HTMX targets** - Independent updates for list and detail
7. **Return all tasks on empty query** - Better UX than showing nothing
8. **Task ID in URL** - RESTful pattern, allows direct linking

## Testing Strategy

Create integration test following existing patterns:
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- Test GET `/tasksearch` returns full page with all tasks
- Test POST `/tasksearch/search` with query returns filtered fragment
- Test POST `/tasksearch/search` with empty query returns all tasks
- Test GET `/tasksearch/{id}` returns task detail fragment
- Test GET `/tasksearch/{invalid-id}` handles missing task gracefully
- Verify fragments contain correct `id` attributes
- Verify no full page markup in fragment responses

## Consistency with Existing Application

- Follows package-by-feature architecture (counter, userform, tasksearch)
- Uses same HTMX + Thymeleaf fragment pattern
- Controller/Service separation maintained
- Bootstrap styling matches existing features
- Integration tests follow same structure
- Template naming: `tasksearch/tasksearch.html`
- Multiple fragments in single template (like userform feature)

# Bootstrap 5 Migration Plan

## Overview
Migrate the userform from Base Styles + custom CSS to Bootstrap 5 (CSS only, no JavaScript).

## Why Bootstrap 5 Without JavaScript?
- **CSS-only features work perfectly with HTMX**: forms, validation, spinners, buttons, grid
- **No conflicts**: HTMX handles all interactivity, Bootstrap provides styling
- **Battle-tested**: Widely used, well-documented, accessible
- **Less custom CSS**: Leverage Bootstrap's utility classes and components

## What Bootstrap 5 Provides (CSS Only)

### Forms
- `.form-label` - styled labels
- `.form-control` - input styling with focus states
- `.is-invalid` - red border and background for errors
- `.invalid-feedback` - error message styling

### Validation
- `.was-validated` - class to trigger validation styling on form
- `.is-invalid` / `.is-valid` - per-field validation states
- Built-in error message styling

### Spinners
- `.spinner-border` - spinning circle (default)
- `.spinner-border-sm` - small spinner for buttons
- `.spinner-grow` - pulsing circle
- Works with HTMX `htmx-indicator` class

### Buttons
- `.btn .btn-primary` - styled buttons
- Spinners go directly inside buttons with proper sizing

### Layout
- Container and grid system
- Spacing utilities (mt-3, mb-2, gap-2, etc.)
- Card components for containers

## Migration Steps

### 1. Replace CSS Link
**Current:**
```html
<link rel="stylesheet" href="https://unpkg.com/@rgs/base-styles@latest/base.css">
```

**New:**
```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
```

### 2. Update Form Structure

**Current pattern:**
```html
<div>
    <label for="name">Name *</label>
    <input type="text" id="name" th:field="*{name}"
           th:classappend="${#fields.hasErrors('name')} ? 'error' : ''">
    <small th:if="${#fields.hasErrors('name')}"
           th:errors="*{name}" class="error-message"></small>
</div>
```

**Bootstrap pattern:**
```html
<div class="mb-3">
    <label for="name" class="form-label">Name *</label>
    <input type="text" id="name" th:field="*{name}"
           class="form-control"
           th:classappend="${#fields.hasErrors('name')} ? 'is-invalid' : ''">
    <div th:if="${#fields.hasErrors('name')}"
         th:errors="*{name}" class="invalid-feedback"></div>
</div>
```

### 3. Update Button with Spinner (Bootstrap Pattern)

**Current:**
```html
<button type="button" style="display: flex; align-items: center; justify-content: center;">
    <span class="spinner-inline htmx-indicator"></span>
    Submit Form
</button>
```

**Bootstrap with HTMX:**
```html
<button type="button"
        class="btn btn-primary"
        hx-post="/userform/submit"
        hx-target="#form-section"
        hx-swap="outerHTML"
        hx-indicator=".submit-spinner">
    <span class="spinner-border spinner-border-sm me-2 htmx-indicator submit-spinner" aria-hidden="true"></span>
    <span>Submit Form</span>
</button>
```

**Key points:**
- Bootstrap spinner classes: `spinner-border spinner-border-sm`
- Spacing utility: `me-2` (margin-end 2) for gap between spinner and text
- HTMX integration: Add `htmx-indicator` class AND a unique class (`.submit-spinner`)
- Use `hx-indicator=".submit-spinner"` to target it
- Add `aria-hidden="true"` for accessibility
- Wrap text in `<span>` for proper structure

### 4. Update Container

**Current:**
```html
<div class="form-container">
    <!-- content -->
</div>
```

**Bootstrap:**
```html
<div class="container mt-5">
    <div class="card shadow">
        <div class="card-body p-4">
            <!-- content -->
        </div>
    </div>
</div>
```

### 5. Update Body Styling

**Keep minimal custom CSS:**
```css
body {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
}
```

**Remove all other custom CSS** - Bootstrap handles:
- Form inputs
- Buttons
- Error messages
- Spinners
- Layout/spacing
- Typography

### 6. Tag Component

**Current custom styling:**
```html
<div class="tag">
    <span>htmx-enthusiast</span>
    <button>×</button>
</div>
```

**Bootstrap badges with flex:**
```html
<span class="badge bg-primary d-inline-flex align-items-center gap-2 fs-6 py-2 px-3">
    htmx-enthusiast
    <button type="button"
            class="btn-close btn-close-white"
            style="font-size: 0.5rem;"
            hx-post="/userform/remove-tag"
            hx-vals='{"index": [[${iter.index}]]}'
            hx-target="#tag-list"
            hx-swap="outerHTML"
            aria-label="Remove tag"></button>
</span>
```

Bootstrap provides:
- `.badge` for tag styling
- `.bg-primary` for color
- `.btn-close` for close button
- `.btn-close-white` for white X on colored background
- `.d-inline-flex .align-items-center .gap-2` for layout
- `.fs-6 .py-2 .px-3` for sizing

### 7. Success Message

**Current:**
```html
<div class="success-box">
    <h2>Success!</h2>
    <a href="/userform">Submit Another</a>
</div>
```

**Bootstrap:**
```html
<div class="alert alert-success shadow-sm" role="alert">
    <h4 class="alert-heading">Success!</h4>
    <p>Your form has been submitted.</p>
    <hr>
    <a href="/userform" class="btn btn-success">Submit Another</a>
</div>
```

## Custom CSS After Migration

Reduction: **~90% less custom CSS** (from ~200 lines to ~10 lines)

Remaining custom CSS:
```css
/* Body background gradient */
body {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
}

/* Optional: Customize Bootstrap variables if needed */
```

## Benefits of Migration

1. **Massive CSS reduction** - From 200+ lines to ~10 lines
2. **Better accessibility** - Bootstrap has ARIA built-in
3. **Responsive by default** - Mobile-friendly without custom media queries
4. **Consistent design** - Professional look without design work
5. **Utility classes** - Fast styling with `mt-3`, `px-4`, `d-flex`, etc.
6. **Well-documented** - Extensive examples and patterns
7. **Team familiarity** - Most developers know Bootstrap

## Implementation Checklist

- [ ] Replace Base Styles CDN link with Bootstrap 5 CDN
- [ ] Update `<body>` and form container to use Bootstrap card
- [ ] Update all form fields to use Bootstrap form classes
- [ ] Update validation styling (`.is-invalid`, `.invalid-feedback`)
- [ ] Replace custom spinner with Bootstrap spinner in button
- [ ] Update submit button with Bootstrap button classes
- [ ] Update tag component with Bootstrap badges
- [ ] Update success message with Bootstrap alert
- [ ] Remove all obsolete custom CSS (~90% of current CSS)
- [ ] Keep minimal background gradient CSS
- [ ] Test validation styling
- [ ] Test spinner animation with HTMX
- [ ] Test responsive layout on mobile
- [ ] Update documentation

## Expected Result

A cleaner, more maintainable form with:
- Professional Bootstrap styling
- Minimal custom CSS (~10 lines vs ~200 lines)
- Better accessibility
- Responsive design
- Same HTMX functionality
- Proper Bootstrap spinner inside button with HTMX integration

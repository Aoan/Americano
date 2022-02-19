# Americano
In addition to Espresso, Americano comes with a few extra handy classes to help with your Android tests.

## WaitActions
Avoid `Thread.sleep`; Use `WaitActions.waitUntil` function to pause and wait for your delayed views:
```
// Wait for "Login" button to enable
onView(withText("Login")).perform(waitUntil(isEnabled()), click())
// Wait for "Hello" text to appear
onView(isRoot()).perform(waitUntil(hasDescendant(withText("Hello"))))
// Wait for snackbar to appear
onView(isRoot()).perform(waitUntil(hasDescendant(isAssignableFrom(Snackbar.SnackbarLayout::class.java))))
```
Or wait for views until dismiss:
```
// Wait for progress dialog until dismiss
onView(isAssignableFrom(ProgressBar::class.java)).perform(waitUntilDismiss())
```
But if you must, then use `WaitActions.waitFor` function:
```
// Idle for 1 second
onView(isRoot()).perform(waitFor(1000))
```

## RecyclerViewActions
Use `RecyclerViewActions.actionOnItemView` function to perform action on specific view in a specific row:
```
// Click "Delete" on the row item with text "Unwanted"
onView(isAssignableFrom(RecyclerView::class.java)).perform(actionOnItem<RecyclerView.ViewHolder>(
    hasDescendant(withText("Unwanted")), actionOnItemView(withText("Delete"), click())))
```
## RecyclerViewMatchers
Use `RecyclerViewMatchers.hasItemCount` to check your adapter size:
```
// Check for adapter item count
onView(isAssignableFrom(RecyclerView::class.java)).check(matches(hasItemCount(100)))
// Check if adapter is empty
onView(isAssignableFrom(RecyclerView::class.java)).check(matches(hasItemCount(greaterThan(0))))
```
Or `RecyclerViewMatchers.hasItem` to check if an item exists in your adapter without scrolling:
```
// Check if adapter contains view with text "Americano"
onView(isAssignableFrom(RecyclerView::class.java)).check(matches(hasItem(withText("Americano"))))
```

## ViewActions
Use enhanced `ViewActions.scrollTo` for scrolling:
```
// Support NestedScrollView scrolling
onView(isAssignableFrom(NestedScrollView::class.java)).perform(scrollTo())
```
Try other convenient functions to enhance your android tests:
```
// Rotate your device or emulators at ease
onView(isRoot()).perform(rotateScreenOrientation())
// Request view focus
onView(withText("Username")).perform(requestFocus())
```
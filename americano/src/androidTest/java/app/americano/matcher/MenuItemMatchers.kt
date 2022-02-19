package app.americano.matcher

import android.view.MenuItem
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.equalTo
import org.hamcrest.TypeSafeMatcher

object MenuItemMatchers {

    fun withMenuItem(title: String) = withMenuItem(equalTo(title))

    private fun withMenuItem(title: Matcher<String>): Matcher<MenuItem> {

        return object : TypeSafeMatcher<MenuItem>(MenuItem::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("with menu item: $title")
            }

            override fun matchesSafely(item: MenuItem) = title.matches(item.title)
        }
    }
}
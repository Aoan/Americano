package app.americano.matcher

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Matchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.equalTo

object RecyclerViewMatchers {

    fun hasItem(matcher: Matcher<View>): Matcher<View> {

        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item: ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val adapter = view.adapter ?: return false
                for (position in 0 until adapter.itemCount) {
                    val type = adapter.getItemViewType(position)
                    val holder = adapter.createViewHolder(view, type)
                    adapter.onBindViewHolder(holder, position)
                    if (matcher.matches(holder.itemView)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    fun hasItemAtPosition(position: Int, matcher: Matcher<View>): Matcher<View> {

        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item: ")
                matcher.describeTo(description)
                description.appendText(" at position: $position")
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val adapter = view.adapter ?: return false
                val type = adapter.getItemViewType(position)
                val holder = adapter.createViewHolder(view, type)
                adapter.onBindViewHolder(holder, position)
                return matcher.matches(holder.itemView)
            }
        }
    }

    fun hasItemCount(count: Int) = hasItemCount(equalTo(count))

    fun hasItemCount(count: Matcher<Int>): Matcher<View> {

        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("has item count: ")
                count.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                return count.matches(view.adapter?.itemCount)
            }
        }
    }
}
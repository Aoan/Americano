package app.americano.action

import android.os.SystemClock
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.util.HumanReadables
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import app.americano.action.RecyclerViewActions.actionOnItemView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import org.hamcrest.SelfDescribing
import org.hamcrest.StringDescription

object RecyclerViewMotionActions {

    fun drag(from: Target, to: Target): ViewAction {

        return object : ViewAction {

            override fun getConstraints() = isAssignableFrom(RecyclerView::class.java)

            override fun getDescription(): String {
                return StringDescription().apply {
                    appendText("drag from: ")
                    from.describeTo(this)
                    appendText(" to: ")
                    to.describeTo(this)
                }.toString()
            }

            override fun perform(uiController: UiController, view: View) {
                view as RecyclerView

                val startCoordinates = from.findCoordinates(uiController, view)

                val precision = Press.FINGER.describePrecision()
                val downEvent = LONG_PRESS.perform(uiController, startCoordinates, precision)

                val endCoordinates = to.findCoordinates(uiController, view)

                val steps = interpolate(startCoordinates, endCoordinates)
                val interval = DRAG_DURATION / steps.size

                try {
                    steps.forEachIndexed { index, step ->
                        if (!MotionEvents.sendMovement(uiController, downEvent, step)) {
                            MotionEvents.sendCancel(uiController, downEvent)
                            fail(RuntimeException("Injection of move event as part of the drag failed"), view)
                        }

                        val expectedStepTime = downEvent.downTime + interval * index
                        val timeUntilExpectedStepTime = expectedStepTime - SystemClock.uptimeMillis()

                        if (timeUntilExpectedStepTime > 0) {
                            uiController.loopMainThreadForAtLeast(timeUntilExpectedStepTime)
                        }
                    }

                    uiController.loopMainThreadForAtLeast(PRESS_RELEASE_DELAY)
                    if (!MotionEvents.sendUp(uiController, downEvent, endCoordinates)) {
                        MotionEvents.sendCancel(uiController, downEvent)
                        fail(RuntimeException("Injection of up event as part of the drag failed"), view)
                    }
                } finally {
                    downEvent.recycle()
                }
            }
        }
    }

    private fun interpolate(start: FloatArray, end: FloatArray): Array<FloatArray> {
        return Array(DRAG_STEP_COUNT) { FloatArray(2) }.apply {
            forEachIndexed { index, _ ->
                this[index][0] = start[0] + (end[0] - start[0]) * index / (DRAG_STEP_COUNT - 1f)
                this[index][1] = start[1] + (end[1] - start[1]) * index / (DRAG_STEP_COUNT - 1f)
            }
        }
    }

    private fun ViewAction.fail(cause: Throwable, view: View): Nothing {
        throw PerformException.Builder()
            .withActionDescription(description)
            .withViewDescription(HumanReadables.describe(view))
            .withCause(cause)
            .build()
    }

    private const val DRAG_STEP_COUNT = 15
    private const val DRAG_DURATION = 1500
    private const val PRESS_RELEASE_DELAY = 300L

    private val LONG_PRESS = object : DownMotionPerformer {
        override fun perform(uiController: UiController, coordinates: FloatArray, precision: FloatArray): MotionEvent {
            val event = MotionEvents.sendDown(uiController, coordinates, precision).down
            uiController.loopMainThreadForAtLeast((ViewConfiguration.getLongPressTimeout() * 1.5f).toLong())
            return event
        }
    }

    private interface DownMotionPerformer {
        fun perform(uiController: UiController, coordinates: FloatArray, precision: FloatArray): MotionEvent
    }

    private class CalculateCoordinatesAction(
        private val provider: CoordinatesProvider,
        private val coordinates: FloatArray,
    ) : ViewAction {

        override fun getDescription() = "calculate coordinates"

        override fun getConstraints(): Matcher<View> = any(View::class.java)

        override fun perform(uiController: UiController, view: View) {
            provider.calculateCoordinates(view).forEachIndexed { index, value -> coordinates[index] = value }
        }
    }

    class Target(
        private val item: Matcher<View>,
        private val target: Matcher<View> = item,
        private val proximity: CoordinatesProvider = GeneralLocation.CENTER,
    ) : SelfDescribing {

        override fun describeTo(description: Description) {
            description.appendText("item with: ")
            item.describeTo(description)
            description.appendText(" on target: ")
            target.describeTo(description)
        }

        fun findCoordinates(uiController: UiController, view: RecyclerView): FloatArray {
            val coordinates = FloatArray(2)
            val itemAction = actionOnItemView(target, CalculateCoordinatesAction(proximity, coordinates))
            actionOnItem<RecyclerView.ViewHolder>(item, itemAction).perform(uiController, view)
            return coordinates
        }
    }
}
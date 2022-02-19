package app.americano.rule

import android.content.Intent
import android.content.Intent.ACTION_CLOSE_SYSTEM_DIALOGS
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description

object SystemBehaviorRules {

    fun closeSystemDialogs(): TestWatcher {

        return object : TestWatcher() {

            override fun starting(description: Description) {
                InstrumentationRegistry.getInstrumentation().context.sendBroadcast(Intent(ACTION_CLOSE_SYSTEM_DIALOGS))
            }
        }
    }
}
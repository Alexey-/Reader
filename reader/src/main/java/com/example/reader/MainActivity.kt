package com.example.reader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.base.ui.base.RoutingActivity
import com.example.base.utils.Path
import com.example.reader.ui.article.ArticlesListFragment
import dagger.android.AndroidInjection

class MainActivity : RoutingActivity() {

    companion object {

        const val INTENT_PARAM_INITIAL_PATH = "APP_PATH"

        @JvmOverloads
        @JvmStatic
        fun getIntent(context: Context, appPath: Path = Path.main): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(INTENT_PARAM_INITIAL_PATH, appPath.toString())
            return intent
        }

    }

    override val mainContainerId: Int = R.id.main_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (isLaunchedFromRecents) {
                openPath(Path.main)
            } else {
                openPath(detectPath(intent) ?: Path.main)
            }
        }
    }

    private fun detectPath(intent: Intent?): Path? {
        if (intent == null) return null

        val action = intent.action
        val data = intent.data
        if (Intent.ACTION_VIEW == action && data != null) {
            val query = if (data.query.isNullOrBlank()) {
                ""
            } else {
                "?${data.query}"
            }
            return Path(data.path!! + query)
        }

        val initialPathString = intent.extras?.getString(INTENT_PARAM_INITIAL_PATH)
        if (!initialPathString.isNullOrBlank()) {
            return Path(initialPathString)
        }

        return null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val newPath = detectPath(intent)
        if (newPath != null) {
            openPath(newPath)
            this.intent = getIntent(this)
        }
    }

    private val isLaunchedFromRecents: Boolean
        get() = (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY

    fun openPath(path: Path, animate: Boolean = false) {
        pushFragment(ArticlesListFragment.newInstance())
    }

}

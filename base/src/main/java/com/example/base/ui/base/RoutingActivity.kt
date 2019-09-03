package com.example.base.ui.base

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.example.base.R

abstract class RoutingActivity : AppCompatActivity() {

    @get:IdRes
    protected abstract val mainContainerId: Int

    fun replaceFragmentStack(newRoot: BaseFragment, animate: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        if (animate) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            transaction.setCustomAnimations(0, 0)
        }
        transaction.replace(mainContainerId, newRoot)
        transaction.commit()
    }

    fun pushFragment(fragment: BaseFragment, animate: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        if (animate) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
        } else {
            transaction.setCustomAnimations(0, 0)
        }
        val currentFragment = supportFragmentManager.findFragmentById(mainContainerId)
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        transaction.add(mainContainerId, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun popFragment() {
        supportFragmentManager.popBackStackImmediate()
    }

    fun popFragmentsToRoot() {
        val fragmentsToPop = supportFragmentManager.backStackEntryCount
        for (i in 0 until fragmentsToPop) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun sendBackSignalToFragment(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(mainContainerId)
        if (fragment != null && fragment is BaseFragment) {
            if (fragment.onBackPressed()) {
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (sendBackSignalToFragment()) {
            return
        }
        super.onBackPressed()
    }

}

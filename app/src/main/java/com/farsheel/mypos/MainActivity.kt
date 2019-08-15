package com.farsheel.mypos

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.facebook.stetho.Stetho
import com.farsheel.mypos.data.local.AppDatabase
import com.farsheel.mypos.data.local.PreferenceManager
import com.farsheel.mypos.util.AppEvent
import com.farsheel.mypos.util.Util
import com.farsheel.mypos.util.appEventFlowable
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(application)
        setupNavigation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        compositeDisposable.add(createAppEventsSubscription())
    }


    private fun setupNavigation() {

        setSupportActionBar(toolbar)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        val topLevelDestinations = setOf(R.id.homeFragment, R.id.splashFragment, R.id.loginFragment)
        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setDrawerLayout(drawer_layout)
            .build()

        navigationView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Util.hideKeyboard(this)

            if (destination.id == R.id.splashFragment || destination.id == R.id.loginFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
            if (destination.id == R.id.homeFragment) {
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            if (drawer_layout.isDrawerOpen(GravityCompat.START))
                drawer_layout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        compositeDisposable = CompositeDisposable()
    }


    private fun createAppEventsSubscription(): Disposable =
        appEventFlowable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                when (it) {
                    AppEvent.TokenExpired -> {
                        showLogout()
                    }
                }
            }
            .subscribe()


    private fun showLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.your_session_is_expired))
        builder.setPositiveButton(getString(R.string.login)) { dialog, _ ->
            dialog.dismiss()
            PreferenceManager.clear(this)
            compositeDisposable.add(Completable.fromAction {
                AppDatabase.invoke(applicationContext).clearAndResetAllTables()
            }.subscribeOn(Schedulers.io()).subscribe())
            navController.popBackStack(R.id.splashFragment, false)
            navController.navigate(R.id.splashFragment)
        }
        builder.setCancelable(false)
        builder.show()
    }
}

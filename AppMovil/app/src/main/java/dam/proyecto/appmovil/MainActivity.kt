package dam.proyecto.appmovil

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupViews()

        setupNavigation()

        handleWindowInsets()
    }

    private fun setupViews() {
        drawerLayout = findViewById(R.id.navigation_drawer)
        val navView = findViewById<NavigationView>(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.appBarLayout)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.profileFragment,
                R.id.productosFragment,
                R.id.pedidosFragment
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.navigation_view).setupWithNavController(navController)

        val destinationListener = NavController.OnDestinationChangedListener { _, destination, _ ->
            handleDestinationChange(destination.id)
        }

        navController.addOnDestinationChangedListener(destinationListener)

        findViewById<NavigationView>(R.id.navigation_view).setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cerrarSesion -> {
                    cerrarSesion()
                    true
                }
                else -> {
                    navController.removeOnDestinationChangedListener(destinationListener)
                    val handled = androidx.navigation.ui.NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) drawerLayout.closeDrawers()
                    navController.addOnDestinationChangedListener(destinationListener)
                    handled
                }
            }
        }
    }

    private fun handleDestinationChange(destinationId: Int) {
        val isAuthScreen = destinationId == R.id.loginFragment || destinationId == R.id.registerFragment

        appBarLayout.animate()
            .alpha(if (isAuthScreen) 0f else 1f)
            .setDuration(200)
            .withEndAction {
                appBarLayout.visibility = if (isAuthScreen) View.GONE else View.VISIBLE
            }

        if (isAuthScreen) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
            setupActionBarWithNavController(navController, appBarConfiguration) // Reconfigurar
        }

        drawerLayout.setDrawerLockMode(
            if (isAuthScreen) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            else DrawerLayout.LOCK_MODE_UNLOCKED
        )

        if (isAuthScreen && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
    }

    private fun handleWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, 0, 0, 0)
            insets
        }
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this).apply {
            setTitle("Cerrar sesión")
            setMessage("¿Estás seguro de que quieres cerrar sesión?")
            setPositiveButton("Sí") { _, _ ->
                val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            setNegativeButton("No", null)
            create().show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
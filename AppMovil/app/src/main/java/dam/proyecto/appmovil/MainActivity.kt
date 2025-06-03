package dam.proyecto.appmovil

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
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
        enableEdgeToEdge() // Añadir esto para mejor manejo de bordes
        setContentView(R.layout.activity_main)

        // Configuración inicial
        setupViews()
        setupNavigation()


    }

    private fun setupViews() {
        drawerLayout = findViewById(R.id.navigation_drawer)
        toolbar = findViewById(R.id.toolbar)
        appBarLayout = findViewById(R.id.appBarLayout)

        // Configuración crítica del Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.profileFragment, R.id.productosFragment, R.id.pedidosFragment),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = findViewById<NavigationView>(R.id.navigation_view)
        navView.setupWithNavController(navController)

        // Mantenemos tu listener original para cerrar sesión
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.cerrarSesion -> {
                    cerrarSesion()
                    true
                }
                else -> {
                    val handled = androidx.navigation.ui.NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) drawerLayout.closeDrawers()
                    handled
                }
            }
        }

        // Listener mejorado para el Toolbar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleToolbarVisibility(destination.id)
        }
    }

    private fun handleToolbarVisibility(destinationId: Int) {
        when (destinationId) {
            R.id.loginFragment, R.id.registerFragment -> {
                // Pantallas sin Toolbar
                supportActionBar?.hide()
                appBarLayout.visibility = View.GONE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            else -> {
                // Pantallas con Toolbar
                supportActionBar?.show()
                appBarLayout.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                // Configuración adicional para el Toolbar
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
            }
        }
    }




    private fun cerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }




    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



}
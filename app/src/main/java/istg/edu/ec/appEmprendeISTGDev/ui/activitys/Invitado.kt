package istg.edu.ec.appEmprendeISTGDev.ui.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
// 1. AGREGA ESTE IMPORT
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.utils.setStatusBarColor
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Invitado : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. AÑADE ESTA LÍNEA (en Kotlin)
        // Esto deshabilita el "edge-to-edge"
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_invitado)

        // 3. ¡OJO AQUÍ! (ver nota abajo)
        setStatusBarColor(R.color.azul_navbar, lightIcons = false)

        val toolbar: Toolbar = findViewById(R.id.toolbarInvity)
        setSupportActionBar(toolbar)

        // --- AÑADE ESTE CÓDIGO PARA EL MARGEN/PADDING ---
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Aplica el padding solo en la parte de arriba
            view.setPadding(view.paddingLeft, systemBars.top, view.paddingRight, view.paddingBottom)
            insets // Devuelve los insets
        }

        // Ahora puedes personalizar la Toolbar
        supportActionBar?.apply {
            title = "Explorar como invitado"
            setDisplayHomeAsUpEnabled(true)
            // setHomeAsUpIndicator(R.drawable.ic_back) // Si tienes un icono personalizado
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val invite = intent.getStringExtra("evento")
        val miBundle = Bundle()
        miBundle.putString("miParametro", invite)
        navegarARevisarPublicaciones(miBundle)
    }

    fun navegarARevisarPublicaciones(bundle: Bundle) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.filtroBusquedaFragment, bundle)
    }

    // 🔙 Manejar clic en el botón de regreso de la Toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

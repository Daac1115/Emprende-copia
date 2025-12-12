package istg.edu.ec.appEmprendeISTGDev

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import istg.edu.ec.appEmprendeISTGDev.data.model.PerfilModel
import istg.edu.ec.appEmprendeISTGDev.data.model.UserModel
import istg.edu.ec.appEmprendeISTGDev.databinding.ActivityInicioBinding
import istg.edu.ec.appEmprendeISTGDev.utils.DeepLinkManager.handleIncomingIntent
import istg.edu.ec.appEmprendeISTGDev.utils.setStatusBarColor
import istg.edu.ec.appEmprendeISTGDev.viewModel.PerfilsViewModel
import istg.edu.ec.appEmprendeISTGDev.viewModel.PermisosViewModel
import istg.edu.ec.appEmprendeISTGDev.viewModel.UserViewModel

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var userViewModel: UserViewModel
    private lateinit var permisosViewModel: PermisosViewModel
    private lateinit var perfilViewModel: PerfilsViewModel

    private lateinit var navController: NavController
    private lateinit var mAppBarConfiguration: AppBarConfiguration

    private var appUpdateManager: AppUpdateManager? = null

    // ✔ CORRECCIÓN: firma correcta en Kotlin (solo 1 parámetro)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("InicioActivity", if (isGranted)
            "Permiso de notificaciones concedido"
        else
            "Permiso de notificaciones denegado"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        this.setStatusBarColor(R.color.azul_navbar, false)

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkForAppUpdate()
        askNotificationPermission()

        sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        permisosViewModel = ViewModelProvider(this)[PermisosViewModel::class.java]
        perfilViewModel = ViewModelProvider(this)[PerfilsViewModel::class.java]

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setSupportActionBar(binding.appBarInicio.toolbar)

        // ✔ Obtener NavController correctamente en Kotlin
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_inicio) as NavHostFragment
        navController = navHostFragment.navController

        mAppBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.perfilFragment,
                R.id.nav_home,
                R.id.misNegociosFragment,
                R.id.filtroBusquedaFragment,
                R.id.opcionesAdministradorFragment
            ),
            binding.drawerLayout
        )

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration)

        // ✔ Firma correcta — ahora sí override válido
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                navigationViewChecked()
            }
        })

        setupGoogleSignIn()
        setupUserData(currentUser)
        setupNavigationView()

        // Deep link
        binding.root.post {
            handleIncomingIntent(
                this,
                intent,
                R.id.nav_host_fragment_content_inicio,
                R.id.revisarPublicacionesFragment
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        binding.root.post {
            handleIncomingIntent(
                this,
                getIntent(),
                R.id.nav_host_fragment_content_inicio,
                R.id.revisarPublicacionesFragment
            )
        }
    }

    private fun navigationViewChecked() {
        val dest = navController.currentDestination?.id ?: R.id.nav_home
        binding.navView.setCheckedItem(dest)
    }

    // ================= GOOGLE SIGN IN =================

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        progressDialog = ProgressDialog(this).apply {
            setMessage("Cerrando sesión...")
            setCancelable(false)
        }
    }

    // ================= USUARIO FIRESTORE =================

    private fun setupUserData(user: FirebaseUser) {
        val uid = user.uid
        sharedPreferences.edit().putString("uid", uid).apply()

        permisosViewModel.checkAdminStatus(uid)
        permisosViewModel.esAdmin.observe(this) { esAdmin ->
            binding.navView.menu.findItem(R.id.opcionesAdministradorFragment)
                .isVisible = esAdmin == true
        }

        val header = binding.navView.getHeaderView(0)
        header.findViewById<TextView>(R.id.textView).text = user.email

        userViewModel.consultarUsuario(user.email!!)
        userViewModel.usuario.observe(this) { usuario ->
            if (usuario == null) {
                guardarUsuario(user)
            }
        }
    }

    private fun guardarUsuario(fireuser: FirebaseUser) {
        val user = UserModel(
            fireuser.email!!,
            fireuser.displayName ?: "",
            fireuser.uid
        )
        userViewModel.saveUsuario(user)

        val perfil = PerfilModel().apply {
            uid = fireuser.uid
            nombre = fireuser.displayName ?: ""
            email = fireuser.email!!
            foto = fireuser.photoUrl?.toString() ?: ""
        }
        perfilViewModel.savePerfil(perfil)
    }

    // ================= NAVIGATION =================

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener { item ->

            var handled = NavigationUI.onNavDestinationSelected(item, navController)

            if (item.itemId == R.id.nav_share) {
                compartirApp()
                handled = true
            }

            binding.drawerLayout.closeDrawers()
            handled
        }
    }

    // ================= PERMISOS =================

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            when {
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED -> {}

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // ================= IN-APP UPDATE =================

    private fun checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager!!.appUpdateInfo
            .addOnSuccessListener { info: AppUpdateInfo ->
                if (
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager!!.startUpdateFlowForResult(
                        info,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        MY_REQUEST_CODE
                    )
                }
            }
    }

    // ================= COMPARTIR APP =================

    private fun compartirApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "👋 Hola, te invito a descargar *App Emprende ISTG* 🚀\n" +
                        "👉 https://play.google.com/store/apps/details?id=istg.edu.ec.appEmprendeISTGDev"
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir vía"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.inicio, menu)

        val item = menu.findItem(R.id.action_settings)
        val spanString = SpannableString(item.title)

        val isNight =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES

        spanString.setSpan(
            ForegroundColorSpan(if (isNight) Color.WHITE else Color.BLACK),
            0,
            spanString.length,
            0
        )

        item.title = spanString
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro?")
                .setPositiveButton("Sí") { _, _ ->
                    progressDialog.show()
                    signOut()
                }
                .setNegativeButton("No", null)
                .show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        mAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener {
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp()
    }

    companion object {
        private const val MY_REQUEST_CODE = 100
    }
}

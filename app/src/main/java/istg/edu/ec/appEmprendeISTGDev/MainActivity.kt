package istg.edu.ec.appEmprendeISTGDev

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import istg.edu.ec.appEmprendeISTGDev.ui.activitys.Invitado
import istg.edu.ec.appEmprendeISTGDev.utils.setStatusBarColor
import istg.edu.ec.appEmprendeISTGDev.viewModel.UserViewModel

class MainActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    private var progressDialog: ProgressDialog? = null
    private lateinit var userViewModel: UserViewModel
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cambiar color barra estado
        this.setStatusBarColor(R.color.white, true)

        // ViewModel CORRECTO
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Configuración Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        // Botón Google
        findViewById<View>(R.id.btnGoogleSignIn).setOnClickListener {
            if (isConnectedToInternet) {
                signIn()
            } else {
                showNoInternetConnectionAlert()
            }
        }

        // Botón invitado
        findViewById<View>(R.id.btn_invitado).setOnClickListener {
            val intent = Intent(this, Invitado::class.java)
            intent.putExtra("evento", "invitado")
            startActivity(intent)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Iniciando sesión...")
        progressDialog!!.setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            navigateToInicioActivity()
        }
    }

    // --- Google Sign In ---
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle: ${acct.id}")

        progressDialog!!.show()

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                progressDialog!!.dismiss()

                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    val email = user?.email

                    if (email != null && (
                                email.endsWith("@gmail.com") ||
                                        email.endsWith("@est.istg.edu.ec") ||
                                        email.endsWith("@istg.edu.ec")
                                )
                    ) {
                        Toast.makeText(this, "Autenticación exitosa.", Toast.LENGTH_SHORT).show()
                        navigateToInicioActivity()
                    } else {
                        showInvalidEmailAlert()
                        mAuth!!.signOut()
                        mGoogleSignInClient!!.signOut()
                    }

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Error al autenticar.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToInicioActivity() {
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
        finish()
    }

    // --- Verificar conexión ---
    private val isConnectedToInternet: Boolean
        get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
            val networkInfo = cm?.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    private fun showNoInternetConnectionAlert() {
        AlertDialog.Builder(this)
            .setTitle("Sin conexión a Internet")
            .setMessage("Revisa tu conexión e inténtalo otra vez.")
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun showInvalidEmailAlert() {
        AlertDialog.Builder(this)
            .setTitle("Correo no permitido")
            .setMessage("Solo se permite el acceso a estudiantes y docentes del ISTG.")
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "MainActivity"
    }
}

package istg.edu.ec.appEmprendeISTGDev;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

// CAMBIO IMPORTANTE: Usamos androidx para que las alertas se vean modernas
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import istg.edu.ec.appEmprendeISTGDev.ui.activitys.Invitado;
import istg.edu.ec.appEmprendeISTGDev.viewModel.UserViewModel;

// Asegúrate de que esta ruta sea correcta en tu proyecto
import static istg.edu.ec.appEmprendeISTGDev.utils.StatusBarUtilsKt.setStatusBarColor;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private UserViewModel userViewModel;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cambiar color de barra de estado a blanco con íconos oscuros (true)
        setStatusBarColor(this, R.color.white, true);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Configuración para Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializa la instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Configura el botón para iniciar sesión con Google
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(view -> {
            if (isConnectedToInternet()) {
                signIn();
            } else {
                showNoInternetConnectionAlert();
            }
        });

        // Configura el botón para iniciar sesión como invitado
        findViewById(R.id.btn_invitado).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Invitado.class);
            intent.putExtra("evento", "invitado");
            startActivity(intent);
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Verifica si hay un usuario actualmente autenticado
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToInicioActivity();
        }
    }

    // Método para iniciar sesión con Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica si la solicitud es para el inicio de sesión con Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account); // Autentica con Firebase
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Error al iniciar sesión con Google.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para autenticar con Firebase usando la cuenta de Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            // Verifica correos institucionales o gmail
                            if (email != null && (email.endsWith("@gmail.com") ||
                                    email.endsWith("@est.istg.edu.ec") ||
                                    email.endsWith("@istg.edu.ec"))) {
                                Toast.makeText(MainActivity.this, "Autenticación exitosa.", Toast.LENGTH_SHORT).show();
                                navigateToInicioActivity();
                            } else {
                                showInvalidEmailAlert();
                                // Si no es válido, cerramos la sesión inmediatamente
                                mAuth.signOut();
                                mGoogleSignInClient.signOut();
                            }
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Error de autenticación.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToInicioActivity() {
        Intent intent = new Intent(MainActivity.this, InicioActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    // Alerta moderna (AppCompat)
    private void showNoInternetConnectionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Sin conexión a Internet")
                .setMessage("Revisa tu conexión a Internet y vuelve a intentarlo.")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    // Alerta moderna (AppCompat)
    private void showInvalidEmailAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Acceso Restringido")
                .setMessage("Solo se permite el inicio de sesión a estudiantes y docentes del ISTG.")
                .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}
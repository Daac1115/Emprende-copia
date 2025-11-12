package istg.edu.ec.appEmprendeISTGDev;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

import static istg.edu.ec.appEmprendeISTGDev.utils.StatusBarUtilsKt.setStatusBarColor;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    UserViewModel userViewModel;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarColor(this, R.color.white, true);


        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Configuración para Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Solicita el ID del token
                .requestEmail() // Solicita la dirección de correo electrónico
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

        // Configura el botón para iniciar sesión con invitado
           findViewById(R.id.btn_invitado).setOnClickListener(view -> {
               Intent intent = new Intent(MainActivity.this, Invitado.class);
               intent.putExtra("evento", "invitado"); // Enviamos el nombre del evento
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
                firebaseAuthWithGoogle(account); // Autentica con Firebase usando la cuenta de Google
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Método para autenticar con Firebase usando la cuenta de Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressDialog.show();

        // Crea las credenciales de autenticación usando el token de la cuenta de Google
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // La autenticación fue exitosa
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            // Verifica si el correo electrónico es válido
                            if (email != null && (email.endsWith("@gmail.com") || email.endsWith("@est.istg.edu.ec") || email.endsWith("@istg.edu.ec"))) {
                                Toast.makeText(MainActivity.this, "Autenticación exitosa.", Toast.LENGTH_SHORT).show();
                                navigateToInicioActivity(); // redirige a la actividad de inicio
                            } else {
                                showInvalidEmailAlert();
                                mAuth.signOut();
                                mGoogleSignInClient.signOut();
                            }
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para navegar a la actividad de inicio
    private void navigateToInicioActivity() {
        Intent intent = new Intent(MainActivity.this, InicioActivity.class);
        startActivity(intent);
        finish();
    }

    // Método para verificar la conexión a Internet
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    // metodo paralerta si no hay conexión a Internet
    private void showNoInternetConnectionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sin conexión a Internet")
                .setMessage("Revisa tu conexión a Internet y vuelve a intentarlo.")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    //metodo si el correo electrónico no es válido
    private void showInvalidEmailAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Correo electrónico no válido")
                .setMessage("Solo se permite el inicio de sesión a estudiantes y Docentes del ISTG")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}

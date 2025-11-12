package istg.edu.ec.appEmprendeISTGDev.ui.activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.core.content.ContextCompat
import istg.edu.ec.appEmprendeISTGDev.MainActivity
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.utils.setStatusBarColor


class PantallaInicialActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var videoView: VideoView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_inicial)

        setStatusBarColor(R.color.white, lightIcons = true)

        // Inicializar VideoView
        videoView = findViewById(R.id.videoView)
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.inicial}")
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }

        // Inicializar ProgressBar
        progressBar = findViewById(R.id.progress_bar)

        // Cambiar color del círculo giratorio
        progressBar.indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(this, R.color.navbar), // <-- este es tu color personalizado
            android.graphics.PorterDuff.Mode.SRC_IN
        )

        showLoading()

        // Después de 3 segundos, ocultar la barra y cambiar de actividad
        Handler(Looper.getMainLooper()).postDelayed({
            hideLoading()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3600)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }
}

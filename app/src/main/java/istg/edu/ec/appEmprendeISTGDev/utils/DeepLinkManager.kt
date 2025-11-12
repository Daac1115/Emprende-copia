package istg.edu.ec.appEmprendeISTGDev.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.lang.Exception

object DeepLinkManager {

    private const val WEB_REDIRECT_URL = "https://emprende-perfil.netlify.app"

    // Función para crear el enlace web (CORRECTA)
    fun buildWebRedirectUrl(userId: String, publicacionId: String): String {
        return "$WEB_REDIRECT_URL?userId=$userId&publicacionId=$publicacionId"
    }

    // Función para compartir una publicación (CORRECTA)
    fun sharePublication(context: Context, userId: String, publicacionId: String) {
        val webUrl = buildWebRedirectUrl(userId, publicacionId)
        val shareText = "¡Mira esta publicación en EmprendeISTG!\n$webUrl"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Publicación de EmprendeISTG")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir publicación"))
    }

    // ▼▼▼ FUNCIÓN handleIncomingIntent (VERSIÓN FINAL Y CORREGIDA) ▼▼▼
    fun handleIncomingIntent(
        activity: FragmentActivity,
        intent: Intent?,
        navHostResId: Int,
        publicacionDestinationId: Int // Solo necesitamos el destino de la publicación
    ): Boolean {
        val data: Uri = intent?.data ?: return false

        val navController: NavController = try {
            Navigation.findNavController(activity, navHostResId)
        } catch (e: Exception) {
            return false // No se puede navegar si no hay NavController
        }

        // Solo nos interesa el host "publication"
        if (data.host == "publication") {
            val userId = data.lastPathSegment
            val publicacionId = data.getQueryParameter("publicacionId")

            if (userId != null && publicacionId != null) {
                val bundle = Bundle().apply {
                    putString("userId", userId)
                    // El nombre del argumento debe coincidir con el de mobile_navigation.xml
                    putString("publicacionId", publicacionId)
                }
                navController.navigate(publicacionDestinationId, bundle)
                return true
            }
        }

        // Si el enlace no es para una publicación, no hacemos nada especial.
        // La app simplemente se abrirá en su pantalla de inicio por defecto.
        return false
    }
}
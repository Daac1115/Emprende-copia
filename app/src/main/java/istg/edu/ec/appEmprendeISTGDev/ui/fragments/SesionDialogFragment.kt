package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import istg.edu.ec.appEmprendeISTGDev.R
import istg.edu.ec.appEmprendeISTGDev.ui.activitys.PantallaInicialActivity

class SesionDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.fragment_sesion_dialog, null)

        val btnIniciarSesion = view.findViewById<Button>(R.id.btn_go_to_login)
        val btnSeguirNavegando = view.findViewById<Button>(R.id.btn_continue_as_guest)
        val btnCerrar = view.findViewById<ImageButton>(R.id.btn_close)
        // val tvTitulo = view.findViewById<TextView>(R.id.tv_title) // Opcional

        btnIniciarSesion.setOnClickListener {
            val intent = Intent(requireContext(), PantallaInicialActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        btnSeguirNavegando.setOnClickListener {
            dismiss()
        }

        btnCerrar.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
            .setView(view)
            .create()
    }
}

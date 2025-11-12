package istg.edu.ec.appEmprendeISTGDev.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import istg.edu.ec.appEmprendeISTGDev.R
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImagenSeleccionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImagenSeleccionFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var listener: OnImageSelectedListener

    interface OnImageSelectedListener {
        fun onImageSelected(imageUri: Bitmap?)
        fun onImageSelected2(imageUri2: Uri?)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnCamera).setOnClickListener()
        {
            dispatchTakePictureIntent()
        }

        view.findViewById<Button>(R.id.btnGallery).setOnClickListener {
            pickFromGallery()
        }
    }

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2


    private fun dispatchTakePictureIntent() {
        // Intent para abrir la cámara
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue otorgado, iniciar la cámara
                dispatchTakePictureIntent()
            } else {
                // El permiso fue denegado
                Toast.makeText(requireContext(), "Se necesita el permiso de la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)


    }
    private var bitmap: Bitmap? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    bitmap = data?.extras?.getParcelable("data")
                    listener.onImageSelected(bitmap)
                }
                REQUEST_IMAGE_GALLERY -> {
                    val imageUri2 = data?.data
                    // Asegúrate de que imageUri2 no sea nulo antes de intentar usarlo
                    imageUri2?.let { uri ->
                        try {
                            // Intentamos cargar el Bitmap desde el URI
                            bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                            listener.onImageSelected2(uri)
                        } catch (e: IOException) {
                            // Manejo de errores en caso de que falle la carga del Bitmap
                            Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        dismiss() // Cerrar el diálogo
    }

    fun setOnImageSelectedListener(listener: OnImageSelectedListener) {
        this.listener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_imagen_seleccion, container, false)
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ImagenSeleccionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImagenSeleccionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
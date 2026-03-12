package com.cibertec.agroconecta.Cliente.N_F_cliente

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.Maps.UbicacionActivity
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.FragmentMiPerfilcBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class FragmentMiPerfilc : Fragment() {

    private lateinit var binding : FragmentMiPerfilcBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var mContext : android.content.Context

    private var imgUri : Uri?=null

    override fun onAttach(context: android.content.Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentMiPerfilcBinding.inflate(layoutInflater,container, false)

        binding.imgPerfilc.setOnClickListener {
            seleccionarImg()
        }
        binding.txtUbicacionc.setOnClickListener {
            val intent = Intent(mContext, UbicacionActivity::class.java)
            obtenerUbicacion_ARL.launch(intent)
        }

        binding.btnGuardarPc.setOnClickListener {
            actualizarInfo()
        }
        return binding.root
    }
    private var nombres = ""
    private var email = ""
    private var dni = ""
    private var telefono = ""
    private fun actualizarInfo() {
        nombres = binding.txtNombrePerfilc.text.toString().trim()
        email = binding.txtEmailPerfilc.text.toString().trim()
        dni = binding.txtDniPc.text.toString().trim()
        telefono = binding.txtTelfPc.text.toString().trim()
        direccion = binding.txtUbicacionc.text.toString().trim()

        val hashMap : HashMap<String, Any> = HashMap()


        hashMap["nombres"] = "${nombres}"
        hashMap["email"] = "${email}"
        hashMap["dni"] = "${dni}"
        hashMap["telefono"] = "${telefono}"
        hashMap["direccion"] = "${direccion}"
        hashMap["latitud"] = "${latitud}"
        hashMap["longitud"] = "${longitud}"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContext, "Se actualizó la información",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext, "${e.message}",Toast.LENGTH_SHORT).show()

            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        leerInformacion()
    }
    /*Este metodo lee la informacion de la BD y la envia o setea en view de perfil*/
    private fun leerInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener datos de usuario
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val dni = "${snapshot.child("dni").value}"
                    val imagen = "${snapshot.child("imagen").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"
                    val fechaRegistro = "${snapshot.child("tRegistro").value}"
                    val direction = "${snapshot.child("direccion").value}"

                    val fecha = Constantes().obtenerFecha(fechaRegistro.toLong())

                    binding.txtNombrePerfilc.setText(nombres)
                    binding.txtEmailPerfilc.setText(email)
                    binding.txtDniPc.setText(dni)
                    binding.txtTelfPc.setText(telefono)
                    binding.fechaPerfilPc.text = "Se unio el $fecha"
                    binding.txtUbicacionc.setText(direccion)

                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.ico_fotoperfil)
                            .into(binding.imgPerfilc)
                    }catch (e:Exception){

                    }

                    if (proveedor == "email"){
                        binding.proveerdoPc.text = "La cuenta fue creada a través de su email"
                        binding.txtEmailPerfilc.isEnabled = false

                    }else if (proveedor == "google"){
                        binding.proveerdoPc.text = "La cuenta fue creada a través de una cuenta de Google"
                        binding.txtEmailPerfilc.isEnabled = false

                    }else if (proveedor == "telefono"){
                        binding.proveerdoPc.text = "La cuenta fue creada a través de su número telefónico"
                        binding.txtTelfPc.isEnabled = false
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }


            })
    }
    private fun seleccionarImg(){
        ImagePicker.with(this)
            .crop() //
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent {intent->
               resultadoImg.launch(intent)
            }
    }

    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imgUri = data!!.data
                subirImagenStorage(imgUri)
            }else{
                Toast.makeText(mContext, "Acción cancelada", Toast.LENGTH_SHORT).show()
            }
        }

    private fun subirImagenStorage(imagenUri: Uri?) {
        val rutaImagen = "imagenesPerfil/"+firebaseAuth.uid
        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.putFile(imagenUri!!)
            .addOnSuccessListener { taskSnapShot->
                val uriTask = taskSnapShot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagenCargada = uriTask.result.toString()
                if (uriTask.isSuccessful){
                    actualizarImagenBD(urlImagenCargada)
                }
            }
            .addOnFailureListener{e->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()

            }

    }
    private fun actualizarImagenBD(urlImagenCargada: String) {
        val hashMap : HashMap<String, Any> = HashMap()
        if (imgUri!=null){
            hashMap["imagen"] = urlImagenCargada
        }

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContext, "Su imagen de perfil se ha actualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private var latitud = 0.0
    private var longitud = 0.0
    private var direccion = ""

    private val obtenerUbicacion_ARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {

            val data = resultado.data

            if (data != null) {
                latitud = data.getDoubleExtra("latitud", 0.0)
                longitud = data.getDoubleExtra("longitud", 0.0)
                direccion = data.getStringExtra("direccion") ?: ""

                binding.txtUbicacionc.setText(direccion)
            }
        }
    }


}
package com.cibertec.agroconecta.Vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.databinding.ActivityRegistroVendedorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroVendedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)/* con esto ya tenemos acceso a los elementos registro vendedor los txt y boton*/


        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarV.setOnClickListener { validarInformacion() }
    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var repassword = ""

    private fun validarInformacion() {
        nombres = binding.edtNombresv.text.toString().trim()
        email = binding.edtEmail.text.toString().trim()
        password = binding.edtPass.text.toString().trim()
        repassword = binding.edtConfirm.text.toString().trim()
        if (nombres.isEmpty()) {
            binding.edtNombresv.error = "Ingrese sus nombres"
            binding.edtNombresv.requestFocus()
        } else if (email.isEmpty()) {
            binding.edtEmail.error = "Ingrese email"
            binding.edtEmail.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Email no valido"
            binding.edtEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.edtPass.error = "Ingrese password"
            binding.edtPass.requestFocus()
        } else if (password.length <= 6) {
            binding.edtPass.error = "Necesita 6 o mas caracteres"
            binding.edtPass.requestFocus()
        } else if (repassword.isEmpty()) {
            binding.edtConfirm.error = "confirmar password"
            binding.edtConfirm.requestFocus()
        } else if (password != repassword) {
            binding.edtConfirm.error = "No Coincide"
            binding.edtConfirm.requestFocus()
        } else {
            registrarVendedor()
        }
    }

    private fun registrarVendedor() {
        progressDialog.setMessage("creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "fallo el registro por que ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun insertarInfoBD() {
        progressDialog.setMessage("Guardando informacion..")

        val uidBD = firebaseAuth.uid
        val nombreBD = nombres
        val emailBD = email
        val tipoUsuario = "vendedor"
        val tiempoBD = Constantes().obtenerTiempoD()

        val datosVendedor = HashMap<String, Any>()

        datosVendedor["uid"] = "$uidBD"
        datosVendedor["nombres"] = "$nombreBD"
        datosVendedor["email"] = "$emailBD"
        datosVendedor["tipoUsuario"] ="vendedor"
        datosVendedor["timpo_registro"] = tiempoBD

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidBD!!)
            .setValue(datosVendedor)
            .addOnSuccessListener {
                 progressDialog.dismiss()
                 startActivity(Intent(this, MainActivityVendedor::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "fallo el registro en BD por que ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
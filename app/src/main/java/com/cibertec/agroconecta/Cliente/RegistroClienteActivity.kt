package com.cibertec.agroconecta.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.databinding.ActivityRegistroClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.collections.set

class RegistroClienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroClienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarc.setOnClickListener {
            validarInformacion()
        }
    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var cpassword = ""
    private fun validarInformacion() {
        nombres = binding.txtNombresc.text.toString().trim()
        email = binding.edtEmail.text.toString().trim()
        password = binding.edtPass.text.toString().trim()
        cpassword = binding.edtConfirm.text.toString().trim()

        if (nombres.isEmpty()) {
            binding.txtNombresc.error = "Ingrese sus nombres"
            binding.txtNombresc.requestFocus()
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
        } else if (cpassword.isEmpty()) {
            binding.edtConfirm.error = "confirmar password"
            binding.edtConfirm.requestFocus()
        } else if (password != cpassword) {
            binding.edtConfirm.error = "No Coincide"
            binding.edtConfirm.requestFocus()
        } else {
            registrarCliente()
        }


    }

    private fun registrarCliente(){
        progressDialog.setMessage("Creando Cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener {e->
                Toast.makeText(this,"Fallo el registro debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


        private fun insertarInfoBD() {
            progressDialog.setMessage("Guardando informacion")

            val uid = firebaseAuth.uid
            val nombrec = nombres
            val emailc = email
            val tiempoRegistro = Constantes().obtenerTiempoD()

            val datosCliente = HashMap<String, Any>()

            datosCliente["uid"] = "$uid"
            datosCliente["nombres"] = "$nombrec"
            datosCliente["email"] = "$emailc"
            datosCliente["telefono"] = ""
            datosCliente["dni"] = ""
            datosCliente["proveedor"] = "email"
            datosCliente["tRegistro"] = "$tiempoRegistro"
            datosCliente["imagen"] = ""
            datosCliente["tipoUsuario"] ="cliente"


            val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(uid!!)
                .setValue(datosCliente)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    startActivity(Intent(this@RegistroClienteActivity, MainActivityCliente::class.java))
                    finishAffinity()
                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Toast.makeText(this, "fallo el registro en BD por que ${e.message}", Toast.LENGTH_SHORT).show()
                }

        }
}
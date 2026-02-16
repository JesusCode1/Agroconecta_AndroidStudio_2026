package com.cibertec.agroconecta.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.databinding.ActivityLoginClienteBinding
import com.google.firebase.auth.FirebaseAuth

class LoginClienteActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding: ActivityLoginClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por Favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.tuRegistrameC.setOnClickListener {
            startActivity(Intent(this@LoginClienteActivity, RegistroClienteActivity::class.java))
        }


        binding.btnLoginC.setOnClickListener {
            validarInfo()
        }

    }

    private var email = ""
    private var password = ""




    private fun validarInfo() {
        email = binding.txtEmail.text.toString().trim()
        password = binding.txtPassword.text.toString().trim()
        if(email.isEmpty()){
            binding.txtEmail.error = "Ingrese email"
            binding.txtEmail.requestFocus()
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.txtEmail.error = "Email no válido"
            binding.txtEmail.requestFocus()
        }else if (password.isEmpty()){
            binding.txtPassword.error = "Ingrese password"
            binding.txtPassword.requestFocus()
        }else {
            loginCliente()
        }
    }

    private fun loginCliente(){
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
                Toast.makeText(
                    this,
                    "Bienvenido",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"No se pudo Iniciar sesion debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }



    }
}
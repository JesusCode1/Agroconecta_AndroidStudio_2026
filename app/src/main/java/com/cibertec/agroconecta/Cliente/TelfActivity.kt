package com.cibertec.agroconecta.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.databinding.ActivityTelfBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class TelfActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTelfBinding
    private lateinit var progressDialog : ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var forceResendingToken : ForceResendingToken?=null
    private lateinit var mCallback : OnVerificationStateChangedCallbacks
    private var mVerification : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.rltelefono.visibility = View.VISIBLE
        binding.cardCodigo.visibility = View.GONE

        phoneLoginCallbacks()

        binding.btnEnviarCodigo.setOnClickListener {
            validarData()
        }

        binding.btnVerificarCod.setOnClickListener {
            val otp = binding.etCodver.text.toString().trim()
            if (otp.isEmpty()){
                binding.etCodver.error = "Ingrese código"
                binding.etCodver.requestFocus()
            }else if (otp.length<6){
                binding.etCodver.error = "El código debe contener 6 car."
                binding.etCodver.requestFocus()
            }else{
                verificarCodTel(otp)
            }
        }

        binding.tvReenviarCod.setOnClickListener {
            if (forceResendingToken != null){
                reenviarCodVer()
            }else{
                Toast.makeText(this, "No se puede reeviar el código",
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun verificarCodTel(otp: String) {
        progressDialog.setMessage("Verificando código")
        progressDialog.show()

        val credencial = PhoneAuthProvider.getCredential(mVerification!!, otp)
        signInWithPhoneAuthCredencial(credencial)

    }

    private fun signInWithPhoneAuthCredencial(credencial: PhoneAuthCredential) {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult->
                if (authResult.additionalUserInfo!!.isNewUser){
                    guardarInfo()
                }else{
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }
            }
    }

    private fun guardarInfo() {
        progressDialog.setMessage("Guardando información")
        progressDialog.show()

        val uid = firebaseAuth.uid
        val tiempoReg = Constantes().obtenerTiempoD()

        val datosCliente = HashMap<String, Any>()

        datosCliente["uid"] = "${uid}"
        datosCliente["nombres"] = ""
        datosCliente["telefono"] = "${codTelnumTel}"
        datosCliente["email"] = ""
        datosCliente["dni"] = ""
        datosCliente["proveedor"] = "telefono"
        datosCliente["tRegistro"] = tiempoReg
        datosCliente["imagen"] = ""
        datosCliente["tipoUsuario"] = "cliente"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datosCliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this , MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(
                    this@TelfActivity,
                    "${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun reenviarCodVer() {
        progressDialog.setMessage("Enviando código a ${numeroTel}")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(codTelnumTel)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback)
            .setForceResendingToken(forceResendingToken!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var codigoTel = "" //+51
    private var numeroTel = "" //123456789
    private var codTelnumTel = "" //+51 123456789
    private fun validarData() {
        codigoTel = binding.telfCodPicker.selectedCountryCodeWithPlus
        numeroTel = binding.etTelefonoC.text.toString().trim()
        codTelnumTel = codigoTel + numeroTel

        if (numeroTel.isEmpty()){
            binding.etTelefonoC.error = "Ingrese número telefónico"
            binding.etTelefonoC.requestFocus()
        }else{
            verificarNumeroTel()
        }

    }

    private fun verificarNumeroTel() {
        progressDialog.setMessage("Enviando código a ${numeroTel}")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(codTelnumTel)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun phoneLoginCallbacks() {
        mCallback = object : OnVerificationStateChangedCallbacks(){
            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                mVerification = verificationId
                forceResendingToken = token

                progressDialog.dismiss()

                binding.rltelefono.visibility = View.GONE
                binding.cardCodigo.visibility = View.VISIBLE

                Toast.makeText(this@TelfActivity, "Enviando código ${codTelnumTel}", Toast.LENGTH_SHORT).show()
            }
            override fun onVerificationCompleted(phoneAuthCredencial: PhoneAuthCredential) {
                signInWithPhoneAuthCredencial(phoneAuthCredencial)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(
                    this@TelfActivity,
                    "Falló la verificación debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
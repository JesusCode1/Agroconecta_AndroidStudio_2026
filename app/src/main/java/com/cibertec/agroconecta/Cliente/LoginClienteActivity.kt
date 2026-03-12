package com.cibertec.agroconecta.Cliente


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.R

import com.cibertec.agroconecta.databinding.ActivityLoginClienteBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase


class LoginClienteActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var binding: ActivityLoginClienteBinding

    private lateinit var googleClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por Favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)


        binding.btntelf.setOnClickListener {
            startActivity(Intent(this, TelfActivity::class.java))
        }
        binding.tuRegistrameC.setOnClickListener {
            startActivity(Intent(this@LoginClienteActivity, RegistroClienteActivity::class.java))
        }

        binding.btnLoginC.setOnClickListener {
            validarInfo()
        }

        binding.btngoogle.setOnClickListener {
            googleLogin()
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

    /*Metodos de inicio sesion google*/
    /*   * Inicia el flujo de inicio de sesión con Google.
         * 1. Primero cierra cualquier sesión previa guardada en el dispositivo (esto se hace para forzar que siempre aparezca el selector de cuentas).
         * 2. Luego obtiene el Intent de GoogleSignIn.
         * 3. Lanza la pantalla donde el usuario puede elegir su cuenta de Google.
         * solo estamos solicitando el idToken a Google.    */
    private fun googleLogin() {

        // Cierra sesión primero para forzar que aparezca el selector
        googleClient.signOut().addOnCompleteListener {

            val googleSignInIntent = googleClient.signInIntent
            googleSignInARL.launch(googleSignInIntent)

        }
    }
    /*Metodo que comprueba si se selecciono una cuenta de cuadro de dialogo*/
    /*   * Recibe el resultado del selector de cuentas de Google.
         * - Si el usuario selecciona una cuenta correctamente:
         *      1. Obtiene la cuenta seleccionada.
         *      2. Extrae el idToken generado por Google.
         *      3. Llama al metodo autenticacionGoogle() para validar en Firebase.
         *
         * - Si el usuario cancela: Muestra un mensaje indicando que la operación fue cancelada.
         *   Aquí todavía no se guarda nada en la base de datos.*/
    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ resultado->
        if (resultado.resultCode == RESULT_OK){
            //Si el usuario seleccionó una cuenta del cuadro de diálogo
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                autenticacionGoogle(cuenta.idToken)
            }catch (e:Exception){
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"Se cancelo la operacion",Toast.LENGTH_SHORT).show()
        }

    }
    /*Metodo que inicia sesion */
    /*   * Autentica al usuario en Firebase usando el idToken de Google.
         * 1. Convierte el idToken en una credencial válida de Firebase.
         * 2. Firebase verifica si el token es auténtico.
         * 3. Si la autenticación es exitosa:
         *      - Verifica si el usuario es nuevo.
         *          • Si es nuevo → llama a llenarInfoBD() para guardar sus datos.
         *          • Si ya existía → lo envía directamente al MainActivity.
         * En este punto el usuario ya está autenticado en Firebase Authentication.*/
    private fun autenticacionGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { resultadoAuth->
                if (resultadoAuth.additionalUserInfo!!.isNewUser){
                    //Si el usuario es nuevo, registrar su información
                    llenarInfoBD()
                }else{
                    //Si el usuario ya se registró con anterioridad
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e->
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
    /*Metodo que guarda la info en firebase*/
    private fun llenarInfoBD() {
        progressDialog.setMessage("Guardando información")

        val uid = firebaseAuth.uid
        val nombreC = firebaseAuth.currentUser?.displayName
        val emailC = firebaseAuth.currentUser?.email
        val tiempoRegistro = Constantes().obtenerTiempoD()

        val datosCliente = HashMap<String, Any>()

        datosCliente["uid"] = "$uid"
        datosCliente["nombres"] = "$nombreC"
        datosCliente["email"] = "$emailC"
        datosCliente["telefono"] = ""
        datosCliente["dni"] = ""
        datosCliente["proveedor"] = "google"
        datosCliente["tRegistro"] = "$tiempoRegistro"
        datosCliente["imagen"] = ""
        datosCliente["tipoUsuario"] = "cliente"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uid!!)
            .setValue(datosCliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"${e.message}",Toast.LENGTH_SHORT).show()

            }



    }
}
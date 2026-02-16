package com.cibertec.agroconecta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Cliente.LoginClienteActivity
import com.cibertec.agroconecta.Vendedor.LoginVendedorActivity
import com.cibertec.agroconecta.databinding.ActivityStactivityBinding
import com.cibertec.agroconecta.databinding.FragmentIniciocBinding


class STActivity : AppCompatActivity() {


    private lateinit var binding: ActivityStactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tipoVendedor.setOnClickListener {
            startActivity(Intent(this@STActivity, LoginVendedorActivity::class.java))
        }
        binding.tipoCliente.setOnClickListener {
            startActivity(Intent(this@STActivity, LoginClienteActivity::class.java))
        }
    }
}
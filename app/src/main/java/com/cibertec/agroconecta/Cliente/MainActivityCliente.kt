package com.cibertec.agroconecta.Cliente

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.cibertec.agroconecta.Cliente.B_N_F_Cliente.FragmentOrdenesc
import com.cibertec.agroconecta.Cliente.B_N_F_Cliente.FragmentTiendac
import com.cibertec.agroconecta.Cliente.N_F_cliente.FragmentMiPerfilc
import com.cibertec.agroconecta.Cliente.N_F_cliente.Fragment_inicioc
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.STActivity
import com.cibertec.agroconecta.databinding.ActivityMainClienteBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivityCliente : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

   private lateinit var binding : ActivityMainClienteBinding
   private var firebaseAuth : FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()
        binding.navigationView.setNavigationItemSelectedListener (this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.opend,
            R.string.closed
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        replaceFragment(Fragment_inicioc())
        }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment,fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.inicio_c->{
                replaceFragment(Fragment_inicioc())
            }
            R.id.mi_perfil_c->{
                replaceFragment(FragmentMiPerfilc())
            }
            R.id.cerrar_sesion_c->{
                cerrarSesion()
            }
            R.id.tienda_c->{
                replaceFragment(FragmentTiendac())
            }
            R.id.mis_ordenes_c->{
                replaceFragment(FragmentOrdenesc())
            }
        }
         binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun comprobarSesion(){
        if(firebaseAuth!!.currentUser==null){
            startActivity(Intent(this@MainActivityCliente, STActivity::class.java))
            finishAffinity()
        }else{
            Toast.makeText(this,"Usuario en linea", Toast.LENGTH_SHORT).show()
        }
    }
    private fun cerrarSesion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(this@MainActivityCliente, STActivity::class.java))
        finishAffinity()
        Toast.makeText(this,"saliste de la sesion", Toast.LENGTH_SHORT).show()
    }
}
package com.cibertec.agroconecta.Vendedor

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.STActivity
import com.cibertec.agroconecta.Vendedor.B_N_F_Framents_Vendedor.FragmentMisProductosv
import com.cibertec.agroconecta.Vendedor.B_N_F_Framents_Vendedor.FragmentOrdenesv
import com.cibertec.agroconecta.Vendedor.N_F_vendedor.FragmentCategoriasv
import com.cibertec.agroconecta.Vendedor.N_F_vendedor.FragmentResenv
import com.cibertec.agroconecta.Vendedor.N_F_vendedor.Fragment_iniciov
import com.cibertec.agroconecta.Vendedor.N_F_vendedor.Fragmentmitiendav
import com.cibertec.agroconecta.databinding.ActivityMainVendedorBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivityVendedor : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainVendedorBinding
    private var firebaseAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.opend,
            R.string.closed
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        replaceFragment(Fragment_iniciov())
        binding.navigationView.setCheckedItem(R.id.inicio_v)


    }

    private fun comprobarSesion() {
        if (firebaseAuth!!.currentUser == null) {
            startActivity(Intent(applicationContext, STActivity::class.java))

        } else {
            Toast.makeText(applicationContext, "vendedor en linea", Toast.LENGTH_SHORT).show()
        }
    }


    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment,fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
          when (item.itemId){
              R.id.inicio_v->{
                  replaceFragment(Fragment_iniciov())
              }
              R.id.mi_tienda_v->{
                  replaceFragment(Fragmentmitiendav())
              }
              R.id.categorias_v->{
                  replaceFragment(FragmentCategoriasv())
              }
              R.id.resenia_v->{
                  replaceFragment(FragmentResenv())
              }
              R.id.cerrar_sesion_v->{
                  cerrarSesion()
              }
              R.id.mis_productos_v->{
                  replaceFragment(FragmentMisProductosv())
              }
              R.id.mis_ordenes_v->{
                  replaceFragment(FragmentOrdenesv())
              }
          }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun cerrarSesion(){
        firebaseAuth!!.signOut()
        startActivity(Intent(applicationContext, STActivity::class.java))
        finish()
        Toast.makeText(applicationContext,"Has cerrado session", Toast.LENGTH_SHORT).show()
    }

}
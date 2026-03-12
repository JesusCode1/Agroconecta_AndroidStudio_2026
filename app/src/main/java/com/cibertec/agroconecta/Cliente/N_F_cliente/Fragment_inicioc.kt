package com.cibertec.agroconecta.Cliente.N_F_cliente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.agroconecta.Cliente.B_N_F_Cliente.FragmentCarrito
import com.cibertec.agroconecta.Cliente.B_N_F_Cliente.FragmentOrdenesc
import com.cibertec.agroconecta.Cliente.B_N_F_Cliente.FragmentTiendac
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.FragmentIniciocBinding


class Fragment_inicioc : Fragment() {

   private  lateinit var binding: FragmentIniciocBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIniciocBinding.inflate(inflater,container,false)

        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.tienda_c->{
                   replaceFragment(FragmentTiendac())
                }
                R.id.mis_ordenes_c->{
                    replaceFragment(FragmentOrdenesc())
                }
                R.id.op_carrito_c->{
                    replaceFragment(FragmentCarrito())
                }
            }
            true
        }

        replaceFragment(FragmentTiendac())
        binding.bottomNavigation.selectedItemId = R.id.tienda_c
        return binding.root
    }
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment,fragment)
            .commit()
    }

}
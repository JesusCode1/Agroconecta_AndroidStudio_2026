package com.cibertec.agroconecta.Vendedor.N_F_vendedor

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.Vendedor.B_N_F_Framents_Vendedor.FragmentMisProductosv
import com.cibertec.agroconecta.Vendedor.B_N_F_Framents_Vendedor.FragmentOrdenesv
import com.cibertec.agroconecta.databinding.FragmentIniciovBinding


class Fragment_iniciov : Fragment() {

    private lateinit var binding : FragmentIniciovBinding
    private lateinit var  mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIniciovBinding.inflate(inflater,container,false)

        binding.btnNavigation.setOnItemSelectedListener{
           when(it.itemId){
               R.id.mis_productos_v->{
                  replaceFragment(FragmentMisProductosv())
               }
               R.id.mis_ordenes_v->{
                       replaceFragment(FragmentOrdenesv())
               }
         }
            true
        }

        replaceFragment(FragmentMisProductosv())
        binding.btnNavigation.selectedItemId = R.id.mis_productos_v

        binding.FloatB.setOnClickListener {
            Toast.makeText(
                mContext,
                "Has presionado el boton flotante" ,
                Toast.LENGTH_SHORT
            ).show()
        }

       return  binding.root
    }


    private fun replaceFragment(fragment: Fragment){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bfragment, fragment)
            .commit()
    }
}
package com.cibertec.agroconecta.Cliente.B_N_F_Cliente

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.agroconecta.Adaptadores.AdapOrden
import com.cibertec.agroconecta.Modelos.ModeloOrden
import com.cibertec.agroconecta.databinding.FragmentOrdenescBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentOrdenesc : Fragment() {

    private lateinit var binding : FragmentOrdenescBinding

    private lateinit var mContext : Context
    private lateinit var ordenesArrayList : ArrayList<ModeloOrden>
    private lateinit var ordenAdaptador : AdapOrden

    override fun onAttach(context: Context) {
        this.mContext = context
        super.onAttach(context)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOrdenescBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verOrdenes()
    }

    private fun verOrdenes() {
        ordenesArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        ref.orderByChild("ordenadoPor").equalTo(uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(ModeloOrden::class.java)
                        ordenesArrayList.add(modelo!!)
                    }

                    ordenAdaptador = AdapOrden(mContext, ordenesArrayList)
                    binding.ordenesRV.adapter = ordenAdaptador
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}
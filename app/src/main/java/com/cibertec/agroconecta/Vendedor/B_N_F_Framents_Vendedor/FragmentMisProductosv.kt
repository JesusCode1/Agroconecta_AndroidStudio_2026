package com.cibertec.agroconecta.Vendedor.B_N_F_Framents_Vendedor

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.agroconecta.Adaptadores.AdapProducto
import com.cibertec.agroconecta.Modelos.ModeloProducto
import com.cibertec.agroconecta.databinding.FragmentMisProductosvBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseAuth

class FragmentMisProductosv : Fragment() {

    private lateinit var binding : FragmentMisProductosvBinding
    private lateinit var mContext : Context

    private lateinit var productoArrayList : ArrayList<ModeloProducto>
    private lateinit var adaptadorProducto : AdapProducto

    override fun  onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentMisProductosvBinding.inflate(LayoutInflater.from(mContext), container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listarProductos()

    }

    private fun listarProductos() {

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        productoArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.orderByChild("uid").equalTo(uid)
            .addValueEventListener(object  : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                productoArrayList.clear()
                for (ds in snapshot.children){
                    val modeloProducto = ds.getValue(ModeloProducto::class.java)
                    if (modeloProducto != null) {
                        productoArrayList.add(modeloProducto)
                    }
                }
                adaptadorProducto = AdapProducto(mContext, productoArrayList)
                binding.productRV.adapter = adaptadorProducto
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}
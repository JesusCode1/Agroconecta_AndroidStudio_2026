package com.cibertec.agroconecta.Cliente.B_N_F_Cliente

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.agroconecta.Adaptadores.AdapCategoriac
import com.cibertec.agroconecta.Adaptadores.AdapVistaAletoria
import com.cibertec.agroconecta.Modelos.ModeloCategoria
import com.cibertec.agroconecta.Modelos.ModeloProducto
import com.cibertec.agroconecta.databinding.FragmentTiendacBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentTiendac : Fragment() {


    private lateinit var binding : FragmentTiendacBinding
    private lateinit var mContext : Context
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoriaArrayList : ArrayList<ModeloCategoria>
    private lateinit var adaptadorCategoria : AdapCategoriac

    private lateinit var productosArrayList : ArrayList<ModeloProducto>
    private lateinit var adaptadorProducto : AdapVistaAletoria

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTiendacBinding.inflate(LayoutInflater.from(mContext), container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        leerInfoCliente()
        listarCategorias()
        obtenerProductosAlea()
    }

    private fun leerInfoCliente(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                   // val direccion = "${snapshot.child("direccion").value}"
                    binding.txtBienvenido.setText("Bienvenido(a): ${nombres}")
                    //binding.direccionTXT.setText("${direccion}")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun obtenerProductosAlea() {
        productosArrayList = ArrayList()

        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                productosArrayList.clear()
                for (ds in snapshot.children){
                    val modeloProducto = ds.getValue(ModeloProducto::class.java)
                    productosArrayList.add((modeloProducto!!))
                }

                val listaAleatoria = ArrayList(productosArrayList.shuffled().take(5))

                adaptadorProducto = AdapVistaAletoria(mContext, listaAleatoria)
                binding.vistaAleatoriarv.adapter = adaptadorProducto
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun listarCategorias() {
        categoriaArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
            .orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children){
                    val modeloCat = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modeloCat!!)
                }

                adaptadorCategoria = AdapCategoriac(mContext, categoriaArrayList)
                binding.categoriarvc.adapter = adaptadorCategoria
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

}
package com.cibertec.agroconecta.Cliente.B_N_F_Cliente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cibertec.agroconecta.Adaptadores.AdapCarrito
import com.cibertec.agroconecta.Cliente.Orden.DetalleOrdencActivity
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.Modelos.ModeloCarrito
import com.cibertec.agroconecta.databinding.FragmentCarritoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentCarrito : Fragment() {

    private lateinit var binding: FragmentCarritoBinding
    private lateinit var mContext : Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var productoArrayList : ArrayList<ModeloCarrito>
    private lateinit var productoCarritoAdap : AdapCarrito

    override fun onAttach(context: Context){
        this.mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarritoBinding.inflate(inflater, container , false)
        binding.btnGeneraOrden.setOnClickListener {
            if (productoArrayList.size == 0){
                Toast.makeText(mContext, "No hay productos en el carrito",Toast.LENGTH_SHORT).show()
            }else{
                crearOrden()
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        cargarCarrito()
        suma()
    }
    //Paso4
    private fun cargarCarrito() {

        productoArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    productoArrayList.clear()
                    //RECORRE LA BASE DE DATOS 1,2,3 SI ENCUENTRA UNO DOS PRO
                    for (ds in snapshot.children){
                        val modeloCarrito = ds.getValue(ModeloCarrito::class.java)
                        productoArrayList.add(modeloCarrito!!)
                    }

                    productoCarritoAdap = AdapCarrito(mContext, productoArrayList)
                    binding.carritoRV.adapter = productoCarritoAdap    //EN EL RV LOS LISTA Y MUESTRA
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }

   /*fun para mostrar el total de todos los prod en el Fragmen_carrito.xml*/
    private fun suma() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var suma = 0.0
                    for (producto in snapshot.children){

                        val precioTotal = producto.child("precioTotal").getValue(String::class.java)

                        if (precioTotal!=null){
                            suma += precioTotal.toDouble()
                        }

                        binding.suma.text = "Total: S/ $suma"

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }


    /*Generar Orden*/
    private fun crearOrden() {
        val tiempo = Constantes().obtenerTiempoD()
        val totalOrden = binding.suma.text.toString().replace("Total: S/ ", "").trim()
        val uid = firebaseAuth.uid

        val ref = FirebaseDatabase.getInstance().getReference("Ordenes")
        val keyId = ref.push().key

        val hashMap = HashMap<String , Any> ()
        hashMap["idOrden"] = "${keyId}"
        hashMap["tiempoOrden"] = "${tiempo}"
        hashMap["estadoOrden"] = "Solicitud recibida"
        hashMap["totalOrden"] = "${totalOrden}"
        hashMap["ordenadoPor"] = "${uid}"

        ref.child(keyId!!).setValue(hashMap)
            .addOnSuccessListener {

                for (producto in productoArrayList){

                    val idProducto = producto.idProducto
                    val nombre = producto.nombre
                    val precio = producto.precio
                    val precioTotal = producto.precioTotal
                    val cantidad = producto.cantidad

                    val hashMap2 = HashMap<String , Any>()
                    hashMap2["idProducto"] = idProducto
                    hashMap2["nombre"] = nombre
                    hashMap2["precio"] = precio
                    hashMap2["precioTotal"] = precioTotal
                    hashMap2["cantidad"] = cantidad

                    ref.child(keyId).child("Productos").child(idProducto).setValue(hashMap2)
                    eliminarProductosCarrito()
                    //binding.suma.text = ""
                }
                Toast.makeText(mContext , "Orden realizada con éxito",Toast.LENGTH_SHORT).show()
                 val intent = Intent(mContext, DetalleOrdencActivity::class.java)
                 intent.putExtra("idOrden", keyId)
                 startActivity(intent)
            }
            .addOnFailureListener { e->
                Toast.makeText(mContext , "${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun eliminarProductosCarrito(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
            .child(uid!!).child("CarritoCompras")

        ref.removeValue().addOnCompleteListener {
            Toast.makeText(mContext, "Los productos se han eliminado del carrito",Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {e->
                Toast.makeText(mContext, "${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

}
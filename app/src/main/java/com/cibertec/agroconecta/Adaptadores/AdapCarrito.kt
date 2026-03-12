package com.cibertec.agroconecta.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.agroconecta.Modelos.ModeloCarrito
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.CarritoListaBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdapCarrito : RecyclerView.Adapter<AdapCarrito.HolderProductoCarrito>{

    private lateinit var binding: CarritoListaBinding

    private var mContext : Context
    var productoArrayList : ArrayList<ModeloCarrito>
    private var firebaseAuth : FirebaseAuth

    constructor(mContext: Context, productoArrayList: ArrayList<ModeloCarrito>) : super() {
        this.firebaseAuth = FirebaseAuth.getInstance()
        this.mContext = mContext
        this.productoArrayList = productoArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductoCarrito {
        binding = CarritoListaBinding.inflate(LayoutInflater.from(mContext),parent, false)
        return HolderProductoCarrito(binding.root)
    }


    //CARGA AL Carrito_lista.XML
    var costo : Double = 0.0
    override fun onBindViewHolder(holder: HolderProductoCarrito, position: Int) {
        val modeloCarrito = productoArrayList[position]

        val nombre = modeloCarrito.nombre
        var cantidad = modeloCarrito.cantidad
        var precioTotal = modeloCarrito.precioTotal
        var precio = modeloCarrito.precio


        holder.nombrecar.text = nombre
        holder.cantidadcar.text = cantidad.toString()

        holder.preciocar.text = "Precio: S/ $precio"
        holder.precioTotalcar.text = "Total: S/ $precioTotal"

        cargarImg(modeloCarrito, holder)


        holder.btnEliminar.setOnClickListener {
            eliminar(mContext , modeloCarrito.idProducto)
        }

        var precioTotalDouble = precioTotal.toDouble()

        holder.btnMas.setOnClickListener {

            val ref = FirebaseDatabase.getInstance().getReference("Productos")
            ref.child(modeloCarrito.idProducto).child("stock")
                .get()
                .addOnSuccessListener { snapshot ->

                    val stock = snapshot.value.toString().toInt()

                    if (cantidad < stock) {

                        costo = precio.toDouble()

                        precioTotalDouble += costo
                        cantidad++

                        holder.precioTotalcar.text = "Total: S/ $precioTotalDouble"
                        holder.cantidadcar.text = cantidad.toString()

                        val precioFinalString = precioTotalDouble.toString()

                        calcularPrecio(
                            mContext,
                            modeloCarrito.idProducto,
                            precioFinalString,
                            cantidad
                        )

                    } else {
                        Toast.makeText(mContext,"No hay más stock disponible",Toast.LENGTH_SHORT).show()
                    }

                }

        }

        holder.btnMenos.setOnClickListener {

            if (cantidad > 1) {

                costo = precio.toDouble()

                precioTotalDouble -= costo
                cantidad--

                holder.precioTotalcar.text = "Total: S/ $precioTotalDouble"
                holder.cantidadcar.text = cantidad.toString()

                val precioFinalString = precioTotalDouble.toString()

                calcularPrecio(
                    mContext,
                    modeloCarrito.idProducto,
                    precioFinalString,
                    cantidad
                )
            }
        }
    }


    override fun getItemCount(): Int {
        return productoArrayList.size
    }
    /*Aqui se carga la imagen*/
    private fun cargarImg(modeloCarrito: ModeloCarrito, holder: AdapCarrito.HolderProductoCarrito) {
        val idProducto = modeloCarrito.idProducto

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val imagenUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imagenUrl)
                                .placeholder(R.drawable.img_item)
                                .into(holder.imagencar)
                        }catch (e:Exception){

                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
    /*Funcion eliminar*/
    private fun eliminar(mContext: Context, idProducto: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras").child(idProducto)
            .removeValue()
            .addOnFailureListener {
                Toast.makeText(mContext , "Producto eliminado del carrito",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext , "${e.message}",Toast.LENGTH_SHORT).show()

            }

    }
     /*Aqui se calcula el precio para el onBinViewHolder*/
    private fun calcularPrecio(mContext: Context, idProducto: String, precioFinalString: String, cantidad: Int) {

        val hashMap: HashMap<String, Any> = HashMap()

        hashMap["cantidad"] = cantidad
        hashMap["precioTotal"] = precioFinalString

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")

        ref.child(firebaseAuth.uid!!)
            .child("CarritoCompras")
            .child(idProducto)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContext, "Cantidad actualizada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderProductoCarrito (itemView: android.view.View) : RecyclerView.ViewHolder(itemView){
        var imagencar = binding.listimgProshap
        var nombrecar = binding.listnombreCarr
        var preciocar = binding.listPrecioCarr
        var precioTotalcar = binding.listpreTotalCarr
        var cantidadcar = binding.listcantidadcarr
        var btnMas = binding.btnMas
        var btnMenos = binding.btnMenos
        var btnEliminar = binding.btnlistEliminar

    }
}
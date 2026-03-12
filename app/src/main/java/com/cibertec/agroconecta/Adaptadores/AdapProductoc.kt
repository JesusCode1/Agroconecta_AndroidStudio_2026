package com.cibertec.agroconecta.Adaptadores

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.agroconecta.DetalleProductoActivity
import com.cibertec.agroconecta.FiltroProducto
import com.cibertec.agroconecta.Modelos.ModeloProducto
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.ProductocBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapProductoc : RecyclerView.Adapter<AdapProductoc.HolderProducto> , Filterable{

    private lateinit var binding : ProductocBinding

    private var mContext : Context
    var productosArrayList : ArrayList<ModeloProducto>
    private var filtroLista : ArrayList<ModeloProducto>
    private var filtro : FiltroProducto ?= null

    constructor(mContex: Context, productosArrayList: ArrayList<ModeloProducto>) : super() {
        this.mContext = mContex
        this.productosArrayList = productosArrayList
        this.filtroLista = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ProductocBinding.inflate(LayoutInflater.from(mContext), parent,false)
        return HolderProducto(binding.root)
    }

    override fun onBindViewHolder(holder: HolderProducto, position: Int) {
        val modeloProducto = productosArrayList[position]
        val nombre = modeloProducto.nombre
        val precio = modeloProducto.precio
        val stock = modeloProducto.stock
        val direccion = modeloProducto.direccion

        cargarPImg(modeloProducto, holder)

        holder.item_nombrepro.text = "${nombre}"

        val formato = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("es", "PE"))
        holder.item_preciopro.text = formato.format(precio.toDouble())

        holder.item_stockpro.text = "Stock: $stock"
        holder.item_direccionpro.text = "Dir: ${modeloProducto.direccion}"

        //Evento para dirigirnos a la actividad de detalle
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetalleProductoActivity::class.java)
            intent.putExtra("idProducto", modeloProducto.id)
            mContext.startActivity(intent)

        }

        holder.agregar_carrito.setOnClickListener {
            verCarrito(modeloProducto)
        }

    }

    private fun cargarPImg(modeloProducto: ModeloProducto, holder: AdapProductoc.HolderProducto) {
        val idProducto = modeloProducto.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val imgUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imgUrl)
                                .placeholder(R.drawable.img_item)
                                .into(holder.imgpro)
                        }catch (e: Exception){

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            } )
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }



    inner class HolderProducto(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imgpro = binding.imgProshap
        var item_nombrepro = binding.itemNombrepro
        var item_preciopro = binding.itemPreciopro
        var item_stockpro = binding.itemStockpro

        var item_direccionpro = binding.itemDireccionpro

        var agregar_carrito = binding.agregarcarrito

    }


    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = FiltroProducto(this, filtroLista)
        }
        return filtro as FiltroProducto
    }
    //desde aqui se trabajo para el carrito
    var preciototal : Double = 0.0

    var cantidad : Int = 0

    var costo : Double = 0.0

    //Carrio.xml
    // se pasa como parametro modeloProducto ya que contiene la info extraida de Firebase
    private fun verCarrito(modeloProducto: ModeloProducto) {
        //Declarar vistas
        var imagenProshap: ShapeableImageView
        var nombreTv : TextView
        var descripcionTv : TextView
        var direccionTv : TextView
        var precioTv : TextView
        var stockTv : TextView
        var precioTotalTv : TextView
        var btnmenos : ImageButton
        var cantidadTv : TextView
        var btnmas : ImageButton
        var btnAgregarCarrito : MaterialButton

        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.carrito) //Hacemos la referencia a la vista (carrito.xml)

        imagenProshap = dialog.findViewById(R.id.imgcar)
        nombreTv = dialog.findViewById(R.id.txtNombrePcarr)
        descripcionTv = dialog.findViewById(R.id.txtDescPcarr)
        direccionTv = dialog.findViewById(R.id.txtdirPcarr)
        precioTv = dialog.findViewById(R.id.txtPrecioPCarr)
        stockTv = dialog.findViewById(R.id.txtstockPcarr)
        precioTotalTv = dialog.findViewById(R.id.txtPrecioTotalcarr)
        btnmenos = dialog.findViewById(R.id.btnMenos)
        cantidadTv = dialog.findViewById(R.id.txtcantidadCarr)
        btnmas = dialog.findViewById(R.id.btnMas)
        btnAgregarCarrito = dialog.findViewById(R.id.btnAgregarCarr)


        /*Obtener los datos del modelo*/
        val productoId = modeloProducto.id
        val nombre = modeloProducto.nombre
        val descripcion = modeloProducto.descripcion
        val precio = modeloProducto.precio
        val stock = modeloProducto.stock.toInt()
        val direccion = modeloProducto.direccion


        /*Setear la información*/
        nombreTv.setText(nombre)
        descripcionTv.setText(descripcion)
        direccionTv.setText(direccion)
        precioTv.text = "Precio: S/ $precio x kg"
        stockTv.text = "Stock: $stock kg"

        /*Convertir precio*/
        costo = precio.toDouble()

        /*Valores iniciales*/
        cantidad = 1
        preciototal = costo

        cantidadTv.text = cantidad.toString()
        precioTotalTv.text = "Total: S/ $preciototal"

        /*Botón aumentar no puede superar al stock*/
        btnmas.setOnClickListener {
           if (cantidad < stock) {
               cantidad++
               preciototal = costo * cantidad

               cantidadTv.text = cantidad.toString()
               precioTotalTv.text = "Total: S/ $preciototal"
           }else{
               Toast.makeText(mContext,"No hay más stock disponible",Toast.LENGTH_SHORT).show()
           }
        }

        btnmenos.setOnClickListener {

            if (cantidad > 1){
                cantidad--
                preciototal = costo * cantidad

                cantidadTv.text = cantidad.toString()
                precioTotalTv.text = "Total: S/ $preciototal"
            }
        }

        cargarImg(productoId, imagenProshap)
        //Paso 2
        btnAgregarCarrito.setOnClickListener {
            agregarCarrito(mContext, modeloProducto , preciototal , cantidad)
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(true)

    }
    //img para cargar carrito
    private fun cargarImg(productoId: String, imagenSIV: ShapeableImageView) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(productoId).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        //Extraer la url de la primera imagen
                        val imagenUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imagenUrl)
                                .placeholder(R.drawable.img_item)
                                .into(imagenSIV)
                        }catch (e:Exception){

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
    //Paso2
    private fun agregarCarrito(mContex: Context, modeloProducto: ModeloProducto, precioTotal: Double, cantidad: Int) {

        val firebaseAuth = FirebaseAuth.getInstance()

        val hashMap = HashMap<String, Any>()

        hashMap["idProducto"] = modeloProducto.id
        hashMap["nombre"] = modeloProducto.nombre
        hashMap["precio"] = modeloProducto.precio
        hashMap["cantidad"] = cantidad
        hashMap["precioTotal"] = precioTotal.toString()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras").child(modeloProducto.id)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContex, "Se agregó al carrito el producto",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(mContex, "${e.message}",Toast.LENGTH_SHORT).show()

            }

    }

}
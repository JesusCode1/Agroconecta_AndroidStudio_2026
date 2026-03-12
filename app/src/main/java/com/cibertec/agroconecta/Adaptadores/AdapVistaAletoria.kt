package com.cibertec.agroconecta.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.agroconecta.DetalleProductoActivity
import com.cibertec.agroconecta.Modelos.ModeloProducto
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.ProductoVistaAleatoriaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdapVistaAletoria : RecyclerView.Adapter<AdapVistaAletoria.HolderProductosAletorios>{

    private lateinit var binding : ProductoVistaAleatoriaBinding

    private var mContext : Context
    var productoArrayList : ArrayList<ModeloProducto>

    constructor(mContext: Context, productoArrayList: ArrayList<ModeloProducto>) {
        this.mContext = mContext
        this.productoArrayList = productoArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductosAletorios {
        binding = ProductoVistaAleatoriaBinding.inflate(LayoutInflater.from(mContext),parent, false)
        return HolderProductosAletorios(binding.root)
    }
    override fun getItemCount(): Int {
        return productoArrayList.size
    }

    override fun onBindViewHolder(holder: HolderProductosAletorios, position: Int) {
        val modeloProducto = productoArrayList[position]

        val nombreP = modeloProducto.nombre
        val categoriaP = modeloProducto.categoria
        val precioP = modeloProducto.precio


        cargarPrimeraImg(modeloProducto, holder)

        holder.nombreP.text = "${nombreP}"
        holder.precioP.text = "S/ $precioP"
        //holder.item_categoria_p.text = "${categoriaP}"

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetalleProductoActivity::class.java)
            intent.putExtra("idProducto", modeloProducto.id)
            mContext.startActivity(intent)
        }
    }

    private fun cargarPrimeraImg(modeloProducto: ModeloProducto, holder: AdapVistaAletoria.HolderProductosAletorios) {

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
                                    .into(holder.imagenP)
                            }catch (e: Exception){

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                } )

    }

    inner class HolderProductosAletorios(item : View) : RecyclerView.ViewHolder(item){
        var imagenP = binding.imgProshap
        var nombreP = binding.itemNombrepro
        var precioP = binding.itemPreciopro
        var stockP = binding.itemStockpro


    }
}
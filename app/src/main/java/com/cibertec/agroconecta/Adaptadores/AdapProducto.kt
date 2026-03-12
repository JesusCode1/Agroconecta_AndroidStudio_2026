package com.cibertec.agroconecta.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.cibertec.agroconecta.Modelos.ModeloProducto
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.Vendedor.MainActivityVendedor
import com.cibertec.agroconecta.Vendedor.Productos.AgregarProductoActivity
import com.cibertec.agroconecta.databinding.ItemProductoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdapProducto : RecyclerView.Adapter<AdapProducto.HolderProducto> {

    private lateinit var binding : ItemProductoBinding

    private var mContex : Context
    private var productosArrayList : ArrayList<ModeloProducto>

    constructor(mContex: Context, productosArrayList: ArrayList<ModeloProducto>) : super() {
        this.mContex = mContex
        this.productosArrayList = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ItemProductoBinding.inflate(LayoutInflater.from(mContex), parent,false)
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

        holder.Ib_editar.setOnClickListener {
            val intent = Intent(mContex, AgregarProductoActivity::class.java)
            intent.putExtra("Edicion",true)
            intent.putExtra("idProducto",modeloProducto.id)
            mContex.startActivity(intent)
        }

        holder.Ib_eliminar.setOnClickListener {  
            eliminarProducto(modeloProducto)
        }
        holder.Ib_eliminar.setOnClickListener {
            val mAlertDialog = MaterialAlertDialogBuilder(mContex)
            mAlertDialog.setTitle("Eliminar producto")
                .setMessage("¿Estás seguro(a) de eliminar el producto?")
                .setPositiveButton("Eliminar"){dialog,which->
                    eliminarProducto(modeloProducto)
                }
                .setNegativeButton("Cancelar"){dialog,which->
                    dialog.dismiss()
                }
                .show()

        }

    }

    private fun cargarPImg(modeloProducto: ModeloProducto, holder: AdapProducto.HolderProducto) {
        val idProducto = modeloProducto.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val imgUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContex)
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


    private fun eliminarProducto(modeloProducto: ModeloProducto) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(modeloProducto.id)
            .removeValue()
            .addOnSuccessListener {
                val intent = Intent(mContex, MainActivityVendedor::class.java)
                mContex.startActivity(intent)
                Toast.makeText(mContex, "Producto eliminado",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                Toast.makeText(mContex, "${e.message}",Toast.LENGTH_SHORT).show()
            }

    }
    inner class HolderProducto(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgpro = binding.imgProshap
        var item_nombrepro = binding.itemNombrepro
        var item_preciopro = binding.itemPreciopro
        var item_stockpro = binding.itemStockpro

        var item_direccionpro = binding.itemDireccionpro

        var Ib_editar = binding.btnEditarProv

        var Ib_eliminar = binding.btnEliminarProv
    }
}



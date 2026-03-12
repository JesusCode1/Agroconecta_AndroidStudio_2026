package com.cibertec.agroconecta.Adaptadores

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.agroconecta.Modelos.ModeloCategoria
import com.cibertec.agroconecta.databinding.CategoriavBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class AdapCategoriav : RecyclerView.Adapter<AdapCategoriav.HolderCategoriav>{

    private lateinit var binding : CategoriavBinding
    private val mContext : Context
    private val categoriaArrayList : ArrayList<ModeloCategoria>

    constructor(mContext: Context, categoriaArrayList: ArrayList<ModeloCategoria>) {
        this.mContext = mContext
        this.categoriaArrayList = categoriaArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriav {
        binding = CategoriavBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderCategoriav(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategoriav, position: Int) {
        val modelo = categoriaArrayList[position]

        val id = modelo.id
        val categoria = modelo.categoria
        //val imagen = modelo.imagenUrl

        holder.itnombre_categoriav.text = categoria

        /*Glide.with(mContext)
            .load(imagen)
            .placeholder(R.drawable.categorias)
            .into(holder.item_img_c_v)*/

        holder.iteliminarc.setOnClickListener {
           // actualizarNomCat(id)
            //Toast.makeText(mContext,"Eliminar categoria", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Eliminar categoria")
            builder.setMessage("¿Estas Seguro de eliminar?")
                .setPositiveButton("Confirmar"){a,d->
                    eliminarCategoria(modelo, holder)

                }
                .setNegativeButton("Cancelar"){a,d->
                    a.dismiss()
                }
            builder.show()
        }
    }



    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    private fun eliminarCategoria(modelo: ModeloCategoria, holder: HolderCategoriav) {

        val idCategoria = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        ref.child(idCategoria).removeValue()
            .addOnSuccessListener {
                Toast.makeText(mContext,"Se elimino categoria", Toast.LENGTH_SHORT).show()
                eliminarImgCat(idCategoria)
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext,"No se elimino categoria debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarImgCat(idCategoria: String) {
        val nombreimg = idCategoria
        val rutaimg = "Categorias/$nombreimg"
        val storage = FirebaseStorage.getInstance().getReference(rutaimg)
        storage.delete()
            .addOnSuccessListener {
                Toast.makeText(mContext,"Se elimino la imagen de categoria", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(mContext,"${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderCategoriav(itemView: View) : RecyclerView.ViewHolder(itemView){
        var itnombre_categoriav = binding.itnombreCategoriav
        var iteliminarc = binding.iteliminarc

    }


}



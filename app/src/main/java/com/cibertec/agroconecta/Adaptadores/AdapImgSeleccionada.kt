package com.cibertec.agroconecta.Adaptadores

import android.view.LayoutInflater
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.agroconecta.Modelos.ModeloImagenSeleccionada
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.ImgSeleccionadasBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdapImgSeleccionada (

    private val  context : Context,
    private val imgArrayList : ArrayList<ModeloImagenSeleccionada>,
    private val idProducto : String,
    private val edicion: Boolean
    ): RecyclerView.Adapter<AdapImgSeleccionada.HolderImgSeleccionada>(){
    private lateinit var binding: ImgSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImgSeleccionada {
         binding = ImgSeleccionadasBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImgSeleccionada(binding.root)
    }

    override fun onBindViewHolder(holder: HolderImgSeleccionada, position: Int) {
       val modelo = imgArrayList[position]

        if (modelo.deInternet){
            try {
                val imagenUrl = modelo.imagenUrl
                Glide.with(context)
                    .load(imagenUrl)
                    .placeholder(R.drawable.img_item)
                    .into(holder.item_img)
            }catch (e:Exception){

            }
        }else{
            //Leyendo la imagen(es)
            val imagenUri = modelo.imageUri
            try {
                Glide.with(context)
                    .load(imagenUri)
                    .placeholder(R.drawable.img_item)
                    .into(holder.item_img)
            }catch (e:Exception){

            }
        }

        // 🔹 Deshabilitar eliminar si queda solo 1 imagen en edición
        if (edicion && imgArrayList.size == 1){
            holder.btn_borrar.isEnabled = false
        }else{
            holder.btn_borrar.isEnabled = true
        }

         //Evento para eliminar una imagen de la lista
        holder.btn_borrar.setOnClickListener {
            // 🔹 evitar borrar la última imagen
            if (edicion && imgArrayList.size == 1){
                Toast.makeText(context,"Debe existir al menos una imagen",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (modelo.deInternet){
                eliminarImgFirebase(modelo, holder, position)
            }

            imgArrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imgArrayList.size
    }

    inner class HolderImgSeleccionada(itemView : View) : RecyclerView.ViewHolder(itemView){
       var item_img = binding.iimg
        var btn_borrar = binding.borraItem
    }



    private fun eliminarImgFirebase(modelo: ModeloImagenSeleccionada, holder: AdapImgSeleccionada.HolderImgSeleccionada, position: Int) {

        val idImagen = modelo.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes").child(idImagen)
            .removeValue()
            .addOnSuccessListener {
                try {
                    imgArrayList.remove(modelo)
                    notifyItemRemoved(position)
                    eliminarImgStorage(modelo)
                }catch (e:Exception){
                    Toast.makeText(context, "${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e->
                Toast.makeText(context, "${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarImgStorage(modelo: ModeloImagenSeleccionada) {
        val rutaImagen = "Productos/"+modelo.id

        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Imagen eliminada",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context, "${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

}





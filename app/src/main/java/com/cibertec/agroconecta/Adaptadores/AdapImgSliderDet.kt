package com.cibertec.agroconecta.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.agroconecta.Modelos.ModeloImgSliderDet
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.ImgSliderDetBinding
import com.google.android.material.imageview.ShapeableImageView

class AdapImgSliderDet : RecyclerView.Adapter<AdapImgSliderDet.HolderImagenSlider>{

    private lateinit var binding : ImgSliderDetBinding
    private var context : Context
    private var imagenArrayList : ArrayList<ModeloImgSliderDet>

    constructor(context: Context, imagenArrayList: ArrayList<ModeloImgSliderDet>) {
        this.context = context
        this.imagenArrayList = imagenArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSlider {
        binding = ImgSliderDetBinding.inflate(LayoutInflater.from(context),parent, false)
        return HolderImagenSlider(binding.root)
    }

    override fun getItemCount(): Int {
         return imagenArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSlider, position: Int) {
         val modeloImagenSlider = imagenArrayList[position]

        val imagenUrl = modeloImagenSlider.imagenUrl
        val imagenContador = "${position+1}/${imagenArrayList.size}" //2/4 3/4
        holder.imagenContadorTv.text = imagenContador

        try {
            Glide.with(context)
                .load(imagenUrl)
                .placeholder(R.drawable.img_item)
                .into(holder.imagenSIV)
        }catch (e:Exception){

        }

        /*holder.itemView.setOnClickListener {
            zoomImg(imagenUrl)
        }*/

    }

    inner class HolderImagenSlider(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imagenSIV : ShapeableImageView = binding.imgSha
        var imagenContadorTv : TextView = binding.imgdetV
    }
    /*
    private fun zoomImg(imagen : String){
        val pv : PhotoView
        val btnCerrar : MaterialButton

        val dialog = Dialog(context)

        dialog.setContentView(R.layout.zoom_imagen)

        pv = dialog.findViewById(R.id.zoomImg)
        btnCerrar = dialog.findViewById(R.id.cerrarZoom)

        try {
            Glide.with(context)
                .load(imagen)
                .placeholder(R.drawable.item_img_producto)
                .into(pv)
        }catch (e:Exception){

        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }*/
}
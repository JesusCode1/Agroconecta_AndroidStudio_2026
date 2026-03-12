package com.cibertec.agroconecta.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.agroconecta.Modelos.ModeloOrden
import com.cibertec.agroconecta.Modelos.ModeloOrdenProducto
import com.cibertec.agroconecta.databinding.OrdenProductoBinding


class AdapProductoOrden : RecyclerView.Adapter<AdapProductoOrden.HolderProductoOrden>{

    private lateinit var binding : OrdenProductoBinding
    private var mContext : Context
    private var productosArrayList : ArrayList<ModeloOrdenProducto>

    constructor(mContext: Context, productosArrayList: ArrayList<ModeloOrdenProducto>) {
        this.mContext = mContext
        this.productosArrayList = productosArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProductoOrden {
        binding = OrdenProductoBinding.inflate(LayoutInflater.from(mContext), parent , false)
        return HolderProductoOrden(binding.root)
    }

    override fun onBindViewHolder(holder: HolderProductoOrden, position: Int) {
        val modeloProductoOrden = productosArrayList[position]

        val nombre = modeloProductoOrden.nombre
        val precio = modeloProductoOrden.precio


        holder.itemNombreP.text = nombre
        holder.itemPrecioP.text = precio

    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }


    inner class HolderProductoOrden (itemView: View) : RecyclerView.ViewHolder(itemView){

        var itemNombreP = binding.itemNombreP
        var itemPrecioP = binding.itemPrecioP

    }

}
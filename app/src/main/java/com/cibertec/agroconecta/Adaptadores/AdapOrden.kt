package com.cibertec.agroconecta.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.agroconecta.Cliente.Orden.DetalleOrdencActivity
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.Modelos.ModeloOrden
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.OrdenCompraBinding


class AdapOrden : RecyclerView.Adapter<AdapOrden.HolderOrdenCompra>{

    private lateinit var binding : OrdenCompraBinding

    private var mContext : Context
    var ordenesArrayList : ArrayList<ModeloOrden>

    constructor(mContext: Context, ordenesArrayList: ArrayList<ModeloOrden>) : super() {
        this.mContext = mContext
        this.ordenesArrayList = ordenesArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderOrdenCompra {
        binding = OrdenCompraBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return HolderOrdenCompra(binding.root)
    }

    override fun getItemCount(): Int {
        return ordenesArrayList.size
    }

    override fun onBindViewHolder(holder: HolderOrdenCompra, position: Int) {
        val ordenCompra = ordenesArrayList[position]

        val idOrden = ordenCompra.idOrden
        val tiempoOrden = ordenCompra.tiempoOrden
        val totalOrden = ordenCompra.totalOrden
        val estadoOrden = ordenCompra.estadoOrden

        holder.idOrdenItem.text = idOrden
        holder.costoOrdenItem.text = totalOrden
        holder.estadoOrdenItem.text = estadoOrden

        if (estadoOrden.equals("Solicitud recibida")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.azul_marino_oscuro))
        }else if (estadoOrden.equals("Pago Pendiente")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.morado))
        }
        else if (estadoOrden.equals("En Preparación")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.naranja))
        }else if (estadoOrden.equals("Entregado")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.verde_oscuro2))
        }else if (estadoOrden.equals("Cancelado")){
            holder.estadoOrdenItem.setTextColor(ContextCompat.getColor(mContext, R.color.rojo))
        }

        val fecha = Constantes().obtenerFecha(tiempoOrden.toLong())

        binding.fechaOrdenItem.text = fecha

       holder.ibSiguiente.setOnClickListener {
            val intent = Intent(mContext, DetalleOrdencActivity::class.java)
            intent.putExtra("idOrden", idOrden)
            mContext.startActivity(intent)
        }

    }

    inner class HolderOrdenCompra (itemView : View) : RecyclerView.ViewHolder(itemView){
        var idOrdenItem = binding.idOrdenItem
        var fechaOrdenItem = binding.fechaOrdenItem
        var estadoOrdenItem = binding.estadoOrdenItem
        var costoOrdenItem = binding.costoOrdenItem
        var ibSiguiente = binding.ibSiguiente
    }
}
package com.cibertec.agroconecta.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.agroconecta.Cliente.Productos.ProductosCatcActivity
import com.cibertec.agroconecta.Modelos.ModeloCategoria
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.CategoriacBinding

class AdapCategoriac : RecyclerView.Adapter<AdapCategoriac.HolderCategoriaC>{

    private lateinit var binding : CategoriacBinding

    private var mContext : Context
    private var categoriaArrayList : ArrayList<ModeloCategoria>

    constructor(mContext: Context, categoriaArrayList: ArrayList<ModeloCategoria>) : super() {
        this.mContext = mContext
        this.categoriaArrayList = categoriaArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoriaC {
        binding = CategoriacBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderCategoriaC(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoriaC, position: Int) {
        val modelo = categoriaArrayList[position]

        val categoria = modelo.categoria
        val imagen = modelo.imagenUrl

        holder.nombrecc.text = categoria

        Glide.with(mContext)
            .load(imagen)
            .placeholder(R.drawable.ico_categoria)
            .into(holder.imgcatcc)

       //Evento para ver productos de una categoria
        holder.verproductoscc.setOnClickListener {
            val intent = Intent(mContext, ProductosCatcActivity::class.java)
            intent.putExtra("nombreCat", categoria)
            Toast.makeText(mContext, "Categoria seleccionada ${categoria}", Toast.LENGTH_SHORT).show()
            mContext.startActivity(intent)
        }
    }

    inner class HolderCategoriaC (itemView : View) : RecyclerView.ViewHolder(itemView){
        var nombrecc = binding.lblnombrecatc
        var imgcatcc = binding.imgCategc
        var verproductoscc = binding.verproductoc
    }
}
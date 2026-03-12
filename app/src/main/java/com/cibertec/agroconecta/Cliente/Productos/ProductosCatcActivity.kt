package com.cibertec.agroconecta.Cliente.Productos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Adaptadores.AdapProductoc
import com.cibertec.agroconecta.Modelos.ModeloProducto
import com.cibertec.agroconecta.databinding.ActivityProductosCatcBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductosCatcActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProductosCatcBinding

    private lateinit var productoArrayList : ArrayList<ModeloProducto>
     private lateinit var  adapProductoc   :  AdapProductoc

    private var nombreCat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductosCatcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //obteniendo el nom de categoria enviada del adaptador
        nombreCat = intent.getStringExtra("nombreCat").toString()
        binding.txtProCatTitulo.text = "Categoria - ${nombreCat}"
        listarProductos(nombreCat)

        binding.txtBuscarProducto.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(filtro: CharSequence?, start: Int, before: Int, count: Int) {
                try {

                    val consulta = filtro.toString()
                    adapProductoc.filter.filter(consulta)

                }catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.btnlimpiartextprodcli.setOnClickListener {
            val consulta = binding.txtBuscarProducto.text.toString().trim()
            if (consulta.isNotEmpty()){
                binding.txtBuscarProducto.setText("")
                Toast.makeText(this, "Campo limpiado",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "No se ha ingresado una consulta",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun listarProductos(nombreCat: String) {

        productoArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.orderByChild("categoria").equalTo(nombreCat)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    productoArrayList.clear()
                    for (ds in snapshot.children){
                        val modeloProducto = ds.getValue(ModeloProducto::class.java)
                        productoArrayList.add(modeloProducto!!)
                    }
                    productoArrayList.sortBy { it.nombre }
                    adapProductoc = AdapProductoc(this@ProductosCatcActivity, productoArrayList)
                    binding.productorvcatc.adapter = adapProductoc
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}
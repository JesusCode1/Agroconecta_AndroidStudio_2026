package com.cibertec.agroconecta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Adaptadores.AdapImgSliderDet
import com.cibertec.agroconecta.Modelos.ModeloImgSliderDet
import com.cibertec.agroconecta.Modelos.ModeloProducto
import com.cibertec.agroconecta.databinding.ActivityDetalleProductoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleProductoActivity : AppCompatActivity() {


    private lateinit var binding : ActivityDetalleProductoBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var idProducto = ""

    private lateinit var imagenSlider : ArrayList<ModeloImgSliderDet>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)



        firebaseAuth = FirebaseAuth.getInstance()

        //Obtenemos el id del producto enviado desde el adaptador
        idProducto = intent.getStringExtra("idProducto").toString()

        cargarImagenesProd()
        cargarInfoProducto()
       /*
        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvDejarCalificacion.setOnClickListener {
            val intent = Intent(this , CalificarProductoActivity::class.java)
            intent.putExtra("idProducto", idProducto)
            startActivity(intent)
        }

        binding.tvPromCal.setOnClickListener {
            val intent = Intent(this , MostrarCalificacionesActivity::class.java)
            intent.putExtra("idProducto", idProducto)
            startActivity(intent)
        }

        calcularPromedioCal(idProducto)*/

    }
    /*
    private fun calcularPromedioCal(idProducto: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos/$idProducto/Calificaciones")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var sumaCalificaciones = 0
                var totalCalificaciones = 0

                for (calificacionSn in snapshot.children){

                    val calificacion = calificacionSn.child("calificacion").getValue(Int::class.java)

                    if (calificacion != null){
                        sumaCalificaciones += calificacion
                        totalCalificaciones++
                    }

                }

                if (totalCalificaciones > 0){
                    val promedio = sumaCalificaciones.toDouble() / totalCalificaciones //10 / 2 = 5

                    binding.tvPromCal.text = promedio.toString().plus("/5")
                    binding.tvTotalCal.text = ("(${totalCalificaciones})")
                    binding.ratingBar.rating = promedio.toFloat()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })










    }*/

    private fun cargarInfoProducto() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val modeloProducto = snapshot.getValue(ModeloProducto::class.java)

                    val nombre = modeloProducto?.nombre
                    val descripcion = modeloProducto?.descripcion
                    val precio = modeloProducto?.precio
                    val stock = modeloProducto?.stock
                    val direccion = modeloProducto?.direccion

                    binding.detnombre.text = nombre
                    binding.detdescrip.text = descripcion
                    binding.detprecio.text = "$precio s/ x kg"
                    binding.detstok.text = stock
                    binding.detubicacion.text = direccion

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun cargarImagenesProd() {
        imagenSlider = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    imagenSlider.clear()
                    for (ds in snapshot.children){
                        try {
                            val modeloImgSlider = ds.getValue(ModeloImgSliderDet::class.java)
                            imagenSlider.add(modeloImgSlider!!)
                        }catch (e:Exception){

                        }
                    }
                    val adaptadorImgSlider = AdapImgSliderDet(this@DetalleProductoActivity, imagenSlider)
                    binding.detimgvp.adapter = adaptadorImgSlider
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
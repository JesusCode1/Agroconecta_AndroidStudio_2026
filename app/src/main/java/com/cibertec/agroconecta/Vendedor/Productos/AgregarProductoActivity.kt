package com.cibertec.agroconecta.Vendedor.Productos

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.agroconecta.Adaptadores.AdapImgSeleccionada
import com.cibertec.agroconecta.Constantes
import com.cibertec.agroconecta.Maps.UbicacionActivity
import com.cibertec.agroconecta.Modelos.ModeloCategoria
import com.cibertec.agroconecta.Modelos.ModeloImagenSeleccionada
import com.cibertec.agroconecta.Vendedor.MainActivityVendedor
import com.cibertec.agroconecta.databinding.ActivityAgregarProductoBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAgregarProductoBinding
    private var imgUr : Uri?=null
    private lateinit var imgSeleccionadaArrayList: ArrayList<ModeloImagenSeleccionada>
    private lateinit var adapImgSelec: AdapImgSeleccionada

    private lateinit var categoriaArrayList: ArrayList<ModeloCategoria>

    private lateinit var progressDialog: ProgressDialog

    private var Edicion = false

    private var idProducto = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setCanceledOnTouchOutside(false)

        Edicion = intent.getBooleanExtra("Edicion",false)

        if (Edicion){
            idProducto = intent.getStringExtra("idProducto") ?: ""
            binding.txtAgregarProducto.text = "Editar Producto"
            cargarInfo()
        }else{
            binding.txtAgregarProducto.text = "Agregar Producto"
        }

        imgSeleccionadaArrayList = ArrayList()

        /*Se llama a la fun seleccionar img */
        binding.imgAgregarPro.setOnClickListener {
            selecionarImg()
        }
        binding.txtCategoriapro.setOnClickListener {
            seleccionarCat()
        }
        binding.btnAgregarProducto.setOnClickListener {
            validarInfo()
        }
        binding.txtUbicacionPro.setOnClickListener {val intent = Intent(this, UbicacionActivity::class.java)
        obtenerUbicacion_ARL.launch(intent)
        }
        cargarImg()

    }



    private var nombrePro = ""
    private var descripcionPro = ""
    private var categoriaPro = ""
    private var precioPro = ""
    private var stockPro = ""

    private fun validarInfo() {

        nombrePro = binding.txtNombrepro.text.toString().trim()
        descripcionPro = binding.txtDescripcionpro.text.toString().trim()
        categoriaPro = binding.txtCategoriapro.text.toString().trim()
        precioPro = binding.txtPrecioPro.text.toString().trim()
        stockPro = binding.txtStock.text.toString().trim()

        if (nombrePro.isEmpty()) {
            binding.txtNombrepro.error = "Ingrese nombre"
            binding.txtNombrepro.requestFocus()

        } else if (descripcionPro.isEmpty()) {
            binding.txtDescripcionpro.error = "Ingrese Descripcion"
            binding.txtDescripcionpro.requestFocus()

        } else if (categoriaPro.isEmpty()) {
            binding.txtCategoriapro.error = "Seleccione Categoria"
            binding.txtCategoriapro.requestFocus()

        } else if (precioPro.isEmpty()) {
            binding.txtPrecioPro.error = "Agregue Precio por kilo"
            binding.txtPrecioPro.requestFocus()

        } else if (stockPro.isEmpty()) {
            binding.txtStock.error = "Ingrese Stock disponible"
            binding.txtStock.requestFocus()

        }
        else {

            if (Edicion) {
                actualizarInfo()
            }
            else {
                if (imgSeleccionadaArrayList.isEmpty()) {
                    Toast.makeText(this, "Agregue una imagen", Toast.LENGTH_SHORT).show()
                } else {
                    agregarProducto()
                }
            }
        }
    }

    private fun agregarProducto(){
           progressDialog.setMessage("Agregando Producto")
           progressDialog.show()

        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        var keyId = ref.push().key
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["nombre"] = "${nombrePro}"
        hashMap["descripcion"] = "${descripcionPro}"
        hashMap["categoria"] = "${categoriaPro}"
        hashMap["precio"] = "${precioPro}"
        hashMap["stock"] = "${stockPro}"
        hashMap["latitud"] = "${latitud}"
        hashMap["longitud"] = "${longitud}"
        hashMap["direccion"] = "${direccion}"
        hashMap["uid"] = "${uid}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                subirImgsStorage(keyId)
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "${e.message}",Toast.LENGTH_SHORT).show()
            }

    }


    private fun subirImgsStorage(keyId: String) {

        for (i in imgSeleccionadaArrayList.indices){
            val modeloImagenSel = imgSeleccionadaArrayList[i]

            if (!modeloImagenSel.deInternet){
                val nombreImagen = modeloImagenSel.id
                val rutaImagen = "Productos/$nombreImagen"

                val storageRef = FirebaseStorage.getInstance().getReference(rutaImagen)
                storageRef.putFile(modeloImagenSel.imageUri!!)

                    .addOnSuccessListener {taskSnapshot->
                        val uriTask = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val urlImgCargada = uriTask.result

                        if (uriTask.isSuccessful){
                            val hashMap = HashMap<String, Any>()
                            hashMap["id"] = "${modeloImagenSel.id}"
                            hashMap["imagenUrl"] = "${urlImgCargada}"

                            val ref = FirebaseDatabase.getInstance().getReference("Productos")
                            ref.child(keyId).child("Imagenes")
                                .child(nombreImagen)
                                .updateChildren(hashMap)

                        }
                        if (Edicion){
                            progressDialog.dismiss()
                            val intent = Intent(this@AgregarProductoActivity, MainActivityVendedor::class.java)
                            startActivity(intent)
                            Toast.makeText(this,"Se actualizó la información del producto",Toast.LENGTH_SHORT).show()
                            finishAffinity()
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this, "Se agregó el producto",Toast.LENGTH_SHORT).show()
                            limpiarCampos()
                        }
                    }
                    .addOnFailureListener {e->
                        progressDialog.dismiss()
                        Toast.makeText(this, "${e.message}",Toast.LENGTH_SHORT).show()
                    }
            }


        }
    }


    private fun limpiarCampos() {
        imgSeleccionadaArrayList.clear()
        adapImgSelec.notifyDataSetChanged()
        binding.txtNombrepro.setText("")
        binding.txtDescripcionpro.setText("")
        binding.txtPrecioPro.setText("")
        binding.txtCategoriapro.setText("")
        binding.txtPrecioPro.setText("")
        binding.txtStock.setText("")
        binding.txtUbicacionPro.setText("")
    }











    /*DESDE AQUI SE TRABAJO FUNCIONES CATEGORIA Y CARGAR IMAGEN A PRODUCTO*/
    private fun cCategorias() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")/*Esto leera Categorias <- q es la base de datos y categoria el atributo*/
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               categoriaArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private var idcategoria = ""
    private var tituloCategoria =""

    private fun seleccionarCat(){
        val categoriaArray = arrayOfNulls<String>(categoriaArrayList.size)
        for (i in categoriaArray.indices){
            categoriaArray[i] = categoriaArrayList[i].categoria
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("seleccione una categoria")
            .setItems(categoriaArray){
                dialog, witch ->
                idcategoria = categoriaArrayList[witch].id
                tituloCategoria = categoriaArrayList[witch].categoria
                binding.txtCategoriapro.text = tituloCategoria
            }
            .show()
    }

    private fun cargarImg() {
        adapImgSelec  = AdapImgSeleccionada(this, imgSeleccionadaArrayList, idProducto, Edicion)
        binding.rvImagenespro.adapter = adapImgSelec
    }

    private fun selecionarImg(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .createIntent { intent ->
                    resultadoImg.launch(intent)
            }
    }

    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if(resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imgUr = data!!.data
                val tiempo = "${Constantes().obtenerTiempoD()}"

                val modeimgselec = ModeloImagenSeleccionada(tiempo, imgUr, null,false)
                imgSeleccionadaArrayList.add(modeimgselec)
                cargarImg()
            }else{
                Toast.makeText(this,"Accion cancelada", Toast.LENGTH_SHORT).show()
            }

        }

    /*HASTA AQUI SE TRABAJO CAT Y CARGA IMG*/

    /*Aqui comienzo a agregar la ubicacion*/
    private var latitud = 0.0
    private var longitud = 0.0
    private var direccion = ""

    private val obtenerUbicacion_ARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK) {

            val data = resultado.data

            if (data != null) {
                latitud = data.getDoubleExtra("latitud", 0.0)
                longitud = data.getDoubleExtra("longitud", 0.0)
                direccion = data.getStringExtra("direccion") ?: ""

                binding.txtUbicacionPro.setText(direccion)
            }
        }
    }
     /*Actualizar la info*/
    private fun cargarInfo() {
        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                /*Obteniendo la información de Firebase*/
                val nombre = "${snapshot.child("nombre").value}"
                val descripcion = "${snapshot.child("descripcion").value}"
                val categoria = "${snapshot.child("categoria").value}"
                val precio = "${snapshot.child("precio").value}"
                val stock = "${snapshot.child("stock").value}"
                val direccion = "${snapshot.child("direccion").value}"

                /*Seteo de información*/
                binding.txtNombrepro.setText(nombre)
                binding.txtDescripcionpro.setText(descripcion)
                binding.txtCategoriapro.setText(categoria)
                binding.txtPrecioPro.setText(precio)
                binding.txtStock.setText(stock)
                binding.txtUbicacionPro.setText(direccion)

                val refImagenes = snapshot.child("Imagenes").ref
                refImagenes.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children){
                            val id = "${ds.child("id").value}"
                            val imagenUrl = "${ds.child("imagenUrl").value}"

                            val modeloImgSelec = ModeloImagenSeleccionada(
                                id=id, imageUri = null , imagenUrl = imagenUrl , deInternet = true
                            )
                            imgSeleccionadaArrayList.add(modeloImgSelec)
                        }
                        cargarImg()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun actualizarInfo() {
        progressDialog.setMessage("Actualizando producto")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["nombre"] = "${nombrePro}"
        hashMap["descripcion"] = "${descripcionPro}"
        hashMap["categoria"] = "${categoriaPro}"
        hashMap["precio"] = "${precioPro}"
        hashMap["stock"] = "${stockPro}"
        hashMap["latitud"] = "${latitud}"
        hashMap["longitud"] = "${longitud}"
        hashMap["direccion"] = "${direccion}"

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                subirImgsStorage(idProducto)
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Falló la actualización debido a ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }
}







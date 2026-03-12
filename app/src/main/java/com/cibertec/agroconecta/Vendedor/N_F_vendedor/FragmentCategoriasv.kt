package com.cibertec.agroconecta.Vendedor.N_F_vendedor

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.cibertec.agroconecta.Adaptadores.AdapCategoriav
import com.cibertec.agroconecta.Modelos.ModeloCategoria
import com.cibertec.agroconecta.R
import com.cibertec.agroconecta.databinding.FragmentCategoriasvBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class FragmentCategoriasv : Fragment() {

    private lateinit var binding: FragmentCategoriasvBinding
    private lateinit var mContext: Context
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri? = null

    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>
    private lateinit var adaptadorCatV : AdapCategoriav

    //private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>
    // private lateinit var adaptadorCategoriaV : AdapCategoriaV

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriasvBinding.inflate(inflater, container, false)

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Espere")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.imgCategoria.setOnClickListener {
            selecImgCate()
        }

        binding.btnAgregarCat.setOnClickListener {
            validarInformacion()
        }
        listarCategorias()

        return binding.root
    }

    private fun listarCategorias() {
        categoriasArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
                adaptadorCatV = AdapCategoriav(mContext, categoriasArrayList)
                binding.rvCategoria.adapter =  adaptadorCatV
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun selecImgCate() {
        ImagePicker.with(requireActivity())
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                resultadoImg.launch(intent)
            }


    }

    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                val data = resultado.data
                imageUri = data!!.data
                binding.imgCategoria.setImageURI(imageUri)
            } else {
                Toast.makeText(mContext, "Acción cancelada", Toast.LENGTH_SHORT).show()
            }
        }

    private var categoria = ""
    private fun validarInformacion() {
        categoria = binding.tCategoria.text.toString().trim()
        if (categoria.isEmpty()) {
            Toast.makeText(context, "ingrese una categoria", Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(context, "Agregue imagen", Toast.LENGTH_SHORT).show()
        } else {
            agregarCategoriaBD()
        }
    }

    private fun agregarCategoriaBD() {
        progressDialog.setMessage("Agregando")
        progressDialog.show()

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["categoria"] = "${categoria}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //progressDialog.dismiss()
                //Toast.makeText(context, "Se agregó la categoría con éxito",Toast.LENGTH_SHORT).show()
                //binding.tCategoria.setText("")
                subirImgStorage(keyId)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()

            }


    }


    private fun subirImgStorage(keyId: String) {
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()
        val nombreImagen = keyId
        val nombreCarpeta = "Categoria/$nombreImagen"
        val storageReference = FirebaseStorage.getInstance().getReference(nombreCarpeta)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {taskSnapshot ->
                progressDialog.dismiss()
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImgCargada = uriTask.result

                if (uriTask.isSuccessful){
                    val hashMap = HashMap<String, Any>()
                    hashMap["imagenUrl"] = "$urlImgCargada"
                    val ref = FirebaseDatabase.getInstance().getReference("Categorias")
                    ref.child(nombreImagen).updateChildren(hashMap)
                    Toast.makeText(mContext,"Se agregó la categoria con éxito",Toast.LENGTH_SHORT).show()
                    binding.tCategoria.setText("")
                    imageUri = null
                    binding.imgCategoria.setImageURI(imageUri)
                    binding.imgCategoria.setImageResource(R.drawable.ico_foto)
                }
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(context, "${e.message}",Toast.LENGTH_SHORT).show()

            }

    }

}





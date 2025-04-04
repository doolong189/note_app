package dev.hoanglong180903.noteapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE = 1
    private var imageUri: Uri? = null
    private var imageList = ArrayList<Uri>()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var urlStrings: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        SocketIOManager(this)
        binding.uploadImage.setOnClickListener {
            urlStrings = ArrayList()
            progressDialog.show()
            binding.alert.text = "If Loading Takes too long press button again"
            val imageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder")
            for (uploadCount in imageList.indices) {
                val individualImage = imageList[uploadCount]
                val imageName = imageFolder.child("Images" + individualImage.lastPathSegment)
                imageName.putFile(individualImage).addOnSuccessListener { taskSnapshot ->
                    imageName.downloadUrl.addOnSuccessListener { uri ->
                        urlStrings.add(uri.toString())
                        if (urlStrings.size == imageList.size) {
                            storeLink(urlStrings)
                            updateTextView(urlStrings)
                        }
                    }
                }
            }

        }
    }

    fun mupload_image(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, PICK_IMAGE)
    }

    fun storeLink(urlStrings: ArrayList<String>) {
        val hashMap = HashMap<String, String>()
        for (i in urlStrings.indices) {
            hashMap["ImgLink$i"] = urlStrings[i]
        }
        val databaseReference = FirebaseDatabase.getInstance().getReference().child("User")
        databaseReference.push().setValue(hashMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        progressDialog.dismiss()
        binding.alert.text = "Uploaded Successfully"
        binding.uploadImage.visibility = View.GONE
        imageList.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            data?.clipData?.let { clipData ->
                val countClipData = clipData.itemCount
                val selectedImages = ArrayList<Uri>()
                for (i in 0 until countClipData) {
                    val imageUri = clipData.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
                binding.alert.visibility = View.VISIBLE
                binding.alert.text = "You have selected $countClipData Images"
                binding.chooseImage.visibility = View.GONE
                imageList.clear()
                imageList.addAll(selectedImages)

            } ?: run {
                data?.data?.let { uri ->
                    imageUri = uri
                    binding.alert.visibility = View.VISIBLE
                    binding.alert.text = "You have selected 1 Image"
                    binding.chooseImage.visibility = View.GONE
                    imageList.clear()
                    imageList.add(uri)
                } ?: run {
                    Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateTextView(urlStrings: ArrayList<String>) {
        val urlListString = urlStrings.joinToString(separator = "\n") { it }
        binding.urlTextView.text = urlListString
    }
}
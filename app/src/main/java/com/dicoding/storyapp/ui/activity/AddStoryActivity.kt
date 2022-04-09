package com.dicoding.storyapp.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.data.remote.response.ApiResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.helper.rotateBitmap
import com.dicoding.storyapp.helper.showToast
import com.dicoding.storyapp.helper.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class AddStoryActivity : AppCompatActivity() {
  private lateinit var binding: ActivityAddStoryBinding

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_CODE_PERMISSIONS) {
      if (!allPermissionsGranted()) {
        Toast.makeText(
          this,
          "Tidak mendapatkan permission.",
          Toast.LENGTH_SHORT
        ).show()
        finish()
      }
    }
  }

  private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAddStoryBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupToolbar()

    if (!allPermissionsGranted()) {
      ActivityCompat.requestPermissions(
        this,
        REQUIRED_PERMISSIONS,
        REQUEST_CODE_PERMISSIONS
      )
    }

    binding.btnCameraX.setOnClickListener { startCameraX() }
    binding.btnGallery.setOnClickListener { startGallery() }
    binding.btnUpload.setOnClickListener { uploadImage() }
  }

  private fun setupToolbar(){
    setSupportActionBar(binding.myToolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeButtonEnabled(true)
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }

  // cameraX
  private fun startCameraX() {
    launcherIntentCameraX.launch(Intent(this, CameraActivity::class.java))
  }

  private var getFile: File? = null
  private val launcherIntentCameraX = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    if (it.resultCode == CAMERA_X_RESULT) {
      val myFile = it.data?.getSerializableExtra("picture") as File
      val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

      // rotate
//      val result = rotateBitmap(
//        BitmapFactory.decodeFile(myFile.path),
//        isBackCamera
//      )

      getFile = myFile
      val result = rotateBitmap(
        BitmapFactory.decodeFile(getFile?.path),
        isBackCamera
      )

      binding.ivPreview.setImageBitmap(result)
    }
  }


  private fun startGallery() {
    val intent = Intent()
    intent.action = Intent.ACTION_GET_CONTENT
    intent.type = "image/*"
    val chooser = Intent.createChooser(intent, "Choose a Picture")
    launcherIntentGallery.launch(chooser)
  }

  private val launcherIntentGallery = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    if (it.resultCode == RESULT_OK) {
      val selectedImg: Uri = it.data?.data as Uri
      val myFile = uriToFile(selectedImg, this@AddStoryActivity)
      getFile = myFile
      binding.ivPreview.setImageURI(selectedImg)
    }
  }

  private fun uploadImage() {
    if (getFile != null) {
      val file = reduceFileImage(getFile as File)

//      val description = "Ini adalah deksripsi gambar".toRequestBody("text/plain".toMediaType())
      val description = "Ini adalah deksripsi gambar"
      val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
      val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo",
        file.name,
        requestImageFile
      )

      val service = ApiConfig().getApiService().addStories(description, imageMultipart,"token")
      service.enqueue(object : Callback<ApiResponse> {
        override fun onResponse(
          call: Call<ApiResponse>,
          response: Response<ApiResponse>
        ) {
          if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null && !responseBody.error) {
              showToast(this@AddStoryActivity, responseBody.message)
            }
          } else {
            showToast(this@AddStoryActivity, response.message())
          }
        }
        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
          showToast(this@AddStoryActivity, "Gagal instance Retrofit")
        }
      })

    } else {
      showToast(this@AddStoryActivity, "Silakan masukkan berkas gambar terlebih dahulu.")
    }
  }

  // reduce image size
  private fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
      val bmpStream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
      val bmpPicByteArray = bmpStream.toByteArray()
      streamLength = bmpPicByteArray.size
      compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
  }

  companion object {
    const val CAMERA_X_RESULT = 200

    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private const val REQUEST_CODE_PERMISSIONS = 10
  }

}
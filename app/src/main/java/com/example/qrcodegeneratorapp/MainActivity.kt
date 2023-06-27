package com.example.qrcodegeneratorapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.example.qrcodegeneratorapp.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.net.URI

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //...code

        // full Screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        // init Views
        binding.apply {
            // generator qr code
            generator.setOnClickListener {
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    val bitmap = generateQrCode(text)
                    imageQr.setImageBitmap(bitmap)
                    share.isEnabled = true
                } else {
                    Toast.makeText(applicationContext, "Text Is Empty", Toast.LENGTH_SHORT).show()
                }
            }
            // share button
            share.setOnClickListener {
                shareQr()
            }
        }

    }

    private fun shareQr() {
        val bitmap = (binding.imageQr.drawable).toBitmap()
        val uri = getImageUri(bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Share QrCode"))
    }

    // get Image Uri
    fun getImageUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "QR Code", null)
        return Uri.parse(path)
    }

    private fun generateQrCode(text: String): Bitmap? {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}
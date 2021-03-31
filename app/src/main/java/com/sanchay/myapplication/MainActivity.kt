package com.sanchay.myapplication

import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    val PICK_IMAGE = 111;
    val PICK_IMAGE_2 = 121;
    var detector : FaceDetector ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initML()
        findViewById<ImageView>(R.id.pan_img).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        findViewById<ImageView>(R.id.curr_img).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_2)
        }

        findViewById<Button>(R.id.compare).setOnClickListener {
            val img = InputImage.fromBitmap(findViewById<ImageView>(R.id.pan_img).drawable.toBitmap(200,200),0)
            detector?.process(img)?.addOnSuccessListener {try{
                val drawingView = DrawRect(applicationContext, it)
                drawingView.draw(Canvas(findViewById<ImageView>(R.id.curr_img).drawable.toBitmap(200,200)))
                runOnUiThread { findViewById<ImageView>(R.id.curr_img).setImageBitmap(findViewById<ImageView>(R.id.curr_img).drawable.toBitmap(200,200)) }
            } catch (e:Exception) {
                Toast.makeText(this,"Add second image for comparison",Toast.LENGTH_LONG).show()

                e.printStackTrace()
            }
            }
                ?.addOnFailureListener {
                    Toast.makeText(this,"Something went Wrong",Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun initML() {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        detector = FaceDetection.getClient(options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

                //Get image
                val imageUri = data?.getData();
                val imageStream = imageUri?.let { getContentResolver().openInputStream(it) };
                val selectedImage = BitmapFactory.decodeStream(imageStream);

                findViewById<ImageView>(R.id.pan_img).setImageBitmap(selectedImage)
        }

        if (requestCode == PICK_IMAGE_2&& resultCode == Activity.RESULT_OK) {
            val imageUri = data?.getData();
            val imageStream = imageUri?.let { getContentResolver().openInputStream(it) };
            val selectedImage = BitmapFactory.decodeStream(imageStream);

            findViewById<ImageView>(R.id.curr_img).setImageBitmap(selectedImage)

        }
    }


}

class DrawRect(context: Context, var faceObject: List<Face>) : View(context) {
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val pen = Paint()
        for (item in faceObject) {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = item.boundingBox
            canvas.drawRect(box, pen)
        }
    }
}
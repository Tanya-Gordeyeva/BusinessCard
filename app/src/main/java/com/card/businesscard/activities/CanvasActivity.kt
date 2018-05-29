package com.card.businesscard.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.card.businesscard.R
import org.json.JSONArray
import android.view.View
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Bitmap
import android.widget.Button
import kotlinx.android.synthetic.main.activity_canvas.*


@Suppress("CAST_NEVER_SUCCEEDS")
class CanvasActivity : AppCompatActivity() {

    private var decodedImage: Bitmap? = null
    private var boundsArray: JSONArray = JSONArray()
    private var newBoundsArray: ArrayList<JSONArray> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)

        val picture = intent.getByteArrayExtra("image")
        decodedImage = BitmapFactory.decodeByteArray(picture, 0, picture.size) as Bitmap

        val bounds = intent.getStringExtra("bounds")
        boundsArray = JSONArray(bounds)

        val contRecognition = findViewById<Button>(R.id.button_continue) as Button
        main.addView(DrawView(this))
        contRecognition.setOnClickListener {
            intent.putExtra("newBounds", newBoundsArray.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    internal inner class DrawView(context: Context) : View(context) {

        protected val mPaint: Paint? = Paint(Paint.DITHER_FLAG)
        protected var mBitmap: Bitmap? = decodedImage!!.copy(Bitmap.Config.ARGB_8888, true)
        protected var mCanvas: Canvas? = null
        val mBounds: ArrayList<JSONArray> = ArrayList()


        init {
            mPaint!!.isAntiAlias = true
            mPaint.isDither = true
            mPaint.color = getContext().resources.getColor(android.R.color.black)
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeJoin = Paint.Join.ROUND
            mPaint.strokeCap = Paint.Cap.ROUND
            mPaint.strokeWidth = 3f
        }

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)


            canvas.translate((canvas.width / 2 + mBitmap!!.height).toFloat(), 0f)
            canvas.rotate(90f, 0f, 0f)
            canvas.scale(1.1f*(canvas.height / decodedImage!!.width).toFloat(),1.1f*(canvas.height / decodedImage!!.width).toFloat())

            canvas.drawBitmap(mBitmap, 0f, 0f, mPaint)
            (0 until boundsArray.length()).forEach { i ->
                val it = boundsArray[i] as JSONArray
                mBounds.add(it)
                canvas.drawRect(RectF(it[0].toString().toFloat(), it[1].toString().toFloat(), it[0].toString().toFloat() + it[2].toString().toFloat(), it[1].toString().toFloat() + it[3].toString().toFloat()), mPaint)
            }
            canvas.drawRect(RectF(0f,0f, mBitmap!!.width.toFloat(), mBitmap!!.height.toFloat()), mPaint)
            newBoundsArray = mBounds
        }


        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            mCanvas = Canvas(mBitmap)
        }


    }
}



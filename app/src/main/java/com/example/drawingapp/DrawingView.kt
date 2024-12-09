package com.example.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath = CustomPath(Color.BLACK, 0f)
    private var mCanvasBitmap: Bitmap? = null
    private lateinit var mDrawPaint: Paint
    private lateinit var mCanvasPaint: Paint
    private var mBrushSize: Float = 0f
    private var color: Int = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = arrayListOf<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint().apply {
            color = this@DrawingView.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics)
        mDrawPaint.strokeWidth = mBrushSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
        mPaths.forEach { path ->
            mDrawPaint.strokeWidth = path.brushThickness
            mDrawPaint.color = path.color
            canvas.drawPath(path, mDrawPaint)
        }
        if (!mDrawPath.isEmpty) {
            mDrawPaint.strokeWidth = mDrawPath.brushThickness
            mDrawPaint.color = mDrawPath.color
            canvas.drawPath(mDrawPath, mDrawPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath = CustomPath(color, mBrushSize).apply {
                    reset()
                    moveTo(touchX, touchY)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawPaint.strokeWidth = mBrushSize
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path()
}

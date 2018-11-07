package com.whatnow.wtf

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import java.lang.IllegalArgumentException
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10

private const val TAG = "GLTest"

class MGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {
    private val renderer = MRenderer()

    init {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(MultiSampleConfigChooser())
        setRenderer(renderer)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
}

private class MultiSampleConfigChooser : GLSurfaceView.EGLConfigChooser {

    override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig {

        val value = intArrayOf(0)
        val returnedConfigs = arrayOfNulls<EGLConfig>(16)

        var minimalSpecs = intArrayOf(
            EGL10.EGL_RED_SIZE,
            8,
            EGL10.EGL_GREEN_SIZE,
            8,
            EGL10.EGL_BLUE_SIZE,
            8,
            EGL10.EGL_ALPHA_SIZE,
            8,
            EGL10.EGL_SAMPLE_BUFFERS,
            1,
            EGL10.EGL_SAMPLES,
            2,
            EGL10.EGL_NONE
        )

        if (!egl.eglChooseConfig(display, minimalSpecs, returnedConfigs, returnedConfigs.size, value)) {
            throw IllegalArgumentException("No config found")
        }

        val configCount = value[0]
        Log.d(TAG, "$configCount configs found")

        return returnedConfigs[0]!!
    }
}

private class MRenderer : GLSurfaceView.Renderer {

    override fun onDrawFrame(gl: GL10?) {
        // Redraw background color.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        checkGlError("glClear")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        checkGlError("glViewport")
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Disable dithering.
        GLES20.glDisable(GLES20.GL_DITHER)
        checkGlError("glDisable")

        // Set background frame color.
        GLES20.glClearColor(1f, 0f, 0f, 1f)
        checkGlError("glClearColor")
    }

    fun checkGlError(glOperation: String) {
        checkGlError(glOperation, 0)
    }

    fun checkGlError(glOperation: String, programId: Int) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            if (programId != 0) {
                throw RuntimeException(
                    "$glOperation: glError $error, programInfoLog: ${GLES20.glGetProgramInfoLog(
                        programId
                    )}"
                )
            } else {
                throw RuntimeException("$glOperation: glError $error")
            }
        }
    }
}
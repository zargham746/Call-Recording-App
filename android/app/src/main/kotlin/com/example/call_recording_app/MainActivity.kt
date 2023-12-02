package com.example.call_recording_app

// MainActivity.kt
import java.io.File
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : FlutterActivity() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var audioFile: String? = null

    private val channelName = "call_recorder"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelName).setMethodCallHandler { call, result ->
            when (call.method) {
                "startRecording" -> {
                    startRecording()
                    result.success(true)
                }
                "stopRecording" -> {
                    stopRecording()
                    result.success(true)
                }
                "getFiles" -> {
                    getFiles()    
                }
                else -> result.notImplemented()
            }
        }
    }

    fun getFiles(): List<String> {
        val directoryPath = "${android.os.Environment.getExternalStorageDirectory().absolutePath}/"
        val directory = File(directoryPath)
        val files = directory.listFiles { _, name -> name.endsWith(".3gp") }
    
        val filePaths = mutableListOf<String>()
        files?.forEach { file ->
            filePaths.add(file.absolutePath)
        }
    
        return filePaths
    }
    private fun startRecording() {
        if (isRecording) {
            stopRecording()
        } else {
            if (checkPermissions()) {
                startRecordingInternal()
            }
        }
    }

    private fun startRecordingInternal() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        audioFile = "${Environment.getExternalStorageDirectory().absolutePath}/$timeStamp.3gp"
        // val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        // audioFile = "${downloadsDirectory.absolutePath}/$timeStamp.3gp"
        mediaRecorder?.setOutputFile(audioFile)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true

            Handler().postDelayed({
                stopRecording()
            }, 10 * 1000) // Stop recording after 10 seconds (adjust as needed)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = ArrayList<String>()

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO)
            }

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (permissions.isNotEmpty()) {
                requestPermissions(permissions.toTypedArray(), 1)
                return false
            }
        }

        return true
    }
}

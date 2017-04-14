package com.thealeksandr.mediapicker

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.thealeksandr.mediapicker.views.CameraPreview
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Aleksandr Nikiforov on 2/15/17.
 */
class CameraFragment :Fragment() {


    //https://github.com/pikanji/CameraPreviewSample
    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var mPicture: PictureCallback? = null
    private val capture: Button? = null
    val switchCamera: Button? = null
    private val cameraPreview: LinearLayout? = null
    private var cameraFront = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val view = inflater!!.inflate(R.layout.fragment_camera, container, false)
        mPreview = view.findViewById(R.id.preview_view) as CameraPreview
        return view

    }

    private fun findFrontFacingCamera(): Int {
        var cameraId = -1
        // Search for the front facing camera
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0..numberOfCameras - 1) {
            val info = CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                cameraFront = true
                break
            }
        }
        return cameraId
    }


    private fun findBackFacingCamera(): Int {
        var cameraId = -1
        //Search for the back facing camera
        //get the number of cameras
        val numberOfCameras = Camera.getNumberOfCameras()
        //for every camera check
        for (i in 0..numberOfCameras - 1) {
            val info = CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                cameraFront = false
                break
            }
        }
        return cameraId
    }

    override fun onResume() {
        super.onResume()
        if (!hasCamera(activity)) {
            Toast.makeText(activity, "Sorry, your phone does not have a camera!",
                    Toast.LENGTH_LONG).show()
            activity.finish()
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() == 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa

                releaseCamera()
                chooseCamera()
            } else {
                Toast.makeText(activity, "Sorry, your phone has only one camera!",
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    fun chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            val cameraId = findBackFacingCamera()
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId)
                mPicture = getPictureCallback()
                mPreview!!.refreshCamera(mCamera!!)
            }
        } else {
            val cameraId = findFrontFacingCamera()
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId)
                mPicture = getPictureCallback()
                mPreview!!.refreshCamera(mCamera!!)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        //when on Pause, release camera in order to be used from other applications
        releaseCamera()
    }

    private fun getPictureCallback(): PictureCallback {
        val picture = PictureCallback { data, camera ->
            //make a new picture file
            val pictureFile = getOutputMediaFile() ?: return@PictureCallback

            try {
                //write the file
                val fos = FileOutputStream(pictureFile)
                fos.write(data)
                fos.close()
                val toast = Toast.makeText(activity, "Picture saved: " + pictureFile.name, Toast.LENGTH_LONG)
                toast.show()

            } catch (e: FileNotFoundException) {
            } catch (e: IOException) {
            }

            //refresh camera to continue preview
            mPreview!!.refreshCamera(mCamera!!)
        }
        return picture
    }

    var captrureListener: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View) {
            mCamera!!.takePicture(null, null, mPicture)
        }
    }

    //make picture and save to a folder
    private fun getOutputMediaFile(): File? {
        //make a new file directory inside the "sdcard" folder
        val mediaStorageDir = File("/sdcard/", "JCG Camera")

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        //take the current timeStamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
        //and make a media file:
        mediaFile = File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")

        return mediaFile
    }

    private fun hasCamera(context: Context): Boolean {
        //check if the device has camera
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera!!.release()
            mCamera = null
        }
    }


}
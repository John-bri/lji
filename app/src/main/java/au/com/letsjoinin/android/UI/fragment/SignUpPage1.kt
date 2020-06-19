package com.developers.viewpager


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import au.com.letsjoinin.android.BuildConfig
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.SignUpActivity
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.Compressor
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_sign_up.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpPage1 : Fragment() {
    // TODO: Rename and change types of parameters
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
    }

    private var param1: String? = null
    private var parentView: View? = null
    private var imageView: CircleImageView? = null
    private var mailID: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var layout_parent: LinearLayout? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    val REQUEST_CAMERA = 1
    val REQUEST_GALLERY = 2


    lateinit var currentPhotoPath: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.sign_up_page1, container, false)
        try {
            parentView = v
            mailID = v.findViewById(R.id.sign_up_tv_email)
            password = v.findViewById(R.id.sign_up_tv_password)
            confirmPassword = v.findViewById(R.id.sign_up_tv_confirm_pass)
            confirmPassword!!.setOnEditorActionListener(activity as SignUpActivity)
            val attach_layout: RelativeLayout = v.findViewById(R.id.attach_layout)
            imageView = v.findViewById(R.id.sign_up_avatar)
            layout_parent = v.findViewById(R.id.sign1_parent)
            pasword_info = v.findViewById(R.id.pasword_info)
            confirm_info = v.findViewById(R.id.confirm_info)
            pasword_info!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    CommonMethods.SnackBar(
                        activity!!.content_sign_up,
                        resources.getString(R.string.password_hint),
                        true
                    )


                }

            })


//            confirm_info!!.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//
//                    CommonMethods.SnackBar(
//                        activity!!.content_sign_up,
//                        resources.getString(R.string.cpassword_hint),
//                        true
//                    )
//
//                }
//            })
            (v.findViewById(R.id.gallery) as LinearLayout).setOnClickListener {
                attach_layout.setVisibility(View.GONE)
                try {
                    galleryImageIntent()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            (v.findViewById(R.id.remove) as LinearLayout).setOnClickListener {
                attach_layout.setVisibility(View.GONE)
                try {
                    attach_layout.setVisibility(View.GONE)
                    imageView!!.setImageResource(R.mipmap.ic_choose_profile_image)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            (v.findViewById(R.id.camera) as LinearLayout).setOnClickListener {
                attach_layout.setVisibility(View.GONE)
                try {
                    TakePictureIntent()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            imageView!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if (attach_layout.visibility == View.GONE) {
                        val hasAndroidPermissions = CommonMethods.hasPermissions(
                            activity!!,
                            *arrayOf<String>(
                                android.Manifest.permission.CAMERA,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )

                        if (hasAndroidPermissions) {
                            attach_layout.setVisibility(View.VISIBLE)
                        } else {
                            val intent = Intent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", activity!!.getPackageName(), null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    } else {
                        attach_layout.setVisibility(View.GONE)
                    }
                }
            })

            mailID!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    (activity as SignUpActivity).signUpData.email = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })

            password!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    (activity as SignUpActivity).signUpData.password = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })

            confirmPassword!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    (activity as SignUpActivity).signUpData.confirmPassword = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })



            mailID!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                }
            })
            password!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                }
            })

            confirmPassword!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                }
            })
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }
        return v
    }

    open fun Update() {

        Handler().postDelayed({
            //            showSequence(parentView!!)
        }, 700)


    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun showRectPrompt(view: View) {
        MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
            .setTarget(view)
            .setPrimaryText("")
            .setSecondaryText("")
            .setBackgroundColour(resources.getColor(R.color.transcolorPrimary))
            .setPromptBackground(RectanglePromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .show()


    }

    lateinit var tapTarget: MaterialTapTargetSequence
    fun showSequence(view: View) {
        tapTarget = MaterialTapTargetSequence()
            .addPrompt(
                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
                    .setTarget(mailID)
                    .setPrimaryText("Step 1")
                    .setSecondaryText("Enter Email-ID")
                    .setBackgroundColour(resources.getColor(R.color.transcolorPrimary1))
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .create(), 4000
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
                    .setTarget(password)
                    .setPrimaryText("Step 2")
                    .setSecondaryText("Enter Password")
                    .setBackgroundColour(resources.getColor(R.color.transcolorPrimary1))
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .create(), 4000
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
                    .setPrimaryText("Info")
                    .setSecondaryText(resources.getString(R.string.password_hint))
                    .setAnimationInterpolator(FastOutSlowInInterpolator())
                    .setMaxTextWidth(R.dimen.max_prompt_width)
                    .setTarget(pasword_info)
                    .setBackgroundColour(resources.getColor(R.color.transcolorPrimary1))
                    .create(), 8000
            )

            .addPrompt(
                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
                    .setTarget(confirmPassword)
                    .setPrimaryText("Step 3")
                    .setSecondaryText("Reenter the new Password")
                    .setBackgroundColour(resources.getColor(R.color.transcolorPrimary1))
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .create(), 4000
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
                    .setPrimaryText("Profile Picture")
                    .setSecondaryText("Pick or capture a picture to upload(optional)")
                    .setAnimationInterpolator(FastOutSlowInInterpolator())
                    .setMaxTextWidth(R.dimen.max_prompt_width)
                    .setTarget(imageView)
                    .setBackgroundColour(resources.getColor(R.color.transcolorPrimary1))
                    .create(), 4000
            )

            .addPrompt(
                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
                    .setTarget((activity as SignUpActivity).signup_btn)
                    .setPrimaryText("Next")
                    .setSecondaryText("Click Next to move second page")
                    .setBackgroundColour(resources.getColor(R.color.transcolorPrimary1))
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .create(), 4000
            )
            .show()
    }

    //
    private fun TakePictureIntent() {
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            activity as AppCompatActivity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                    }
                }
            }
        } catch (e: Exception) {

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity!!.getPackageName(), null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun startCropActivity(@NonNull uri: Uri) {
        var destinationFileName = "CropImage"
        val inputFileName = "SampleCropImage_Dup.jpg"
        destinationFileName += ".jpg"
        var uCrop = UCrop.of(
            uri,
            Uri.fromFile(File(activity!!.getCacheDir(), inputFileName)),
            Uri.fromFile(File(activity!!.getCacheDir(), destinationFileName))
        )
        uCrop = basisConfig(uCrop)
        uCrop = advancedConfig(uCrop)
        UCrop.CROP_PAGE = 3
        uCrop.start(activity!!)
    }

    private fun basisConfig(@NonNull uCrop: UCrop): UCrop {
        var uCrop = uCrop
        uCrop = uCrop.withAspectRatio(1f, 1f)
        return uCrop
    }

    private fun advancedConfig(@NonNull uCrop: UCrop): UCrop {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setToolbarColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
        options.setStatusBarColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
        options.setActiveWidgetColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
        options.setToolbarWidgetColor(ContextCompat.getColor(activity!!, R.color.white))
        options.setTitleColor(ContextCompat.getColor(activity!!, R.color.white))
        options.setTitleColor(ContextCompat.getColor(activity!!, R.color.white))

        return uCrop.withOptions(options)
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = imageView!!.width
        val targetH: Int = imageView!!.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView!!.setImageBitmap(bitmap)
        }
    }

    fun handleCropResult(@NonNull result: Intent) {

        try {
            val resultUri = UCrop.getOutput(result)
            if (resultUri != null) {

                object : AsyncTask<Void, Void, String>() {
                    private var result_thumbnail: Bitmap? = null

                    override fun doInBackground(vararg params: Void): String {
                        var msg = ""
                        try {
                            val file = File(resultUri.path!!)
                            val compressedImage = Compressor(activity)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .compressToFile(file)
                            result_thumbnail = BitmapFactory.decodeFile(compressedImage.getAbsolutePath())
                            result_thumbnail = CommonMethods.rotateImageIfRequired(activity,result_thumbnail,resultUri)
                            if (result_thumbnail != null) {
                                (activity as SignUpActivity).signUpData.avatar =
                                    CommonMethods.encodeTobase64(result_thumbnail)
                            }
                        } catch (ex: Exception) {
                            msg = "Error :" + ex.message
                        }

                        return msg
                    }

                    override fun onPostExecute(msg: String) {
                        imageView!!.setImageBitmap(result_thumbnail)
                        //                        File del = new File(Image_Path);
                        //                        if(!del.exists()) {
                        //                            del.delete();
                        //                        }
                    }
                }.execute(null, null, null)
            } else {
                Toast.makeText(activity, "Cannot retrive croped image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {

                val file = File(currentPhotoPath)

                startCropActivity(Uri.fromFile(file))
            } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {

                if (data != null) {
                    val contentURI = data!!.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
//                    val path = saveImage(bitmap)
//                        imageView!!.setImageBitmap(bitmap)
                        val uri: Uri = CommonMethods.getImageUri(activity!!, bitmap)
                        if (uri != null) {
                            startCropActivity(uri)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }
    }

    private var confirm_info: ImageView? = null
    private var pasword_info: ImageView? = null


    override fun onResume() {
        super.onResume()
//        if(tapTarget != null)
//        {
//            tapTarget.dismiss()
//        }
        Update()
    }

    private fun galleryImageIntent() {
        try {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(galleryIntent, REQUEST_GALLERY)
        } catch (e: Exception) {

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity!!.getPackageName(), null)
            intent.data = uri
            startActivity(intent)
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpPage1().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

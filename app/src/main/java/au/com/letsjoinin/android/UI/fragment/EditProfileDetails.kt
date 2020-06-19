package au.com.letsjoinin.android.UI.fragment

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import au.com.letsjoinin.android.BuildConfig
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.utils.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.gson.Gson
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.Holder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
class EditProfileDetails : Fragment() {

    private var change_password_layout: RelativeLayout? = null
    private var mPrefMgr: PreferenceManager? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var layout_parent: LinearLayout? = null
    private var avatar_Layout_loading: RelativeLayout? = null
    private var update_Layout_loading: RelativeLayout? = null
    var edit_profile_txt_age: EditText? = null
    var edit_profile_first_name: EditText? = null
    var edit_profile_last_name: EditText? = null
    var edit_profile_mobile_number: EditText? = null
    var edit_profile_email_id: TextView? = null
    var txt_edit_profile_user_name: TextView? = null
    var edit_profile_suburb: TextView? = null
    var txt_edit_profile_token_count: TextView? = null
    var txt_edit_profile_address: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    val genderMap = HashMap<Int, String>()
    val timezoneMap = HashMap<Int, String>()
    val TZ1 = ArrayList<String>()
    var edit_profile_img_avatar: CircleImageView? = null
    var edit_avatar_img: CircleImageView? = null
    var gender: Spinner? = null
    var countrySpin: Spinner? = null
    var timezone: Spinner? = null
    private var lv: ListView? = null
    private lateinit var builder: DialogPlus
    private var txt_search_suburb: EditText? = null
    private var close_dialog: TextView? = null
    private var txt_no_suburbs: TextView? = null
    private var SuburbSK: Long = 0
    internal var ListOfSuburb = ArrayList<SuburbList>()
    val REQUEST_CAMERA = 1
    val REQUEST_GALLERY = 2
    fun pxTodp(dip: Float): Int {
        val resources = resources
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        ).toInt()
                )
    }

    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )


    protected fun checkPermissions() {
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(activity!!, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(activity!!, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> for (index in permissions.indices.reversed()) {
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                    // exit the app if one permission is not granted
                    Toast.makeText(
                        activity, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG
                    ).show()
                    //                        finish();
                    return
                }
            }
        }// all permissions were granted
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.edit_details, container, false)
        try {
            val attach_layout: RelativeLayout = view.findViewById(R.id.attach_layout)

            checkPermissions()
            attach_layout.bringToFront()
            mPrefMgr = PreferenceManager.getInstance();
            AvatarPath = mPrefMgr!!.getString(AppConstant.AvatarPath, AppConstant.EMPTY).toString().trim()

            change_password_layout = view.findViewById(R.id.change_password_layout)
            val close_password: ImageView = view.findViewById(R.id.close_password)
            val old_password: EditText = view.findViewById(R.id.old_password)
            val new_password: EditText = view.findViewById(R.id.new_password)
            val new_password_confirm: EditText = view.findViewById(R.id.new_password_confirm)
            val img_search_suburb: ImageView = view.findViewById(R.id.img_search_suburb)
            val edit_password: TextView = view.findViewById(R.id.edit_password)
            val loginType = mPrefMgr!!.getString(AppConstant.LOGINTYPE, AppConstant.EMPTY).toString().trim()
            if (loginType.equals(AppConstant.LJI)) {
                edit_password.visibility = View.VISIBLE
            } else {
                edit_password.visibility = View.GONE
            }
            val txt_h_email: TextView = view.findViewById(R.id.txt_h_email)
            val txt_h_lname: TextView = view.findViewById(R.id.txt_h_lname)
            val txt_h_fname: TextView = view.findViewById(R.id.txt_h_fname)
            val txt_h_age: TextView = view.findViewById(R.id.txt_h_age)
            val pasword_info: ImageView = view.findViewById(R.id.pasword_info)
            val confirm_info: ImageView = view.findViewById(R.id.confirm_info)

            val txt_h_year: TextView = view.findViewById(R.id.txt_h_year)
            val txt_h_mobile: TextView = view.findViewById(R.id.txt_h_mobile)
            val txt_h_sub: TextView = view.findViewById(R.id.txt_h_sub)
            val txt_h_country: TextView = view.findViewById(R.id.txt_h_country)
            val txt_h_zone: TextView = view.findViewById(R.id.txt_h_zone)
            val save_btn: TextView = view.findViewById(R.id.edit_profile_save_btn)
            val change_submit: Button = view.findViewById(R.id.change_submit)
            edit_profile_txt_age = view.findViewById(R.id.edit_profile_txt_age)
            edit_profile_first_name = view.findViewById(R.id.edit_profile_first_name)
            edit_profile_last_name = view.findViewById(R.id.edit_profile_last_name)
            edit_profile_mobile_number = view.findViewById(R.id.edit_profile_mobile_number)
            edit_profile_email_id = view.findViewById(R.id.edit_profile_email_id)
            edit_profile_suburb = view.findViewById(R.id.edit_profile_suburb)
            txt_edit_profile_user_name = view.findViewById(R.id.txt_edit_profile_user_name)
            txt_edit_profile_token_count = view.findViewById(R.id.txt_edit_profile_token_count)
            txt_edit_profile_address = view.findViewById(R.id.txt_edit_profile_address)
            layout_parent = view.findViewById(R.id.edit_parent_layout)
            update_Layout_loading = view.findViewById(R.id.update_Layout_loading)
            update_Layout_loading!!.bringToFront()
            avatar_Layout_loading = view.findViewById(R.id.avatar_Layout_loading)
            avatar_Layout_loading!!.visibility = View.GONE
            avatar_Layout_loading!!.bringToFront()
            pasword_info!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    CommonMethods.SnackBar(layout_parent, resources.getString(R.string.password_hint), true)


                }

            })
//            confirm_info!!.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//
//                    CommonMethods.SnackBar(layout_parent, resources.getString(R.string.cpassword_hint),true)
//
//                }
//
//            })
            val gdpr_switch = view.findViewById<View>(R.id.edit_profile_gdpr_switch) as ViewSwitch
            val ss = mPrefMgr!!.getString(AppConstant.GDPR, AppConstant.EMPTY).toString().trim()
            if (ss.equals("N")) {
                gdpr_switch.setActive(true)
            } else {
                gdpr_switch.setActive(false)
            }

            gdpr_switch.setOnStatusChangeListener(object : ViewSwitch.SwitchStatusListner {
                override fun onSwitchStatus(status: Int) {
                    if (status == 0) {
                        GDPR = "Y"
                    } else {
                        GDPR = "N"
                    }
                    Log.e("status", status.toString() + "")
                }
            })
            txt_h_lname.bringToFront()
            txt_h_fname.bringToFront()
            txt_h_email.bringToFront()
            txt_h_age.bringToFront()
            txt_h_year.bringToFront()
            txt_h_mobile.bringToFront()
            txt_h_sub.bringToFront()
            txt_h_country.bringToFront()
            txt_h_zone.bringToFront()
            val sign_up_tv_year: EditText = view.findViewById(R.id.edit_profile_txt_age)
            val sign_up_tv_mobile_number: EditText = view.findViewById(R.id.edit_profile_mobile_number)
            gender = view.findViewById(R.id.edit_profile_gender_input) as Spinner
            countrySpin = view.findViewById(R.id.edit_profile_country_input) as Spinner
            edit_profile_img_avatar = view.findViewById(R.id.edit_profile_img_avatar) as CircleImageView
            edit_avatar_img = view.findViewById(R.id.edit_avatar_img) as CircleImageView
            if (!TextUtils.isEmpty(AvatarPath)) {
                Picasso.with(context)
                    .load(AvatarPath)
                    .error(R.drawable.lji_default_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(edit_profile_img_avatar)
            }
            timezone = view.findViewById(R.id.edit_profile_time_input) as Spinner
            val countries = ArrayList<String>()
            countries.add("Country")
            countries.add("Australia")
            val adapter = ArrayAdapter(activity!!, R.layout.signup_spinner_item, countries)
            countrySpin!!.setAdapter(adapter as SpinnerAdapter?)

            genderMap[0] = "M"
            genderMap[1] = "M"
            genderMap[2] = "F"
            genderMap[3] = "O"
            genderMap[4] = "TG"
            genderMap[5] = "PNS"

            timezoneMap[0] = "AWST"
            timezoneMap[1] = "AWST"
            timezoneMap[2] = "ACWST"
            timezoneMap[3] = "ACST"
            timezoneMap[4] = "AEST"
            timezoneMap[5] = "ACDT"
            timezoneMap[6] = "AEDT"
            timezoneMap[7] = "LHDT"



            TZ1.add("Time Zone")
            TZ1.add("Australian Western Standard Time")
            TZ1.add("Australian Central Western Standard Time")
            TZ1.add("Australian Central Standard Time")
            TZ1.add("Australian Eastern Standard Time")
            TZ1.add("Australian Central Daylight Time")
            TZ1.add("Australian Eastern Daylight Time")
            TZ1.add("Lord Howe Daylight Time")
            val adapter1 = ArrayAdapter(this!!.activity!!, R.layout.signup_spinner_item, TZ1)
            timezone!!.setAdapter(adapter1)


            countrySpin!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    val textView = adapterView.getChildAt(0) as TextView
                    if (i == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                    }
                    (adapterView.getChildAt(0) as TextView).textSize = 14f
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            })
            countrySpin!!.setSelection(1)
            change_password_layout!!.visibility = View.VISIBLE
            builder = DialogPlus.newDialog(activity!!).setContentHolder(object : Holder {
                private lateinit var nview: View

                override fun addHeader(view: View) {
                }

                override fun addHeader(view: View, fixed: Boolean) {
                }

                override fun addFooter(view: View) {
                }

                override fun addFooter(view: View, fixed: Boolean) {
                }

                override fun setBackgroundResource(colorResource: Int) {
                }

                override fun getView(inflater: LayoutInflater, parent: ViewGroup?): View {
                    val view = inflater.inflate(R.layout.suburb_dialog_content, parent, false)
                    nview = view.findViewById(R.id.mainparent) as View
                    close_dialog = view.findViewById<View>(R.id.close_dialog) as TextView
                    txt_no_suburbs = view.findViewById<View>(R.id.txt_no_suburbs) as TextView
                    txt_search_suburb = view.findViewById(R.id.txt_search_suburb) as EditText
                    lv = view.findViewById(R.id.list_view) as ListView

                    txt_search_suburb!!.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                        }

                        override fun afterTextChanged(s: Editable) {

                            try {
                                if(CommonMethods.isNetworkAvailable(context)) {
                                    txt_no_suburbs!!.visibility = View.GONE
                                    if (!TextUtils.isEmpty(s.toString().trim())) {
                                        SearchSuburb(s.toString())
                                    }
                                }else{
                                    if(layout_parent != null) {
                                        CommonMethods.SnackBar(layout_parent,context!!.getString(R.string.network_error),false)
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    })
                    return view
                }

                override fun setOnKeyListener(keyListener: View.OnKeyListener?) {
                }

                override fun getInflatedView(): View {
                    return nview;
                }

                override fun getHeader(): View? {
                    return null;
                }

                override fun getFooter(): View? {
                    return null;
                }
            })
                .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)  // or any custom width ie: 300
                .setContentHeight(pxTodp(350f))
                .setInAnimation(R.anim.slide_in_top)
                .setGravity(Gravity.TOP)
                .setCancelable(false)
                .create();

            close_dialog!!.setOnClickListener(View.OnClickListener {

                CommonMethods.hideKeyboardFrom(activity, txt_search_suburb!!)
                builder.dismiss()
            })


            change_submit!!.setOnClickListener(View.OnClickListener {

                if(CommonMethods.isNetworkAvailable(context)) {
                    val view = activity!!.currentFocus
                    view?.let { v ->
                        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
                    }

                    if (TextUtils.isEmpty(old_password!!.text.toString())) {
                        if (old_password != null) {
                            old_password!!.requestFocus()
                        }
                        CommonMethods.SnackBar(layout_parent, "Please enter Old Password", false)
                        return@OnClickListener
                    }
                    if (old_password.text.toString().length < 6) {
                        new_password.requestFocus()
                        CommonMethods.SnackBar(login_parent, "Password should have minimum 6 characters", false)
                        return@OnClickListener
                    }

                    if (TextUtils.isEmpty(new_password!!.text.toString())) {
                        if (new_password != null) {
                            new_password!!.requestFocus()
                        }
                        CommonMethods.SnackBar(layout_parent, "Please enter New Password", false)
                        return@OnClickListener
                    }
                    if (new_password.text.toString().length < 6) {
                        new_password.requestFocus()
                        CommonMethods.SnackBar(login_parent, "Password should have minimum 6 characters", false)
                        return@OnClickListener
                    }
                    if (!CommonMethods.isValidPassword(new_password!!.text.toString())) {
                        if (new_password != null) {
                            new_password!!.requestFocus()
                        }
                        CommonMethods.SnackBar(layout_parent, "Please enter valid New Password", false)
                        return@OnClickListener
                    }
                    if (TextUtils.isEmpty(new_password_confirm!!.text.toString())) {
                        if (new_password_confirm != null) {
                            new_password_confirm!!.requestFocus()
                        }
                        CommonMethods.SnackBar(layout_parent, "Please enter Confirm Password", false)
                        return@OnClickListener
                    }
                    if (!new_password!!.text.toString().equals(new_password_confirm!!.text.toString())) {
                        if (new_password_confirm != null) {
                            new_password_confirm!!.setText("")
                        }
                        if (new_password != null) {
                            new_password!!.setText("")
                            new_password!!.requestFocus()
                        }
                        CommonMethods.SnackBar(layout_parent, "Password and Confirm Password doesn’t match", false)
                        return@OnClickListener
                    }
                    var input: ChangePasswordReq = ChangePasswordReq()
                    input.NewPassword = new_password.text.toString().trim()
                    input.OldPassword = old_password.text.toString().trim()
                    input.UserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                    input.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
                    ChangeUserPassword(input)
                }else{
                    if(layout_parent != null) {
                        CommonMethods.SnackBar(layout_parent,context!!.getString(R.string.network_error),false)
                    }
                }

            })


            edit_avatar_img!!.setOnClickListener(View.OnClickListener {

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
            })
            var layout_gallery: LinearLayout = view.findViewById(R.id.gallery)
            var layout_camera: LinearLayout = view.findViewById(R.id.camera)
            var layout_remove: LinearLayout = view.findViewById(R.id.remove)
            layout_gallery.setOnClickListener {
                attach_layout.setVisibility(View.GONE)
                try {
                    avatar_Layout_loading!!.visibility = View.VISIBLE
                    galleryImageIntent()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            layout_camera.setOnClickListener {
                attach_layout.setVisibility(View.GONE)
                try {
                    avatar_Layout_loading!!.visibility = View.VISIBLE
                    TakePictureIntent()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            attach_layout.bringToFront()
            attach_layout.setOnClickListener {
                attach_layout.setVisibility(View.GONE)
            }


            layout_remove.setOnClickListener {
                attach_layout.setVisibility(View.GONE)
                AvatarPath = null
                Picasso.with(context)
                    .load(R.drawable.lji_default_img)
                    .error(R.drawable.lji_default_img)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(edit_profile_img_avatar)
            }

            edit_profile_suburb!!.setOnClickListener(View.OnClickListener {

                val inputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                txt_search_suburb!!.setText("")
                txt_search_suburb!!.requestFocus()
                builder.show()
            })


            img_search_suburb!!.setOnClickListener(View.OnClickListener {

                val inputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                txt_search_suburb!!.setText("")
                txt_search_suburb!!.requestFocus()
                builder.show()
            })
            timezone!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    val textView = adapterView.getChildAt(0) as TextView
                    if (i == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                    }
                    (adapterView.getChildAt(0) as TextView).textSize = 14f
                    TimeZone = timezoneMap[i]
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            })
            gender!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    val textView = adapterView.getChildAt(0) as TextView
                    if (i == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                    }
                    (adapterView.getChildAt(0) as TextView).textSize = 14f
                    GenderCode = genderMap[i]
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            })
            save_btn.setOnClickListener(View.OnClickListener {
                val view = activity!!.currentFocus
                view?.let { v ->
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
                }


                if (TextUtils.isEmpty(edit_profile_first_name!!.text.toString().trim())) {
                    if (edit_profile_first_name != null) {
                        edit_profile_first_name!!.requestFocus()
                    }
                    CommonMethods.SnackBar(layout_parent, "Please enter First Name", false)
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(edit_profile_last_name!!.text.toString().trim())) {
                    if (edit_profile_last_name != null) {
                        edit_profile_last_name!!.requestFocus()
                    }
                    CommonMethods.SnackBar(layout_parent, "Please enter Last Name", false)
                    return@OnClickListener
                }

                if (gender!!.getSelectedItem().toString().trim().equals("Gender")) {
                    CommonMethods.SnackBar(layout_parent, "Please choose Gender", false)

                    return@OnClickListener
                }
                if (TextUtils.isEmpty(edit_profile_txt_age!!.text.toString())) {

                    CommonMethods.SnackBar(layout_parent, "Please enter Year of Birth", false)
                    if (edit_profile_txt_age != null) {
                        edit_profile_txt_age!!.requestFocus()
                    }
                    return@OnClickListener
                }
                if (edit_profile_txt_age!!.text.toString().length != 4) {

                    CommonMethods.SnackBar(layout_parent, "Year of Birth must be 4 digits", false)
                    if (edit_profile_txt_age != null) {
                        edit_profile_txt_age!!.requestFocus()
                    }
                    return@OnClickListener
                }
                val year = Calendar.getInstance().get(Calendar.YEAR) - 18
                if (edit_profile_txt_age!!.text.toString().toInt() >= year) {
                    if (edit_profile_txt_age != null) {
                        edit_profile_txt_age!!.requestFocus()
                    }
                    CommonMethods.SnackBar(layout_parent, "Age must be greater or equal to 18", false)
                    return@OnClickListener
                }


                if (TextUtils.isEmpty(edit_profile_mobile_number!!.text.toString())) {
                    if (edit_profile_mobile_number != null) {
                        edit_profile_mobile_number!!.requestFocus()
                    }
                    CommonMethods.SnackBar(layout_parent, "Please enter Mobile No.", false)
                    return@OnClickListener
                }

                if (countrySpin!!.getSelectedItem().toString().trim().equals("Country", true)) {
                    CommonMethods.SnackBar(layout_parent, "Please choose Country", false)
                    return@OnClickListener
                }
                if (timezone!!.getSelectedItem().toString().trim().equals("Time zone", true)) {
                    CommonMethods.SnackBar(layout_parent, "Please choose  Timezone", false)
                    return@OnClickListener
                }






                updateprofileData()
            })
            change_password_layout!!.isClickable = false
            YoYo.with(Techniques.SlideOutDown)
                .duration(100)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(AccelerateDecelerateInterpolator())
                .withListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        change_password_layout!!.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
                .playOn(change_password_layout)
            edit_password.setOnClickListener(View.OnClickListener {


                YoYo.with(Techniques.SlideInUp)
                    .duration(700)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(AccelerateDecelerateInterpolator())
                    .withListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            change_password_layout!!.isClickable = true
                            change_password_layout!!.setBackgroundColor(Color.parseColor("#79CCCCCC"))
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            change_password_layout!!.visibility = View.VISIBLE
                        }

                    })
                    .playOn(change_password_layout)

            })
            close_password.setOnClickListener(View.OnClickListener {

                YoYo.with(Techniques.SlideOutDown)
                    .duration(600)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(AccelerateDecelerateInterpolator())
                    .withListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            change_password_layout!!.visibility = View.GONE
                            change_password_layout!!.isClickable = false

                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            change_password_layout!!.setBackgroundColor(Color.parseColor("#00000000"))
                        }

                    })
                    .playOn(change_password_layout)
            })

        } catch (e: Exception) {
            e.printStackTrace()
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
            }
        }
        return view
    }

    private fun updateprofileData() {


        update_Layout_loading!!.visibility = View.VISIBLE
        update_Layout_loading!!.bringToFront()
        var data: EditUserData = EditUserData()
        data.AvatarPath = AvatarPath
        data.FirstName = edit_profile_first_name!!.text.toString()
        data.LastName = edit_profile_last_name!!.text.toString()
        data.Country = "AUS"
        data.PhoneNo = edit_profile_mobile_number!!.text.toString()
        data.GDPR = GDPR
        data.YearOfBirth = edit_profile_txt_age!!.text.toString()
        data.GenderCode = GenderCode
        data.TimeZone = TimeZone
        data.UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
        val suburb: SignUpSuburb = SignUpSuburb()
        suburb.Code = SuburbCode
        suburb.Name = SuburbName
        data.Suburb = suburb


        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.UpdateUser(data)
        call.enqueue(object : Callback<ServerMessageData> {
            override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                update_Layout_loading!!.visibility = View.GONE
                if (activity != null) {
                    if (response.body() != null) {
                        val res: ServerMessageData = response.body()
                        if (layout_parent != null) {
                            if (TextUtils.isEmpty(AvatarPath)) {
                                mPrefMgr!!.setString(AppConstant.AvatarPath, AvatarPath)
                            }
                            val g = Gson()
                            mPrefMgr!!.setString(AppConstant.TimeZone, TimeZone)
                            mPrefMgr!!.setString(AppConstant.GenderCode, GenderCode)
//                            mPrefMgr!!.setString(AppConstant.Country, res.UserDoc.Country)
                            mPrefMgr!!.setString(AppConstant.Suburb, g.toJson(suburb))
                            mPrefMgr!!.setString(AppConstant.FirstName, edit_profile_first_name!!.text.toString())
                            mPrefMgr!!.setString(AppConstant.LastName, edit_profile_last_name!!.text.toString())
                            mPrefMgr!!.setString(AppConstant.YearOfBirth, edit_profile_txt_age!!.text.toString())
                            mPrefMgr!!.setString(AppConstant.PhoneNo, edit_profile_mobile_number!!.text.toString())
                            mPrefMgr!!.setString(AppConstant.GDPR, GDPR)
                            CommonMethods.SnackBar(layout_parent, res!!.DisplayMsg, false)
                            Handler().postDelayed({
                                val fragment = ProfileDetails()
                                if (fragmentManager != null) {
                                    val transaction = fragmentManager!!.beginTransaction()
                                    transaction.replace(R.id.container_fragment, fragment)
                                    transaction.addToBackStack(null)
                                    transaction.commit()
                                }
                            }, 1500)

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                update_Layout_loading!!.visibility = View.GONE
                CommonMethods.SnackBar(layout_parent, "Something went wrong.", false)
//                if (profile_shimmer_view_container != null) {
//                    profile_shimmer_view_container!!.stopShimmerAnimation()
//                    profile_shimmer_view_container!!.visibility = View.GONE
//                }
            }
        })
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

    private fun ChangeUserPassword(input: ChangePasswordReq) {
        try {
            update_Layout_loading!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)

            val call = service.ChangeUserPassword(input)
            call.enqueue(object : Callback<ServerMessageData> {
                override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                    var res = response.body();
                    update_Layout_loading!!.visibility = View.GONE
                    if (res != null) {

                        if (res!!.Status.equals(AppConstant.SUCCESS)) {
                            CommonMethods.SnackBar(layout_parent, res!!.DisplayMsg, false)
                            mPrefMgr!!.setString(AppConstant.PASSWORD, input.NewPassword)
                            YoYo.with(Techniques.SlideOutDown)
                                .duration(600)
                                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                                .interpolate(AccelerateDecelerateInterpolator())
                                .withListener(object : Animator.AnimatorListener {
                                    override fun onAnimationRepeat(animation: Animator?) {
                                    }

                                    override fun onAnimationEnd(animation: Animator?) {
                                        change_password_layout!!.visibility = View.GONE
                                        change_password_layout!!.isClickable = false

                                    }

                                    override fun onAnimationCancel(animation: Animator?) {
                                    }

                                    override fun onAnimationStart(animation: Animator?) {
                                        change_password_layout!!.setBackgroundColor(Color.parseColor("#00000000"))
                                    }

                                })
                                .playOn(change_password_layout)
                        } else {
                            CommonMethods.SnackBar(layout_parent, res!!.DisplayMsg, true)
                        }
                    } else {
                        CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                    update_Layout_loading!!.visibility = View.GONE
                    CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            update_Layout_loading!!.visibility = View.GONE
            CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
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
                            if (result_thumbnail != null) {

                                AvatarPath = CommonMethods.encodeTobase64(result_thumbnail)
                            }
                        } catch (ex: Exception) {
                            msg = "Error :" + ex.message
                        }

                        return msg
                    }

                    override fun onPostExecute(msg: String) {
                        avatar_Layout_loading!!.visibility = View.GONE
                        edit_profile_img_avatar!!.setImageBitmap(result_thumbnail)
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
    fun handleCropError() {

        avatar_Layout_loading!!.visibility = View.GONE

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

                val file = File(currentPhotoPath)

                startCropActivity(Uri.fromFile(file))
            } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    val contentURI = data!!.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
//                    val path = saveImage(bitmap)
                        val uri: Uri = CommonMethods.getImageUri(activity!!, bitmap)
                        if (uri != null) {
                            startCropActivity(uri)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            } else {
                if (avatar_Layout_loading != null)
                    avatar_Layout_loading!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }
    }


    lateinit var currentPhotoPath: String

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

    private fun getprofileData() {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://letsjoinin.blob.core.windows.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val UserID = mPrefMgr!!.getString(AppConstant.USERID, AppConstant.EMPTY)
        val call = service.getProfileData(UserID)
        call.enqueue(object : Callback<ProfileDataResponse> {
            override fun onResponse(call: Call<ProfileDataResponse>, response: Response<ProfileDataResponse>) {
                if (activity != null) {
                    if (response.body() != null) {
//                        if (profile_shimmer_view_container != null) {
//                            profile_shimmer_view_container!!.stopShimmerAnimation()
//                            profile_shimmer_view_container!!.visibility = View.GONE
//                        }
                        val profileDataResponse: ProfileDataResponse = response.body();

                        if (profileDataResponse != null) {

                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileDataResponse>, t: Throwable) {
//                if (profile_shimmer_view_container != null) {
//                    profile_shimmer_view_container!!.stopShimmerAnimation()
//                    profile_shimmer_view_container!!.visibility = View.GONE
//                }
            }
        })
    }


    private fun SearchSuburb(str: String) {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://ljiservicesdev.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.SearchSuburb(str)
        call.enqueue(object : Callback<SearchSuburbRes> {
            override fun onResponse(call: Call<SearchSuburbRes>, response: Response<SearchSuburbRes>) {

                val suburbRes: SearchSuburbRes = response.body();
                lv!!.setAdapter(null)
                if (suburbRes.ServerStatus.Status.equals(AppConstant.SUCCESS)) {
                    if (suburbRes.ListOfSuburb != null) {
                        ListOfSuburb = suburbRes.ListOfSuburb
                        val subUrbArr = arrayOfNulls<String?>(suburbRes.ListOfSuburb.size)
                        for (i in 0 until suburbRes.ListOfSuburb.size) {
                            subUrbArr[i] = suburbRes.ListOfSuburb.get(i).PostalCodeLocality
                        }
                        txt_no_suburbs!!.visibility = View.GONE
                        if (ListOfSuburb!!.size == 0) {
                            lv!!.visibility = View.GONE
                            txt_no_suburbs!!.visibility = View.VISIBLE
                        } else {
                            lv!!.visibility = View.VISIBLE
                        }
                        // Adding items to listview
                        var arrayAdapter: ArrayAdapter<String?> =
                            ArrayAdapter(activity!!, R.layout.suburb_list_item, R.id.tv_suburb_name, subUrbArr)
                        lv!!.setAdapter(arrayAdapter)

                        lv!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                            SuburbSK = ListOfSuburb.get(position).SuburbSK
                            edit_profile_suburb!!.setText(ListOfSuburb.get(position).PostalCodeLocality)
                            SuburbCode = ListOfSuburb.get(position).PostalCode
                            SuburbName = ListOfSuburb.get(position).Locality
                            CommonMethods.hideKeyboardFrom(activity, txt_search_suburb!!)
                            builder.dismiss()
                        })
                    } else {
                        lv!!.visibility = View.GONE
                        txt_no_suburbs!!.visibility = View.VISIBLE
                    }
                } else {
                }

            }

            override fun onFailure(call: Call<SearchSuburbRes>, t: Throwable) {

            }
        })
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }


    private var UserPoints: String? = null
    private var EmailID: String? = null
    private var YearOfBirth: String? = null
    private var PhoneNo: String? = null
    private var AvatarPath: String? = null
    private var Suburb: String? = null
    private var SuburbCode: String? = null
    private var SuburbName: String? = null
    private var GDPR: String? = null
    private var GenderCode: String? = null
    private var TimeZone: String? = null
    override fun onResume() {
        super.onResume()
        if (activity != null) {
            if ((activity as NavigationActivity).menu_title != null) {
                (activity as NavigationActivity).menu_title.setText("EditProfile")
            }
            if ((activity as NavigationActivity).img_back_arrow != null) {
                (activity as NavigationActivity).img_back_arrow.visibility = View.VISIBLE
            }

            val username = mPrefMgr!!.getString(
                AppConstant.FirstName,
                AppConstant.EMPTY
            ) + " " + mPrefMgr!!.getString(AppConstant.LastName, AppConstant.EMPTY)
            UserPoints = mPrefMgr!!.getString(AppConstant.UserPoints, AppConstant.EMPTY)
            EmailID = mPrefMgr!!.getString(AppConstant.SMEmailID, AppConstant.EMPTY)
            YearOfBirth = mPrefMgr!!.getString(AppConstant.YearOfBirth, AppConstant.EMPTY)
            PhoneNo = mPrefMgr!!.getString(AppConstant.PhoneNo, AppConstant.EMPTY)

            Suburb = mPrefMgr!!.getString(AppConstant.Suburb, AppConstant.EMPTY)
            TimeZone = mPrefMgr!!.getString(AppConstant.TimeZone, AppConstant.EMPTY)
            GenderCode = mPrefMgr!!.getString(AppConstant.GenderCode, AppConstant.EMPTY)
            GDPR = mPrefMgr!!.getString(AppConstant.GDPR, AppConstant.EMPTY)
            val g = Gson()
            var suburb: SignUpSuburb? =
                g.fromJson(Suburb, SignUpSuburb::class.java)



            txt_edit_profile_user_name!!.setText(username)
            edit_profile_first_name!!.setText(
                mPrefMgr!!.getString(
                    AppConstant.FirstName,
                    AppConstant.EMPTY
                )
            )

            SuburbCode = suburb!!.Code
            SuburbName = suburb!!.Name

            val GenderCodeIndex = CommonMethods.getKeyFromValue(genderMap, GenderCode!!)
            val TimeZoneCodeIndex = CommonMethods.getKeyFromValue(timezoneMap, TimeZone!!)
            gender!!.setSelection(1)
            if (GenderCodeIndex != null) {
                if (!GenderCode.equals("M")) {
                    gender!!.setSelection(GenderCodeIndex as Int)
                }
            }
            timezone!!.setSelection(1)
            if (TimeZoneCodeIndex != null) {
                if (!TimeZone.equals("AWST")) {
                    timezone!!.setSelection(TimeZoneCodeIndex as Int)
                }
            }
            edit_profile_last_name!!.setText(mPrefMgr!!.getString(AppConstant.LastName, AppConstant.EMPTY))
            txt_edit_profile_token_count!!.setText(UserPoints + " tokens")
            edit_profile_email_id!!.setText(EmailID)
            edit_profile_txt_age!!.setText(YearOfBirth)
            edit_profile_mobile_number!!.setText(PhoneNo)
            if (suburb != null) {
                edit_profile_suburb!!.setText(suburb!!.Code + " - " + suburb!!.Name)
                txt_edit_profile_address!!.setText(suburb!!.Code + " - " + suburb!!.Name)
            }

            getprofileData()
        }
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
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

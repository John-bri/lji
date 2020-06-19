package au.com.letsjoinin.android.UI.activity

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.utils.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.developers.viewpager.SignUpPage0
import com.developers.viewpager.SignUpPage1
import com.developers.viewpager.SignUpPage2
import com.developers.viewpager.SignUpPage3
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


/*    LocationListener {
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onLocationChanged(p0: Location?) {
        LocationAddress.getAddressFromLocation(
            p0!!.latitude, p0!!.longitude,
            applicationContext, GeocoderHandler()
        )
    }

     lateinit var locationManager: LocationManager
     var locationListener: android.location.LocationListener? = null
    private var Layout_loading: RelativeLayout? = null
    var frag2: SignUpPage2? = null
    var frag1: SignUpPage1? = null
    var frag3: SignUpPage3? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_sign_up)
            val loginType = intent.getStringExtra("login_type")
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return
                }
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                this
            )
*/

class SignUpActivity : AppCompatActivity(), TextView.OnEditorActionListener,
    OnKeyboardVisibilityListener {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null
    var EnterpriseInfo: EnterpriseInfoClass? = EnterpriseInfoClass()
    private var mLatitudeLabel: String? = null
    private var mLongitudeLabel: String? = null
    private var Layout_loading: RelativeLayout? = null
    var frag0: SignUpPage0? = null
    var frag2: SignUpPage2? = null
    var frag1: SignUpPage1? = null
    var frag3: SignUpPage3? = null
    var bottomSheetBehavior: BottomSheetBehavior<LinearLayout?>? = null
    internal var layoutBottomSheet: LinearLayout? = null
    var add_spinner: RelativeLayout? = null
    var apply: Button? = null

    var selected_list_parent: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_sign_up)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            val loginType = intent.getStringExtra("login_type")
            Layout_loading = findViewById(R.id.Layout_loading) as RelativeLayout
            progress_lyt!!.visibility = View.GONE
            signup_btn = findViewById(R.id.signup_btn)
            img_back_page = findViewById(R.id.img_back_page)
            content_sign_up = findViewById(R.id.content_sign_up)
            img_back_page.visibility = View.GONE
            setKeyboardVisibilityListener(this);
            checkPermissions()
            val progress_lyt = findViewById(R.id.progress_lyt) as RelativeLayout

            layoutBottomSheet = findViewById(R.id.bottom_sheet)
            bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
            add_spinner = layoutBottomSheet!!.findViewById(R.id.add_spinner)
            selected_list_parent =
                layoutBottomSheet!!.findViewById<View>(R.id.selected_list_parent) as LinearLayout
            apply = layoutBottomSheet!!.findViewById<View>(R.id.apply) as Button


            bottomSheetBehavior!!.setBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // React to dragging events
                }
            })

            adapterPage = MyAdapter(supportFragmentManager)
            view_pager.adapter = adapterPage
            view_pager.CustomViewPagerData(this)
            view_pager.setPagingEnabled(false)
            view_pager.setCurrentItem(0, true)
            if (loginType != null && (loginType.equals(AppConstant.GPLUS) || loginType.equals(
                    AppConstant.FB
                ))
            ) {
                view_pager.setCurrentItem(1, true)

            }

            view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {

                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

                }

                override fun onPageSelected(p0: Int) {

                    signup_btn.setText("Next")
                    if (p0 == 0) {
                        frag1!!.Update()
                        img_back_page.visibility = View.GONE
                    } else {
                        frag2!!.Update()
                        if (loginType.equals("lji", true)) {
                            img_back_page.visibility = View.VISIBLE
                        } else {
                            img_back_page.visibility = View.GONE
                        }
                    }
                    if (p0 == 3) {
                        frag3!!.Update()
                        img_back_page.visibility = View.VISIBLE
                        signup_btn.setText("Sign Up")
                    }
                }
            })

            signup_btn.setOnClickListener(View.OnClickListener {

                try {
                    val view = this.currentFocus
                    view?.let { v ->
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
                    }
                    val pos = view_pager.currentItem
                    var v = view_pager.getFocusedChild()
                    if (v == null) {
                        v = view_pager.getChildAt(pos)
                    }
                    if (pos == 1) {
                        var mailID: EditText? = v.findViewById(R.id.sign_up_tv_email)
                        var password: EditText? = v.findViewById(R.id.sign_up_tv_password)
                        var confirmPassword: EditText? =
                            v.findViewById(R.id.sign_up_tv_confirm_pass)
                        if (TextUtils.isEmpty(signUpData.email)) {
                            if (mailID != null) {
                                mailID!!.requestFocus()
                            }
                            CommonMethods.SnackBar(content_sign_up, "Please enter Email Id", false)
                            return@OnClickListener
                        }
                        if (!CommonMethods.isValidMailId(signUpData.email)) {
                            if (mailID != null) {
                                mailID!!.requestFocus()
                            }
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please enter valid Email Id",
                                false
                            )
                            return@OnClickListener
                        }
                        if (TextUtils.isEmpty(signUpData.password)) {
                            if (password != null) {
                                password!!.requestFocus()
                            }
                            CommonMethods.SnackBar(content_sign_up, "Please enter Password", false)
                            return@OnClickListener
                        }
                        if (!CommonMethods.isValidPassword(signUpData.password)) {
                            if (password != null) {
                                password!!.requestFocus()
                            }
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please enter valid Password.",
                                false
                            )
                            return@OnClickListener
                        }
                        if (TextUtils.isEmpty(signUpData.confirmPassword)) {
                            if (confirmPassword != null) {
                                confirmPassword!!.requestFocus()
                            }
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please enter Confirm Password",
                                false
                            )
                            return@OnClickListener
                        }
//                        if (!CommonMethods.isValidPassword(signUpData.confirmPassword)) {
//                            if (confirmPassword != null) {
//                                confirmPassword!!.requestFocus()
//                            }
//                            CommonMethods.SnackBar(content_sign_up, "Please enter valid Confirm Password", false)
//                            return@OnClickListener
//                        }
                        if (!signUpData.password.equals(signUpData.confirmPassword)) {
                            if (confirmPassword != null) {
                                confirmPassword!!.requestFocus()
                            }
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Password and Confirm Password doesn’t match",
                                false
                            )
                            return@OnClickListener
                        }

                        view_pager.setCurrentItem(2);
                    } else if (pos == 2) {
                        val sign_up_tv_first_name: EditText =
                            v.findViewById(R.id.sign_up_tv_first_name)
                        val sign_up_tv_last_name: EditText =
                            v.findViewById(R.id.sign_up_tv_last_name)
                        val sign_up_tv_year: EditText = v.findViewById(R.id.sign_up_tv_year)
                        val sign_up_tv_mobile_number: EditText =
                            v.findViewById(R.id.sign_up_tv_mobile_number)
                        if (TextUtils.isEmpty(signUpData.FirstName)) {
                            if (sign_up_tv_first_name != null) {
                                sign_up_tv_first_name!!.requestFocus()
                            }
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please enter First Name",
                                false
                            )
                            return@OnClickListener
                        }
                        if (TextUtils.isEmpty(signUpData.LastName)) {
                            if (sign_up_tv_last_name != null) {
                                sign_up_tv_last_name!!.requestFocus()
                            }
                            CommonMethods.SnackBar(content_sign_up, "Please enter Last Name", false)
                            return@OnClickListener
                        }

                        if (TextUtils.isEmpty(signUpData.gender)) {
                            CommonMethods.SnackBar(content_sign_up, "Please choose Gender", false)

                            return@OnClickListener
                        }
                        if (TextUtils.isEmpty(signUpData.DOB)) {

                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please enter Year of Birth",
                                false
                            )
                            if (sign_up_tv_year != null) {
                                sign_up_tv_year!!.requestFocus()
                            }
                            return@OnClickListener
                        }
                        if (signUpData.DOB!!.length != 4) {

                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Year of Birth must be 4 digits",
                                false
                            )
                            if (sign_up_tv_year != null) {
                                sign_up_tv_year!!.requestFocus()
                            }
                            return@OnClickListener
                        }
                        val year = Calendar.getInstance().get(Calendar.YEAR) - 18
                        if (signUpData.DOB!!.toInt() >= year) {
                            if (sign_up_tv_year != null) {
                                sign_up_tv_year!!.requestFocus()
                            }
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Age must be greater or equal to 18",
                                false
                            )
                            return@OnClickListener
                        }


                        if (TextUtils.isEmpty(signUpData.Mobile)) {
                            if (sign_up_tv_mobile_number != null) {
                                sign_up_tv_mobile_number!!.requestFocus()
                            }
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please enter Mobile No.",
                                false
                            )
                            return@OnClickListener
                        }
                        if (TextUtils.isEmpty(signUpData.SuburbCode)) {
                            CommonMethods.SnackBar(content_sign_up, "Please choose  Suburb", false)
                            return@OnClickListener
                        }

                        if (TextUtils.isEmpty(signUpData.Country)) {
                            CommonMethods.SnackBar(content_sign_up, "Please choose Country", false)
                            return@OnClickListener
                        }
                        if (TextUtils.isEmpty(signUpData.TimeZone)) {
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please choose  Timezone",
                                false
                            )
                            return@OnClickListener
                        }

                        view_pager.setCurrentItem(3);
                    } else if (pos == 3) {


                        if (signUpData.Categories == null) {
                            CommonMethods.SnackBar(
                                content_sign_up,
                                "Please choose Min. 5 Categories",
                                false
                            )
                            return@OnClickListener
                        }
//
//
//                        if (signUpData.Categories.size < 5) {
//                            CommonMethods.SnackBar(
//                                content_sign_up,
//                                "Please choose Min. 5 Categories",
//                                false
//                            )
//                            return@OnClickListener
//                        }


                        if (signup_btn!!.text.equals("Sign Up")) {

                            val signUp: UserDataReq =
                                UserDataReq()
                            signUp.Password = null
                            signUp.GPlusID = null
                            signUp.FacebookID = null
                            if (loginType != null && loginType.equals(AppConstant.GPLUS)) {
                                signUp.EmailID = intent.getStringExtra("email");
                                signUp.GPlusID = intent.getStringExtra("id");
                                signUp.AvatarPath = intent.getStringExtra("photo");
                            } else if (loginType != null && loginType.equals(AppConstant.FB)) {
                                signUp.EmailID = intent.getStringExtra("email");
                                signUp.FacebookID = intent.getStringExtra("id");
                                signUp.AvatarPath = intent.getStringExtra("photo");
                            } else {
                                signUp.EmailID = signUpData.email
                                signUp.GPlusID = null
                                signUp.FacebookID = null
                                signUp.Password = signUpData.password
                                signUp.AvatarPath = signUpData.avatar

                            }
                            signUp.FirstName = signUpData.FirstName
                            signUp.LastName = signUpData.LastName
                            signUp.PhoneNo = signUpData.Mobile.toString()
                            signUp.GenderCode = signUpData.gender
                            signUp.YearOfBirth = signUpData.DOB.toString()
                            signUp.Country = "AUS"
                            signUp.TimeZone = "ACDT"
//                    signUp.Country = signUpData.Country
//                    signUp.TimeZone = signUpData.TimeZone

                            signUp.GDPR = "Y"
                            signUp.CreatedOn = CommonMethods.getDateCreated();

                            val suburb: SignUpSuburb = SignUpSuburb()
                            suburb.Code = signUpData.SuburbCode
                            suburb.Name = signUpData.SuburbName
                            signUp.Suburb = suburb


                            var Categories = ArrayList<CatClass>()
                            if (signUpData.Categories != null) {
                                for (i in 0..signUpData.Categories.size - 1) {
                                    val itemListStr = signUpData.Categories.get(i)
                                    val cat = CatClass()
                                    cat.CategoryID = itemListStr.CategoryID
                                    cat.CategoryName = itemListStr.Name
                                    cat.CategoryCode = itemListStr.CategoryCode
                                    Categories.add(cat)
                                }
                            } else {
//                                Categories.add("Sports")
//                                Categories.add("Politics")
//                                Categories.add("Weather")
//                                Categories.add("UI")
//                                Categories.add("Soccer")
                            }
                            var ChannelsFollowing = ArrayList<String>()
                            ChannelsFollowing.add("BBC NEWS")
                            ChannelsFollowing.add("Animal Planet")

                            var devideData: DeviceData = DeviceData()
                            val android_id = Settings.Secure.getString(
                                getContentResolver(),
                                Settings.Secure.ANDROID_ID
                            )
                            devideData.DeviceID = android_id
                            devideData.DeviceType = "APHN"
                            signUp.Categories .addAll(Categories)
                            signUp.DeviceDetails = devideData
                            if (CommonMethods.isNetworkAvailable(this)) {
                                SignUpService(signUp)
                            }


                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    CommonMethods.SnackBar(content_sign_up, "Something went wrong", false)
                }

            })


            tv_sign_in.setOnClickListener(View.OnClickListener {
                val intent =
                    Intent(this, au.com.letsjoinin.android.UI.activity.LoginActivity::class.java)
                startActivity(intent)
                finish()
            })

            img_back_page.setOnClickListener(View.OnClickListener {

                val pos = view_pager.currentItem - 1
                view_pager.setCurrentItem(pos)
            })
        } catch (e: Exception) {
            e.printStackTrace()
            if (content_sign_up != null) {
                CommonMethods.SnackBar(content_sign_up, "Something went wrong", false)
            }
        }
    }


    override fun onVisibilityChanged(visible: Boolean) {

        val pos = view_pager.currentItem
        if(pos != 0) {
            if (visible) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    YoYo.with(Techniques.SlideOutUp)
                        .duration(300)
                        .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                        .interpolate(AccelerateDecelerateInterpolator())
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                sign_up_logo_imageView.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {

                            }

                        })
                        .playOn(sign_up_logo_imageView)
                }
            } else {
                sign_up_logo_imageView.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    YoYo.with(Techniques.SlideInDown)
                        .duration(400)
                        .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                        .interpolate(AccelerateDecelerateInterpolator())
                        .withListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {

                            }

                        })
                        .playOn(sign_up_logo_imageView)
                }
            }
        }
    }

    private fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (findViewById(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {

            private var alreadyOpen: Boolean = false
            private val defaultKeyboardHeightDP = 100
            private val EstimatedKeyboardDP =
                defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
            private val rect = Rect()

            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    EstimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                ).toInt()
                parentView.getWindowVisibleDisplayFrame(rect)
                val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
                val isShown = heightDiff >= estimatedKeyboardHeight

                if (isShown == alreadyOpen) {
                    return
                }
                alreadyOpen = isShown
                onKeyboardVisibilityListener.onVisibilityChanged(isShown)
            }
        })
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

        }
        return false;
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    var content_sign_up: RelativeLayout? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    internal var isKeyBoardSHown = false
    lateinit var signup_btn: TextView
    lateinit var img_back_page: ImageView
    fun pxTodp(dip: Float): Float {
        val resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        )
    }
//    private var confirm_info: ImageView? = null
//    private var pasword_info: ImageView? = null

    private val RC_SIGN_IN = 234
    public val signUpData: SignUpData =
        SignUpData()
    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )
    lateinit var adapterPage: MyAdapter

    inner class MyAdapter(
        fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager) {


        override fun getCount(): Int {
            return 4
        }


        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                frag0 = SignUpPage0()
                return frag0!!
            } else if (position == 1) {
                frag1 = SignUpPage1()
                return frag1!!
            } else if (position == 2) {
                frag2 = SignUpPage2()
                return frag2!!
            } else if (position == 3) {
                frag3 = SignUpPage3()
                return frag3!!
            } else
                return SignUpPage0()
        }

    }

    protected fun checkPermissions() {
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this@SignUpActivity, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(
                this@SignUpActivity,
                permissions,
                REQUEST_CODE_ASK_PERMISSIONS
            )
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
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    //                        finish();
                    return
                }
            }
        }// all permissions were granted
    }

    private val EMAIL = "email"
    private val USER_POSTS = "user_posts"
    private val AUTH_TYPE = "rerequest"


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode != RESULT_CANCELED) {
                if (data != null) {

                    if (resultCode == Activity.RESULT_OK) {
                        if (requestCode == UCrop.REQUEST_CROP) {
                            val page =
                                view_pager
                                    .getAdapter()!!
                                    .instantiateItem(
                                        view_pager,
                                        view_pager.getCurrentItem()
                                    ) as SignUpPage1;
                            // based on the current position you can then cast the page to the correct
                            // class and call the method:
                            if (view_pager.getCurrentItem() == 0 && page != null) {
                                (page as SignUpPage1).handleCropResult(data!!)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            CommonMethods.SnackBar(content_sign_up, "Something went wrong", false)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result
                    //12.674020, 79.987459
                    LocationAddress.getAddressFromLocation(
                        mLastLocation!!.latitude, mLastLocation!!.longitude,
                        applicationContext, GeocoderHandler()
                    )
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                }
            }
    }

    public override fun onStart() {
        super.onStart()

        getLastLocation()
    }


    /**
     * Callback received when a permissions request has been completed.
     */

    companion object {

        private val TAG = "LocationProvider"

        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun ValidateEnterpriseCode(str: String) {
        progress_lyt!!.visibility = View.VISIBLE
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://ljiservices20dev.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.ValidateEnterpriseCode(str)
        call.enqueue(object : Callback<ValidateEnterprise> {
            override fun onResponse(
                call: Call<ValidateEnterprise>,
                response: Response<ValidateEnterprise>
            ) {
                progress_lyt!!.visibility = View.GONE
                var res = response.body();
                EnterpriseInfo = res.EnterpriseInfo

                if (res.ServerMessage!!.Status.equals("SUCCESS")) {
                    signup_btn.visibility = View.VISIBLE
                    view_pager.setCurrentItem(1);
                } else {
                    CommonMethods.SnackBar(content_sign_up, res.ServerMessage!!.DisplayMsg, false)
                }
            }

            override fun onFailure(call: Call<ValidateEnterprise>, t: Throwable) {
                progress_lyt!!.visibility = View.GONE
            }
        })
    }

    //{"$id":"1","ServerMessageData":{"$id":"2","Status":"SUCCESS","ExMsg":null,"DisplayMsg":"User profile has been successfully created."},"UserID":"a40e6336-59a8-480c-91a7-3b43263794c1","OTP":"RAM9ZN"}
    private fun SignUpService(signUpData: UserDataReq) {
        try {
            signUpData.EnterpriseInfo = EnterpriseInfo
            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            val call = service.RegisterUser(signUpData)
            call.enqueue(object : Callback<SignUpServerMessage> {
                override fun onResponse(
                    call: Call<SignUpServerMessage>,
                    response: Response<SignUpServerMessage>
                ) {
                    var res = response.body();
                    progress_lyt!!.visibility = View.GONE
                    if (res.ServerMessage != null) {
                        if (res.ServerMessage.Status.equals(AppConstant.SUCCESS)) {
                            CommonMethods.SnackBar(
                                content_sign_up,
                                res.ServerMessage.DisplayMsg,
                                false
                            )

                            var mPrefMgr = PreferenceManager.getInstance();
                            mPrefMgr.setString(AppConstant.USERID, res.UserID)
                            mPrefMgr.setString(AppConstant.OTP, res.OTP)
                            mPrefMgr.setString(AppConstant.EmailID, signUpData.EmailID)
                            mPrefMgr.setString(AppConstant.PASSWORD, signUpData.Password)
                            mPrefMgr!!.setString(AppConstant.SMEmailID, signUpData.EmailID)
                            mPrefMgr!!.setString(AppConstant.Country, signUpData.Country)
                            Handler().postDelayed({
                                val intent =
                                    Intent(
                                        this@SignUpActivity,
                                        au.com.letsjoinin.android.UI.activity.OTPActivity::class.java
                                    )
                                startActivity(intent)
                                finish()
                            }, 1500)

                        } else {
                            CommonMethods.SnackBar(
                                content_sign_up,
                                res.ServerMessage.DisplayMsg,
                                true
                            )
                        }
                    } else {
                        CommonMethods.SnackBar(content_sign_up, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<SignUpServerMessage>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(content_sign_up, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(content_sign_up, "Something went wrong", false)
        }
    }

    private fun updateUI(account: Any) {
    }

    private inner class GeocoderHandler : Handler() {
        override fun handleMessage(message: Message) {
            var locationAddress: String?
            when (message.what) {
                1 -> {
                    val bundle = message.data
                    locationAddress = bundle.getString("address")
                }
                else -> locationAddress = null
            }
            //Unable to get address
            if (!TextUtils.isEmpty(locationAddress) && locationAddress!!.startsWith("Unable to get address")) {
                locationAddress = null
            }
            if (!TextUtils.isEmpty(locationAddress)) {
                signUpData.SuburbCode = locationAddress!!.split(" - ")[0]

                signUpData.SuburbName = locationAddress!!.split(" - ")[1]
//                if (frag2 != null && frag2!!.suburb != null) {
//                    frag2!!.suburb!!.setText(locationAddress)
//                }
            }
        }
    }
}

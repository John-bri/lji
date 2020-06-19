package au.com.letsjoinin.android.UI.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.fragment.*
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.NetworkStateReceiver
import au.com.letsjoinin.android.utils.PreferenceManager
import com.developers.viewpager.SearchFrag
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.tenclouds.fluidbottomnavigation.FluidBottomNavigationItem
import com.tenclouds.fluidbottomnavigation.listener.OnTabSelectedListener
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_navigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NavigationActivity : AppCompatActivity(), OnTabSelectedListener,
    NetworkStateReceiver.NetworkStateReceiverListener {
    internal var layoutBottomSheet: RelativeLayout? = null
    private var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>? = null
    var mPrefMgr: PreferenceManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {

            // old 9D:AC:5A:2B:A8:F6:A4:3A:3F:92:18:2D:2C:4E:98:D9:75:93:C4:01
            //91:D0:0A:12:CF:74:E5:06:C5:6E:18:A4:7C:C6:5E:98:FE:C7:7B:69 release
            //‎⁨Macintosh HD⁩ ▸ ⁨ZionGlobal⁩ ▸ ⁨John⁩ ▸ ⁨Projects⁩ ▸ ⁨LetsJoinIn_New_Android⁩
            setContentView(R.layout.activity_navigation)
            relative_parent = findViewById(R.id.drawer_layout)
            mPrefMgr = PreferenceManager.getInstance()
            fluidBottomNavigation.items =
                listOf(
                    FluidBottomNavigationItem(
                        getString(R.string.title_search),
                        ContextCompat.getDrawable(this, R.drawable.ic_search)
                    ),
                    FluidBottomNavigationItem(
                        "Timeline",
                        ContextCompat.getDrawable(this, R.drawable.ic_dashboard_black_24dp)
                    ),
                    FluidBottomNavigationItem(
                        "My Profile",
                        ContextCompat.getDrawable(this, R.drawable.ic_user)
                    ),
                    FluidBottomNavigationItem(
                        "Add a Topic",
                        ContextCompat.getDrawable(this, R.drawable.ic_message)
                    ),
                    FluidBottomNavigationItem(
                        getString(R.string.title_notifications),
                        ContextCompat.getDrawable(this, R.drawable.ic_notifications_black_24dp)
                    )
                )
            fluidBottomNavigation.onTabSelectedListener = this
            menu_title = findViewById(R.id.menu_title)
            img_back_arrow = findViewById(R.id.profile_img_arrow)
            relative_tool_bar = findViewById(R.id.relative_tool_bar)
            tv_check_connection = findViewById(R.id.tv_check_connection)
            network_change_img = findViewById(R.id.network_change_img)
            check_connection = findViewById(R.id.check_connection)
            layoutBottomSheet = findViewById(R.id.bottom_sheet)
            bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
            check_connection.bringToFront()
            relative_tool_bar.bringToFront()
            img_close_disclaimer = layoutBottomSheet!!.findViewById(R.id.img_close_disclaimer)
            img_back_arrow.setOnClickListener(View.OnClickListener { onBackPressed() })
            img_close_disclaimer.setOnClickListener(View.OnClickListener {

                bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
            })

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

            val ShowPrivacyPolicy =
                mPrefMgr!!.getBoolean(AppConstant.isFirstTimeToShowPrivacyPolicy, true)
            mPrefMgr!!.setBoolean(AppConstant.isFirstTimeToShowPrivacyPolicy, false)
            if (ShowPrivacyPolicy) {
                bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            }
//            bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            val textView = layoutBottomSheet!!.findViewById(R.id.txt1) as TextView
            textView.isClickable = true
            textView.movementMethod = LinkMovementMethod.getInstance()
            val text =
                "If you're visiting us from the EU, you may have additional rights beyond our privacy policy. By continuing to use our application, you provide your consent to the terms of our privacy policy, a copy of which can be found <a href='https://www.letsjoinin.com/privacy-policy/'>here..</a>"
            textView.text = Html.fromHtml(text)


            fluidBottomNavigation.accentColor =
                ContextCompat.getColor(this, R.color.colorPrimaryDark)
            fluidBottomNavigation.backColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            fluidBottomNavigation.textColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            fluidBottomNavigation.iconColor = ContextCompat.getColor(this, R.color.colorPrimary)
            fluidBottomNavigation.iconSelectedColor =
                ContextCompat.getColor(this, R.color.iconSelectedColor)
            networkStateReceiver = NetworkStateReceiver()
            registerNetworkBroadcastForNougat()
            fluidBottomNavigation.selectTab(1)


            if (mPrefMgr!!.getBoolean(AppConstant.FromDeepLink, false)) {
                mPrefMgr!!.setBoolean(AppConstant.FromDeepLink, false)
                val intnt = Intent(this, ChatActivity::class.java)
                intnt.putExtra("ProgramDataID", intent?.getStringExtra("ProgramDataID").toString())
                intnt.putExtra("ContentType", intent?.getStringExtra("ContentType").toString())
                intnt.putExtra("BlockPosition", 0)
                intnt.putExtra("IGroupName", intent?.getStringExtra("IGroupName").toString())
                intnt.putExtra("NotificationItemFireBaseID", intent?.getStringExtra("NotificationItemFireBaseID").toString())
                intnt.putExtra("FromDLink", true)
                startActivity(intnt)

            }
//            getImageAd("https://servedbyadbutler.com/adserve/;ID=176585;size=0x0;setID=401628;type=json;click=CLICK_MACRO_PLACEHOLDER")
        } catch (e: Exception) {
            if (relative_parent != null) {
                CommonMethods.SnackBar(relative_parent, getString(R.string.error), false)
            }
        }
    }

    private fun registerNetworkBroadcastForNougat() {
        registerReceiver(
            networkStateReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    protected fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(networkStateReceiver)
        } catch (e: IllegalArgumentException) {
            // e.printStackTrace();
        }

    }

    private fun getImageAd(str: String) {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(str + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.getImageAd("")
        call.enqueue(object : Callback<AdButlerImgResponse> {
            override fun onResponse(
                call: Call<AdButlerImgResponse>,
                response: Response<AdButlerImgResponse>
            ) {

                val suburbRes: AdButlerImgResponse = response.body();

            }

            override fun onFailure(call: Call<AdButlerImgResponse>, t: Throwable) {

                if (tv_check_connection != null) {

                }
            }
        })
    }

    override fun networkAvailable() {
        if (tv_check_connection != null) {
            check_connection.bringToFront()
            tv_check_connection.setText("Network connectivity resumed!")
            network_change_img.setImageResource(R.drawable.ic_success)
            val handler = Handler()
            val delayrunnable = Runnable {
                check_connection.setVisibility(View.GONE)
            }
            check_connection!!.getBackground()
                .setColorFilter(Color.parseColor("#63D532"), PorterDuff.Mode.SRC_ATOP);
            handler.postDelayed(delayrunnable, 2000)
        }
    }

    override fun networkUnavailable() {
        if (tv_check_connection != null) {
            check_connection.bringToFront()
            check_connection.setVisibility(View.VISIBLE)
            network_change_img.setImageResource(R.drawable.ic_warning)
            tv_check_connection.setText("Network connectivity lost!")
            check_connection!!.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }
    }


    open fun SetTabPosition(tab: Int) {
        if (fluidBottomNavigation != null) {

            fluidBottomNavigation.selectTab(tab)
        }
    }

    private var networkStateReceiver: NetworkStateReceiver? = null
    override fun onTabSelected(position: Int) {
        try {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            when (position) {

                0 -> {
                    if (menu_title != null)
                        menu_title.setText("Search")
                    val fragment = SearchFrag()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container_fragment, fragment)
                    transaction.commit()

                }
                1 -> {
                    if (menu_title != null)
                        menu_title.setText("Timeline")
                    val fragment = MainFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container_fragment, fragment)
                    transaction.commit()

                }
                2 -> {
                    if (menu_title != null)
                        menu_title.setText("Profile")
                    val fragment = ProfileDetails()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container_fragment, fragment)
                    transaction.commit()

                }
                3 -> {
                    if (menu_title != null)
                        menu_title.setText("Add a Topic")
                    val fragment = AddTopicFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container_fragment, fragment)
                    transaction.commit()

                }
                4 -> {
                    if (menu_title != null)
                        menu_title.setText("Notifications")
                    val fragment = NotificationFrag()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container_fragment, fragment)
                    transaction.commit()

                }
            }
        } catch (e: Exception) {
            CommonMethods.SnackBar(relative_parent, getString(R.string.error), false)
        }
    }

    public lateinit var menu_title: TextView
    public lateinit var tv_check_connection: TextView
    public lateinit var network_change_img: ImageView
    public lateinit var check_connection: RelativeLayout
    public lateinit var img_back_arrow: ImageView
    public lateinit var img_close_disclaimer: ImageView
    public lateinit var relative_tool_bar: RelativeLayout
    public lateinit var relative_parent: RelativeLayout
    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    //b295d746-e477-411c-888d-37f2954d7363
    private fun LoginService(data: LoginData) {
        try {
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)

            val call = service.ValidateLogin(data)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    var res = response.body();
                    if (res.ServerMessage != null) {
                        if (res.ServerMessage.Status.equals("LOGIN_SUCCESS")) {
                            mPrefMgr!!.setBoolean(AppConstant.IsLogin, true)
                            if (res.UserDoc != null) {
                                val g = Gson()
                                mPrefMgr!!.setString(AppConstant.LJIID, res.UserDoc.LJIID)
                                mPrefMgr!!.setString(AppConstant.TimeZone, res.UserDoc.TimeZone)
                                mPrefMgr!!.setString(AppConstant.GenderCode, res.UserDoc.GenderCode)
                                mPrefMgr!!.setString(AppConstant.Country, res.UserDoc.Country)
                                mPrefMgr!!.setString(AppConstant.FirstName, res.UserDoc.FirstName)
                                mPrefMgr!!.setString(AppConstant.LastName, res.UserDoc.LastName)
                                mPrefMgr!!.setString(
                                    AppConstant.YearOfBirth,
                                    res.UserDoc.YearOfBirth
                                )
                                mPrefMgr!!.setString(AppConstant.PhoneNo, res.UserDoc.PhoneNo)
                                mPrefMgr!!.setString(AppConstant.AvatarPath, res.UserDoc.AvatarPath)
                                mPrefMgr!!.setString(AppConstant.FacebookID, res.UserDoc.FacebookID)
                                mPrefMgr!!.setString(AppConstant.GPlusID, res.UserDoc.GPlusID)
                                mPrefMgr!!.setString(AppConstant.StatusCode, res.UserDoc.StatusCode)
                                mPrefMgr!!.setString(AppConstant.GDPR, res.UserDoc.GDPR)
                                mPrefMgr!!.setString(AppConstant.UserPoints, res.UserDoc.UserPoints)
                                mPrefMgr!!.setString(AppConstant.SMEmailID, res.UserDoc.EmailID)
                                mPrefMgr!!.setString(
                                    AppConstant.UserName,
                                    res.UserDoc.FirstName + " " + res.UserDoc.LastName
                                )
                                if (res.UserDoc.Categories != null && res.UserDoc.Categories.size > 0) {
                                    mPrefMgr!!.setString(
                                        AppConstant.Categories,
                                        res.UserDoc.Categories.toString()
                                    )
                                }
                                if (res.UserDoc.Suburb != null) {
                                    mPrefMgr!!.setString(
                                        AppConstant.Suburb,
                                        g.toJson(res.UserDoc.Suburb)
                                    )
                                }
                                if (res.UserDoc.ChannelsFollowing != null && res.UserDoc.ChannelsFollowing.size > 0) {
                                    mPrefMgr!!.setString(
                                        AppConstant.ChannelsFollowing,
                                        res.UserDoc.ChannelsFollowing.toString()
                                    )
                                }
                            }
                        } else {
                            CommonMethods.SnackBar(
                                relative_parent,
                                res.ServerMessage.DisplayMsg,
                                false
                            )
                            val intent = Intent(this@NavigationActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                }
            })
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (networkStateReceiver!! != null) {
                networkStateReceiver!!.addListener(this)
            }
            val login_username = mPrefMgr!!.getString(AppConstant.EmailID, AppConstant.EMPTY)
            val gplus = mPrefMgr!!.getString(AppConstant.GPlusID, AppConstant.EMPTY)
            val fBook = mPrefMgr!!.getString(AppConstant.FacebookID, AppConstant.EMPTY)
            val login_password = mPrefMgr!!.getString(AppConstant.PASSWORD, AppConstant.EMPTY)
            val logtyp = mPrefMgr!!.getString(AppConstant.LOGINTYPE, AppConstant.EMPTY)
            var data: LoginData = LoginData()
            if (mPrefMgr!!.getString(
                    AppConstant.LOGINTYPE,
                    AppConstant.EMPTY
                ).equals(AppConstant.LJI)
            ) {
                data.LoginID = login_username
                data.Password = login_password
                data.LoginType = "LJI"
            } else if (mPrefMgr!!.getString(AppConstant.LOGINTYPE, AppConstant.EMPTY).equals(
                    AppConstant.FB
                )
            ) {
                data.LoginID = fBook
                data.LoginType = AppConstant.FB


            } else if (mPrefMgr!!.getString(AppConstant.LOGINTYPE, AppConstant.EMPTY).equals(
                    AppConstant.GPLUS
                )
            ) {
                data.LoginID = gplus
                data.LoginType = AppConstant.GPLUS


            } else {
                data.LoginID = login_username
                data.Password = login_password
                data.LoginType = "LJI"
            }
            var deviceData: DeviceData = DeviceData()
            val android_id = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
            deviceData.DeviceID = android_id
            deviceData.DeviceType = "APHN"
            data.DeviceDetails = deviceData
            LoginService(data)
            val OpenEditTopic = mPrefMgr!!.getBoolean(AppConstant.OpenEditTopic, false)
            if (OpenEditTopic) {
                menu_title.text.equals("Add a Topic")
                fluidBottomNavigation.selectTab(3)
            }
        } catch (e: Exception) {
            CommonMethods.SnackBar(relative_parent, getString(R.string.error), false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (networkStateReceiver!! != null) {
                networkStateReceiver!!.removeListener(this)
                unregisterNetworkChanges()
            }
        } catch (e: Exception) {
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode != RESULT_CANCELED) {
                if (data != null) {

                    if (resultCode == Activity.RESULT_OK) {
                        if (requestCode == UCrop.REQUEST_CROP) {

                            if (menu_title.text.equals("EditProfile")) {
                                val fragment =
                                    supportFragmentManager.findFragmentById(R.id.container_fragment) as EditProfileDetails
                                // based on the current position you can then cast the page to the correct
                                // class and call the method:
                                if (fragment != null) {
                                    fragment.handleCropResult(data!!)
                                }
                            } else if (menu_title.text.equals("Add a Topic")) {
                                val fragment =
                                    supportFragmentManager.findFragmentById(R.id.container_fragment) as AddTopicFragment
                                // based on the current position you can then cast the page to the correct
                                // class and call the method:
                                if (fragment != null) {
                                    fragment.handleCropResult(data!!)
                                }
                            }
                        }
                    }
                }
            } else {
                if (requestCode == UCrop.REQUEST_CROP) {

                    if (menu_title.text.equals("EditProfile")) {
                        val fragment =
                            supportFragmentManager.findFragmentById(R.id.container_fragment) as EditProfileDetails
                        // based on the current position you can then cast the page to the correct
                        // class and call the method:
                        if (fragment != null) {
                            fragment.handleCropError()
                        }
                    } else if (menu_title.text.equals("Add a Topic")) {
                        val fragment =
                            supportFragmentManager.findFragmentById(R.id.container_fragment) as AddTopicFragment
                        // based on the current position you can then cast the page to the correct
                        // class and call the method:
                        if (fragment != null) {
                            fragment.handleCropError()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            CommonMethods.SnackBar(relative_parent, "Something went wrong", false)
        }
    }


}

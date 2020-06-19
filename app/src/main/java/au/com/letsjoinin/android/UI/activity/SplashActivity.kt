package au.com.letsjoinin.android.UI.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager.widget.ViewPager
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.LoginData
import au.com.letsjoinin.android.MVP.model.LoginResponse
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.fragment.FirstFragment
import au.com.letsjoinin.android.UI.fragment.FourthFragment
import au.com.letsjoinin.android.UI.fragment.SecondFragment
import au.com.letsjoinin.android.UI.fragment.ThirdFragment
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class SplashActivity : AppCompatActivity() {
    private lateinit var view_parent: RelativeLayout
    var mPrefMgr: PreferenceManager? = null
    private var progress_lyt: RelativeLayout? = null
    internal var SharedSK: String? = null
    internal var SharedType: String? = null
    internal var Screen: String? = null
    internal var viewPager: ViewPager? = null
    internal var ONE: ImageView? = null
    internal var TWO: ImageView? = null
    internal var THREE: ImageView? = null
    internal var FOUR: ImageView? = null
    internal var im_next: RelativeLayout? = null
    internal var relative_bg: RelativeLayout? = null
    internal var im_go: RelativeLayout? = null
    internal var im_skip: RelativeLayout? = null
    internal var second: Fragment? = null
    internal var third: Fragment? = null
    internal var fourth: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefMgr = PreferenceManager.getInstance()
        mPrefMgr!!.setBoolean(AppConstant.OpenEditTopic, false)
        setContentView(R.layout.activity_splash)
        val splashLayout = findViewById<View>(R.id.splash) as RelativeLayout
        val login = mPrefMgr!!.getBoolean(AppConstant.IsLogin, false)
        val login_username = mPrefMgr!!.getString(AppConstant.EmailID, AppConstant.EMPTY)
        val login_password = mPrefMgr!!.getString(AppConstant.PASSWORD, AppConstant.EMPTY)
        val FirstTimeIntro = mPrefMgr!!.getBoolean(AppConstant.FirstTimeIntro, false)
        mPrefMgr!!.setBoolean(AppConstant.FirstTimeIntro, true)
        mPrefMgr!!.setBoolean(AppConstant.isFirstTimeToShowPrivacyPolicy, false)
        mPrefMgr!!.setBoolean(AppConstant.TopicImageSelected, false)
        view_parent = findViewById(R.id.view_parent) as RelativeLayout
        relative_bg = findViewById(R.id.relative_bg) as RelativeLayout
        progress_lyt = findViewById(R.id.progress_lyt);
        progress_lyt!!.visibility = View.GONE
        relative_bg!!.visibility = View.GONE

        val appLinkIntent = intent

        val b = appLinkIntent.getBooleanExtra("FromPNIdle", false)
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data


        val y: String
        val SPLASH_DISPLAY_LENGTH = 1500
        if (login) {
            val intent = Intent(this, NavigationActivity::class.java)
            if(b){
                val ProgramDataID = appLinkIntent.getStringExtra("ProgramDataID") // x = "1.2"
                val ContentType = appLinkIntent.getStringExtra("ContentType") // x = "1.2"
                val GroupName =appLinkIntent.getStringExtra("GroupName") // x = "1.2"
                intent.putExtra("ProgramDataID", ProgramDataID)
                intent.putExtra("ContentType", ContentType)
                intent.putExtra("IGroupName", GroupName)
                mPrefMgr!!.setBoolean(AppConstant.FromDeepLink, true)
            }else  if (appLinkData != null) {
                SharedSK = appLinkData.getQueryParameter("UserGroupForSK") // x = "1.2"
                SharedType = appLinkData.getQueryParameter("UserGroupFor") // x = "1.2"
                val ProgramDataID = appLinkData.getQueryParameter("ProgramDataID") // x = "1.2"
                val ContentType = appLinkData.getQueryParameter("ContentType") // x = "1.2"
                val GroupName = appLinkData.getQueryParameter("GroupName") // x = "1.2"
                val NotificationItemFireBaseID =
                    appLinkData.getQueryParameter("ChatID") // x = "1.2"
                Screen = appLinkData.getQueryParameter("Screen") // x = "1.2"
                mPrefMgr!!.setBoolean(AppConstant.FromDeepLink, true)
                intent.putExtra("ProgramDataID", ProgramDataID)
                intent.putExtra("ContentType", ContentType)
                intent.putExtra("IGroupName", GroupName)
                intent.putExtra("NotificationItemFireBaseID", NotificationItemFireBaseID)
            }

            Handler().postDelayed({
                startActivity(intent)
                finish()
            }, SPLASH_DISPLAY_LENGTH.toLong())

        } else {
            if (!FirstTimeIntro) {

                Handler().postDelayed({

                    splashLayout!!.visibility = View.GONE
                    relative_bg!!.visibility = View.VISIBLE

                }, SPLASH_DISPLAY_LENGTH.toLong())
                viewPager = findViewById(R.id.view_pager) as ViewPager
                ONE = findViewById(R.id.i_one) as ImageView
                TWO = findViewById(R.id.i_two) as ImageView
                THREE = findViewById(R.id.i_three) as ImageView
                FOUR = findViewById(R.id.i_four) as ImageView
                im_skip = findViewById(R.id.im_skip) as RelativeLayout
                im_next = findViewById(R.id.im_next) as RelativeLayout
                im_go = findViewById(R.id.im_go) as RelativeLayout
                im_skip!!.setOnClickListener(View.OnClickListener {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                })
                im_go!!.setOnClickListener(View.OnClickListener {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                })
                im_next!!.setOnClickListener(View.OnClickListener {
                    //                if(viewPager.get)
                    viewPager!!.setCurrentItem(viewPager!!.getCurrentItem() + 1)
                })
                val adapterViewPager = MyPagerAdapter(supportFragmentManager)
                viewPager!!.setAdapter(adapterViewPager)
                viewPager!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        im_next!!.setVisibility(View.VISIBLE)
                        im_go!!.setVisibility(View.GONE)
                        (findViewById(R.id.relative_bg) as RelativeLayout).setBackgroundColor(
                            Color.parseColor(
                                "#ffffff"
                            )
                        )
                        if (position == 0) {
//                        (findViewById(R.id.relative_bg) as RelativeLayout).setBackgroundColor(Color.parseColor("#cecdcd"))
                            ONE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_purple))
                            TWO!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            THREE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            FOUR!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                        } else if (position == 1) {
//                        (findViewById(R.id.relative_bg) as RelativeLayout).setBackgroundColor(Color.parseColor("#6d9ec2"))
                            TWO!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_purple))
                            ONE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            THREE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            FOUR!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                        } else if (position == 2) {
//                        (findViewById(R.id.relative_bg) as RelativeLayout).setBackgroundColor(Color.parseColor("#eb502c"))
                            THREE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_purple))
                            ONE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            TWO!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            FOUR!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                        } else if (position == 3) {
                            im_next!!.setVisibility(View.GONE)
                            im_go!!.setVisibility(View.VISIBLE)
//                        (findViewById(R.id.relative_bg) as RelativeLayout).setBackgroundColor(Color.parseColor("#403a8c"))
                            FOUR!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_purple))
                            ONE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            TWO!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                            THREE!!.setImageDrawable(resources.getDrawable(R.drawable.round_splash_white))
                        }
                    }

                    override fun onPageSelected(position: Int) {
                        if (second != null) {
                            (second as SecondFragment).Update()
                        }
                        if (position == 1) {
                            if (second != null) {
                                (second as SecondFragment).ViewUpdate()
                            }
                        } else if (position == 2) {
                        } else if (position == 3) {
                        }

                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        if (state == 0) {
                        }
                    }
                })
            } else {
                Handler().postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, SPLASH_DISPLAY_LENGTH.toLong())

            }
        }

    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return 4
        }


        override fun getItem(pos: Int): Fragment {

            if (pos == 0) {
//                if (second != null) {
//                    (second as SecondFragment).Update()
//                }
                return FirstFragment.newInstance("FirstFragment, Instance 1", "")
            } else if (pos == 1) {
                second = SecondFragment.newInstance("SecondFragment, Instance 2", "")
                return second!!
            } else if (pos == 2) {
//                if (second != null) {
//                    (second as SecondFragment).Update()
//                }
                return ThirdFragment.newInstance("ThirdFragment, Instance 3", "")
            } else if (pos == 3) {
//                if (second != null) {
//                    (second as SecondFragment).Update()
//                }
                return FourthFragment.newInstance("FourthFragment, Instance 4", "")
            } else {
//                if (second != null) {
//                    (second as SecondFragment).Update()
//                }
                return FirstFragment.newInstance("ThirdFragment, Default", "")
            }


//            when (pos) {
//                0 -> return FirstFragment.newInstance("FirstFragment, Instance 1","")
//                1 -> return SecondFragment.newInstance("SecondFragment, Instance 2","")
//                2 -> return ThirdFragment.newInstance("ThirdFragment, Instance 3","")
//                3 -> return FourthFragment.newInstance("FourthFragment, Instance 4","")
//                else -> return FirstFragment.newInstance("ThirdFragment, Default","")
//            }
        }
    }

    fun showPrompt(view: View) {
        if (view != null) {
            MaterialTapTargetPrompt.Builder(this)
                .setPrimaryText("Centre")
                .setSecondaryText("R.string.fab_centre_description")
                .setAnimationInterpolator(FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen.max_prompt_width)
                .setTarget(view)
                .show()
        }
    }

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
                            mPrefMgr!!.setString(AppConstant.USERID, res.UserID)
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
                            progress_lyt!!.visibility = View.GONE
                            val intent =
                                Intent(
                                    this@SplashActivity,
                                    au.com.letsjoinin.android.UI.activity.NavigationActivity::class.java
                                )
                            startActivity(intent)
                            finish()
                        } else if (res.ServerMessage.Status.equals("OTP_VERIFICATION_PENDING")) {

                            mPrefMgr!!.setString(AppConstant.USERID, res.UserID)
                            mPrefMgr!!.setString(AppConstant.OTP, res.OTP)
                            CommonMethods.SnackBar(view_parent, res.ServerMessage.DisplayMsg, true)
                            val intent =
                                Intent(
                                    this@SplashActivity,
                                    au.com.letsjoinin.android.UI.activity.OTPActivity::class.java
                                )
                            startActivity(intent)
                            finish()
                        } else {
                            CommonMethods.SnackBar(view_parent, res.ServerMessage.DisplayMsg, true)
                            val intent =
                                Intent(
                                    this@SplashActivity,
                                    au.com.letsjoinin.android.UI.activity.LoginActivity::class.java
                                )
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            CommonMethods.SnackBar(view_parent, "Something went wrong", false)
        }
    }
}

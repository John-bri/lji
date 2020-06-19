package au.com.letsjoinin.android.UI.activity

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.DeviceData
import au.com.letsjoinin.android.MVP.model.LoginData
import au.com.letsjoinin.android.MVP.model.LoginResponse
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.OnKeyboardVisibilityListener
import au.com.letsjoinin.android.utils.PreferenceManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL
import java.util.*


class LoginActivity : AppCompatActivity(), TextView.OnEditorActionListener, OnKeyboardVisibilityListener {
    var gplusDetails: GoogleSignInAccount? = null
    var FBFirstName: String? = null
    var FBlastrName: String? = null
    var FBID: String? = null
    var FBEmail: String? = null
    var FBimage_url: String? = null
    var layout_sign_in: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {

            setContentView(R.layout.activity_login)

            mPrefMgr = PreferenceManager.getInstance();
            mPrefMgr!!.setBoolean(AppConstant.IsLogin, false)
            setKeyboardVisibilityListener(this);
            checkPermissions()
            FacebookSdk.sdkInitialize(this.getApplicationContext());
//////////////
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    val msg = getString(R.string.msg_token_fmt, token)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                })
            var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            var account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this);

            mCallbackManager = CallbackManager.Factory.create();
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut()
            }
            val mLoginButton = findViewById(R.id.login_button) as LoginButton
            val mFBLoginButton = findViewById(R.id.img_fb) as RelativeLayout
            layout_sign_in = findViewById(R.id.layout_sign_in) as RelativeLayout
            val mGPlusButton = findViewById(R.id.img_gplus) as RelativeLayout
            val progress_lyt = findViewById(R.id.progress_lyt) as RelativeLayout
            val rememberme = findViewById(R.id.rememberme) as CheckBox
            val login_tv_username = findViewById(R.id.login_tv_username) as EditText
            val login_tv_password = findViewById(R.id.login_tv_password) as EditText
            val forgotpassword = findViewById<LinearLayout>(R.id.forgot_password_lyt)
            progress_lyt!!.visibility = View.GONE
            login_tv_password.setOnEditorActionListener(this)
            mGPlusButton.setOnClickListener(View.OnClickListener {

                signIn()
            })
            forgotpassword.setOnClickListener(View.OnClickListener {
                login_tv_password.setText("")
                val intent = Intent(this, ForgotActivity::class.java)
                startActivity(intent)

            })
            val RememberMe = mPrefMgr!!.getBoolean(AppConstant.RememberMe, false)
            rememberme.isChecked = RememberMe
            val login_username = mPrefMgr!!.getString(AppConstant.EmailID, AppConstant.EMPTY)
            if (RememberMe) {
                login_tv_username.setText(login_username)
            }
            rememberme.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {

                override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                    mPrefMgr!!.setBoolean(AppConstant.RememberMe, isChecked)
                    mPrefMgr!!.setString(AppConstant.EmailID, login_tv_username.text.toString())
                }
            }
            )
            // Set the initial permissions to request from the user while logging in
            mLoginButton.setReadPermissions(Arrays.asList<String>(EMAIL))

            mLoginButton.setAuthType(AUTH_TYPE)
//        val accessToken : AccessToken = AccessToken.getCurrentAccessToken();
//        val isLoggedIn : Boolean = accessToken != null && !accessToken.isExpired();
            // Register a callback to respond to the user

            mLoginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    getUserProfile(AccessToken.getCurrentAccessToken())
                }

                override fun onCancel() {

                }

                override fun onError(e: FacebookException) {
                    // Handle exception
                }
            })



            mFBLoginButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val handler = Handler()
                    handler.postDelayed({
                        mLoginButton.performClick()
                    }, 200)

                }

            })



            login_btn.setOnClickListener(View.OnClickListener {
                //                val intent =
//                    Intent(this@LoginActivity, au.com.letsjoinin.android.UI.activity.NavigationActivity::class.java)
//                startActivity(intent)
//                finish()
                val view = this.currentFocus
                view?.let { v ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
                }
                if (TextUtils.isEmpty(login_tv_username.text.toString())) {
                    login_tv_username.requestFocus()
                    CommonMethods.SnackBar(login_parent, "Please enter Email Id", false)
                    return@OnClickListener
                }
                if (!isValidEmail(login_tv_username.text.toString())) {
                    login_tv_username.requestFocus()
                    CommonMethods.SnackBar(login_parent, "Please enter valid Email Id", false)
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(login_tv_password.text.toString())) {
                    login_tv_password.requestFocus()
                    CommonMethods.SnackBar(login_parent, "Please enter Password.", false)
                    return@OnClickListener
                }
                if (login_tv_password.text.toString().length < 6) {
                    login_tv_password.requestFocus()
                    CommonMethods.SnackBar(login_parent, "Password should have minimum 6 characters.", false)
                    return@OnClickListener
                }
                var data: LoginData = LoginData()
                var devideData: DeviceData = DeviceData()
                data.LoginID = login_tv_username.text.toString()
                data.Password = login_tv_password.text.toString()
                data.LoginType = AppConstant.LJI
                mPrefMgr!!.setString(AppConstant.LOGINTYPE, AppConstant.LJI)

                val android_id = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
                devideData.DeviceID = android_id
                devideData.DeviceType = "APHN"
                data.DeviceDetails = devideData
                progress_lyt.visibility = View.VISIBLE
                LoginService(data)
            })
            tv_sign_up.setOnClickListener(View.OnClickListener {
                mPrefMgr!!.setString(AppConstant.LOGINTYPE, AppConstant.LJI)
                val intent = Intent(this, au.com.letsjoinin.android.UI.activity.SignUpActivity::class.java)
                intent.putExtra("login_type", AppConstant.LJI)
                startActivity(intent)
                finish()
            })
        } catch (e: Exception) {
            if (login_parent != null) {
                CommonMethods.SnackBar(login_parent, "Something went wrong", false)
            }
        }
    }

    private fun getUserProfile(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            currentAccessToken
        ) { `object`, response ->
            try {
                FBFirstName = `object`.getString("first_name")
                FBlastrName = `object`.getString("last_name")
                FBEmail = `object`.getString("email")
                FBID = `object`.getString("id")
                FBimage_url = "https://graph.facebook.com/$FBID/picture?type=normal"

                disconnectFromFb()
//                mPrefMgr!!.setString(AppConstant.Base64String, CommonMethods.getFacebookProfilePicture(FBID))
                var data: LoginData = LoginData()
                var devideData: DeviceData = DeviceData()
                data.LoginID = FBID

                data.LoginType = AppConstant.FB
                val android_id = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
                mPrefMgr!!.setString(AppConstant.LOGINTYPE, AppConstant.FB)

                mGoogleSignInClient!!.signOut()
                devideData.DeviceID = android_id
                devideData.DeviceType = "APHN"
                data.DeviceDetails = devideData
                progress_lyt.visibility = View.VISIBLE
                LoginService(data)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()

    }

    private fun disconnectFromFb() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
            GraphRequest.Callback {
                AccessToken.setCurrentAccessToken(null);
                LoginManager.getInstance().logOut()
            }).executeAsync()

    }

    override fun onVisibilityChanged(visible: Boolean) {
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
                            logo_imageView.visibility = View.GONE
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {

                        }

                    })
                    .playOn(logo_imageView)
            }
        } else {
            logo_imageView.visibility = View.VISIBLE
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
                    .playOn(logo_imageView)
            }
        }
    }

    private fun setKeyboardVisibilityListener(onKeyboardVisibilityListener: OnKeyboardVisibilityListener) {
        val parentView = (findViewById(android.R.id.content) as ViewGroup).getChildAt(0)
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

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

    private var mPrefMgr: PreferenceManager? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    internal var isKeyBoardSHown = false
    fun pxTodp(dip: Float): Float {
        val resources = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        )
    }

    private val RC_SIGN_IN = 234
    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA
    )


    protected fun checkPermissions() {
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this@LoginActivity, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(this@LoginActivity, permissions, REQUEST_CODE_ASK_PERMISSIONS)
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

                    //                        finish();
                    return
                }
            }
        }// all permissions were granted
    }

    private val EMAIL = "email"
    private val USER_POSTS = "user_posts"
    private val AUTH_TYPE = "rerequest"

    private var mCallbackManager: CallbackManager? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode != RESULT_CANCELED) {
                if (data != null) {

                    if (requestCode === RC_SIGN_IN) {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.sarala 1039542627210-brbog8vbnoqbvftkl1fbpm2qsmqcmf3a.apps.googleusercontent.com
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        handleSignInResult(task)
                    }
                    mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
                }
            }
        } catch (e: Exception) {
            CommonMethods.SnackBar(login_parent, "Something went wrong", false)
        }
    }

    override fun onResume() {
        super.onResume()
    }
    fun objectToJSONObject(`object`: Any): JSONObject? {
        var json: Any? = null
        var jsonObject: JSONObject? = null
        try {
            json = JSONTokener(`object`.toString()).nextValue()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (json is JSONObject) {
            jsonObject = json
        }
        return jsonObject
    }
    private fun LoginService(data: LoginData) {
        try {









            Log.d("LoginService", "Start")
            // progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            Log.d("LoginService", "BUild")
            var service: ApiService = retrofit.create(ApiService::class.java)

            val call = service.ValidateLogin(data)
            Log.d("LoginService", "Call service")
//            doAsync {
//                var conn: HttpURLConnection? = null
//                try {
//                    val url = URL(AppConstant.BaseURL+"User/ValidateLoginWithSM")
//                    conn = url.openConnection() as HttpURLConnection
//                    conn.readTimeout = 10000
//                    conn.connectTimeout = 15000
//                    conn.requestMethod = "POST"
//                    conn.doInput = true
//                    conn.doOutput = true
//                    val output = BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8"))
//                    output.write(objectToJSONObject(data).toString())
//                    output.flush()
//
//                        val response = conn!!.inputStream
//                         BufferedReader(InputStreamReader(response)).use {
//                            val response = StringBuffer()
//
//                            var inputLine = it.readLine()
//                            while (inputLine != null) {
//                                response.append(inputLine)
//                                inputLine = it.readLine()
//                            }
//                            it.close()
//                             Log.d("LoginService", response.toString())
//                        }
//
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    conn!!.disconnect()
//                }
//            }




            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    var res = response.body();
                    // progress_lyt!!.visibility = View.GONE
                    progress_lyt!!.visibility = View.GONE
                    if (res.ServerMessage != null) {

                        if (res.ServerMessage.Status.equals("LOGIN_SUCCESS")) {
                            mPrefMgr!!.setString(AppConstant.EmailID, login_tv_username.text.toString())
                            mPrefMgr!!.setString(AppConstant.PASSWORD, login_tv_password.text.toString())
                            mPrefMgr!!.setString(AppConstant.USERID, res.UserID)
                            mPrefMgr!!.setBoolean(AppConstant.IsLogin, true)
                            if (res.UserDoc != null) {
                                val g = Gson()
                                mPrefMgr!!.setBoolean(AppConstant.OpenEditTopic, false)
                                mPrefMgr!!.setString(AppConstant.LJIID, res.UserDoc.LJIID)
                                mPrefMgr!!.setString(AppConstant.TimeZone, res.UserDoc.TimeZone)
                                mPrefMgr!!.setString(AppConstant.GenderCode, res.UserDoc.GenderCode)
                                mPrefMgr!!.setString(AppConstant.Country, res.UserDoc.Country)
                                mPrefMgr!!.setString(AppConstant.FirstName, res.UserDoc.FirstName)
                                mPrefMgr!!.setString(AppConstant.LastName, res.UserDoc.LastName)
                                mPrefMgr!!.setString(AppConstant.YearOfBirth, res.UserDoc.YearOfBirth)
                                mPrefMgr!!.setString(AppConstant.PhoneNo, res.UserDoc.PhoneNo)
                                mPrefMgr!!.setString(AppConstant.AvatarPath, res.UserDoc.AvatarPath)
                                mPrefMgr!!.setString(AppConstant.FacebookID, res.UserDoc.FacebookID)
                                mPrefMgr!!.setString(AppConstant.GPlusID, res.UserDoc.GPlusID)
                                mPrefMgr!!.setString(AppConstant.StatusCode, res.UserDoc.StatusCode)
                                mPrefMgr!!.setString(AppConstant.GDPR, res.UserDoc.GDPR)
                                mPrefMgr!!.setString(AppConstant.UserPoints, res.UserDoc.UserPoints)
                                mPrefMgr!!.setString(AppConstant.SMEmailID, res.UserDoc.EmailID)


                                mPrefMgr!!.setString(AppConstant.EnterPriseLogoPath, res.UserDoc.EnterpriseInfo.LogoPath)
                                mPrefMgr!!.setString(AppConstant.EnterPriseName, res.UserDoc.EnterpriseInfo.Name)
                                mPrefMgr!!.setString(AppConstant.EnterPrisePKCountry,res.UserDoc.EnterpriseInfo.PKCountry)
                                mPrefMgr!!.setString(AppConstant.EnterpriseID,res.UserDoc.EnterpriseInfo.EnterpriseID)



                                mPrefMgr!!.setString(
                                    AppConstant.UserName,
                                    res.UserDoc.FirstName + " " + res.UserDoc.LastName
                                )
                                if (res.UserDoc.Categories != null && res.UserDoc.Categories.size > 0) {
                                    mPrefMgr!!.setString(AppConstant.Categories, res.UserDoc.Categories.toString())
                                }
                                if (res.UserDoc.Suburb != null) {
                                    mPrefMgr!!.setString(AppConstant.Suburb, g.toJson(res.UserDoc.Suburb))
                                }
                                if (res.UserDoc.ChannelsFollowing != null && res.UserDoc.ChannelsFollowing.size > 0) {
                                    mPrefMgr!!.setString(
                                        AppConstant.ChannelsFollowing,
                                        res.UserDoc.ChannelsFollowing.toString()
                                    )
                                }
                            }
                            CommonMethods.SnackBar(login_parent, res.ServerMessage.DisplayMsg, false)
                            val intent =
                                Intent(
                                    this@LoginActivity,
                                    au.com.letsjoinin.android.UI.activity.NavigationActivity::class.java
                                )
                            startActivity(intent)
                            finish()
//                            val snackbar = Snackbar
//                                .make(login_parent, res.ServerMessage.DisplayMsg, Snackbar.LENGTH_INDEFINITE)
//                                .setAction("Ok", View.OnClickListener {
//
//                                })
//
//                            snackbar.show()
                        } else if (res.ServerMessage.Status.equals(AppConstant.SM_NEW_USER)) {

                            CommonMethods.SnackBar(login_parent, res.ServerMessage.DisplayMsg, false)

                            val intent = Intent(
                                this@LoginActivity,
                                au.com.letsjoinin.android.UI.activity.SignUpActivity::class.java
                            )
                            if (mPrefMgr!!.getString(
                                    AppConstant.LOGINTYPE,
                                    AppConstant.EMPTY
                                ).equals(AppConstant.GPLUS)
                            ) {
                                mPrefMgr!!.setString(AppConstant.EmailID, gplusDetails!!.email)
                                mPrefMgr!!.setString(AppConstant.SMEmailID, gplusDetails!!.email)
                                mPrefMgr!!.setString(AppConstant.SignUPFirstName, gplusDetails!!.givenName)
                                mPrefMgr!!.setString(AppConstant.SignUPLastName, gplusDetails!!.familyName)
                                mPrefMgr!!.setString(AppConstant.GPlusID, gplusDetails!!.id)
                                if (gplusDetails!!.photoUrl != null) {
                                    mPrefMgr!!.setString(AppConstant.AvatarPath, gplusDetails!!.photoUrl.toString())
                                }

                                intent.putExtra("login_type", AppConstant.GPLUS)
                                intent.putExtra("email", gplusDetails!!.email)
                                intent.putExtra("name", gplusDetails!!.displayName)
                                intent.putExtra("id", gplusDetails!!.id)
                                if (gplusDetails!!.photoUrl != null) {
                                    intent.putExtra("photo", gplusDetails!!.photoUrl.toString())
                                }
                            } else if (mPrefMgr!!.getString(
                                    AppConstant.LOGINTYPE,
                                    AppConstant.EMPTY
                                ).equals(AppConstant.FB)
                            ) {
                                mPrefMgr!!.setString(AppConstant.SMEmailID, FBEmail)
                                mPrefMgr!!.setString(AppConstant.EmailID, FBEmail)
                                mPrefMgr!!.setString(AppConstant.SignUPFirstName, FBFirstName)
                                mPrefMgr!!.setString(AppConstant.SignUPLastName, FBlastrName)
                                mPrefMgr!!.setString(AppConstant.FacebookID, FBID)
                                mPrefMgr!!.setString(AppConstant.AvatarPath, FBimage_url)

                                intent.putExtra("login_type", AppConstant.FB)
                                intent.putExtra("email", FBEmail)
                                intent.putExtra("name", FBFirstName)
                                intent.putExtra("id", FBID)
                                intent.putExtra("photo", FBimage_url)

                            }







                            Handler().postDelayed({
                                startActivity(intent)
                                finish()
                            }, 600)

                        } else if (res.ServerMessage.Status.equals("OTP_VERIFICATION_PENDING")) {


                            val snackbar = Snackbar
                                .make(login_parent, res.ServerMessage.DisplayMsg, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", View.OnClickListener {
                                    mPrefMgr!!.setString(AppConstant.EmailID, login_tv_username.text.toString())
                                    mPrefMgr!!.setString(AppConstant.PASSWORD, login_tv_password.text.toString())
                                    if (!TextUtils.isEmpty(res.UserID.trim())) {
                                        mPrefMgr!!.setString(AppConstant.USERID, res.UserID)
                                    }
                                    mPrefMgr!!.setString(AppConstant.OTP, res.OTP)
                                    val intent =
                                        Intent(
                                            this@LoginActivity,
                                            au.com.letsjoinin.android.UI.activity.OTPActivity::class.java
                                        )
                                    startActivity(intent)
                                    finish()
                                })
                            snackbar.setActionTextColor(Color.WHITE)
                            snackbar.show()
                        } else if (res.ServerMessage.Status.equals("RSTPWD_OTP_VERIFICATION_PENDING")) {


                            val snackbar = Snackbar
                                .make(login_parent, res.ServerMessage.DisplayMsg, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", View.OnClickListener {
                                    mPrefMgr!!.setString(AppConstant.EmailID, login_tv_username.text.toString())
                                    mPrefMgr!!.setString(AppConstant.PASSWORD, login_tv_password.text.toString())
                                    if (res.UserDoc != null) {
                                        mPrefMgr!!.setString(AppConstant.Country, res.UserDoc.Country)
                                    }
                                    if (!TextUtils.isEmpty(res.UserID.trim())) {
                                        mPrefMgr!!.setString(AppConstant.USERID, res.UserID)
                                    }
                                    mPrefMgr!!.setString(AppConstant.OTP, res.OTP)
                                    val intent =
                                        Intent(
                                            this@LoginActivity,
                                            au.com.letsjoinin.android.UI.activity.ForgotActivity::class.java
                                        )
                                    startActivity(intent)
                                    finish()
                                })
                            snackbar.setActionTextColor(Color.WHITE)
                            snackbar.show()
                        } else {
                            CommonMethods.SnackBar(login_parent, res.ServerMessage.DisplayMsg, true)
                        }
                    } else {
                        CommonMethods.SnackBar(login_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(login_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(login_parent, "Something went wrong", false)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            val personPhoto = account!!.getPhotoUrl()
            var s = account!!.email
            var s1 = account!!.displayName
            var s2 = account!!.id
            gplusDetails = account;
            var data: LoginData = LoginData()
            var devideData: DeviceData = DeviceData()
            data.LoginID = account!!.id

            data.LoginType = AppConstant.GPLUS
            val android_id = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
            mPrefMgr!!.setString(AppConstant.LOGINTYPE, AppConstant.GPLUS)
//            mPrefMgr!!.setString(AppConstant.Base64String, CommonMethods.getGPlusProfilePicture(this@LoginActivity,personPhoto))

            mGoogleSignInClient!!.signOut()
            devideData.DeviceID = android_id
            devideData.DeviceType = "APHN"
            data.DeviceDetails = devideData
            progress_lyt.visibility = View.VISIBLE
            LoginService(data)

            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("error", e.message.toString())
        }
    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun updateUI(account: Any) {

    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

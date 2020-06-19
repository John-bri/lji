package au.com.letsjoinin.android.UI.activity

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.fragment.ForgotFragment
import au.com.letsjoinin.android.UI.fragment.OTPFragment
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForgotActivity : AppCompatActivity() {

    internal lateinit var pin_one: EditText
    internal lateinit var pin_two: EditText
    internal lateinit var pin_three: EditText
    internal lateinit var pin_four: EditText
    internal lateinit var pin_five: EditText
    internal lateinit var pin_six: EditText
    var OTP: String? = null
    var UserID: String? = null
    var UserCountry: String? = null
    private var layout_otp_password: LinearLayout? = null
    private var layout_request_mail: LinearLayout? = null
    private var email_id_edittext: EditText? = null
    private var send_request_btn: TextView? = null
    private var title: TextView? = null
    private var reset_btn: TextView? = null
    private var txt_resend_otp: TextView? = null
    //  private var Layout_loading: RelativeLayout? = null
    private var view_parent: RelativeLayout? = null
    private var confirm_info: ImageView? = null
    private var pasword_info: ImageView? = null
    private var confirm_password_edittext: EditText? = null
    private var new_password_edittext: EditText? = null
    private var progress_lyt: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        val back = findViewById<ImageView>(R.id.back)
        pin_one = findViewById(R.id.pin_one)
        pin_two = findViewById(R.id.pin_two)
        pin_three = findViewById(R.id.pin_three)
        pin_four = findViewById(R.id.pin_four)
        pin_five = findViewById(R.id.pin_five)
        pin_six = findViewById(R.id.pin_six)
        back.setOnClickListener(View.OnClickListener { finish() })
        confirm_password_edittext = findViewById(R.id.confirm_password_edittext);
        new_password_edittext = findViewById(R.id.new_password_edittext);
        progress_lyt = findViewById(R.id.progress_lyt);
        title = findViewById(R.id.title);
        view_parent = findViewById(R.id.view_parent)
        layout_otp_password = findViewById(R.id.layout_otp_password)
        pasword_info = findViewById(R.id.pasword_info)
        confirm_info = findViewById(R.id.confirm_info)
        layout_request_mail = findViewById(R.id.layout_request_mail)
        email_id_edittext = findViewById(R.id.email_id_edittext)
        send_request_btn = findViewById(R.id.send_request_btn)
        reset_btn = findViewById(R.id.reset_btn)
        layout_request_mail!!.visibility = View.VISIBLE
        layout_otp_password!!.visibility = View.GONE
        txt_resend_otp = findViewById(R.id.txt_resend_otp);
        var mPrefMgr = PreferenceManager.getInstance();
        // Layout_loading = findViewById(R.id.Layout_loading) as RelativeLayout
        progress_lyt!!.visibility = View.GONE

        // Layout_loading!!.bringToFront()
        send_request_btn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                hideSoftKeyboard(email_id_edittext!!)
                if (TextUtils.isEmpty(email_id_edittext!!.text.toString())) {
                    CommonMethods.SnackBar(view_parent, "Please enter Email Id.", false)
                    return
                }
                if (!isValidEmail(email_id_edittext!!.text.toString())) {
                    CommonMethods.SnackBar(view_parent, "Please enter valid Email Id.", false)
                    return
                }
                ForgotPasswordService()
            }

        })
        pasword_info!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                CommonMethods.SnackBar(view_parent, resources.getString(R.string.password_hint), true)


            }

        })
        pin_one.addTextChangedListener(object : TextWatcher {
            internal var old = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                old = charSequence.toString()
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                val u = 0
            }

            override fun afterTextChanged(editable: Editable) {

                if (pin_one.text.length > 0) {
                    if (editable.toString().length > 1) {
                        val arr =
                            editable.toString().trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var s = editable.toString()[0].toString()

                        for (i in arr.indices) {
                            if (arr[i] != "") {
                                if (arr[i] != old) {
                                    s = arr[i]
                                }
                            }

                        }
                        editable.clear()
                        editable.insert(0, s)
                    }
                    pin_two.requestFocus(0)
                }


            }
        })
        pin_two.addTextChangedListener(object : TextWatcher {
            internal var old = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                old = charSequence.toString()
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val u = 0
            }

            override fun afterTextChanged(editable: Editable) {

                if (pin_two.text.length > 0) {
                    if (editable.toString().length > 1) {
                        val arr =
                            editable.toString().trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var s = editable.toString()[0].toString()

                        for (i in arr.indices) {
                            if (arr[i] != "") {
                                if (arr[i] != old) {
                                    s = arr[i]
                                }
                            }

                        }
                        editable.clear()
                        editable.insert(0, s)
                    }
                    pin_three.requestFocus(pin_three.text.length)
                } else {
                    pin_one.requestFocus(0)
                }
            }
        })
        pin_three.addTextChangedListener(object : TextWatcher {
            internal var old = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                old = charSequence.toString()
            }


            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val u = 0
            }

            override fun afterTextChanged(editable: Editable) {

                if (pin_three.text.length > 0) {
                    if (editable.toString().length > 1) {
                        val arr =
                            editable.toString().trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var s = editable.toString()[0].toString()

                        for (i in arr.indices) {
                            if (arr[i] != "") {
                                if (arr[i] != old) {
                                    s = arr[i]
                                }
                            }

                        }
                        editable.clear()
                        editable.insert(0, s)
                    }
                    pin_four.requestFocus(pin_four.text.length)
                } else {
                    pin_two.requestFocus(0)
                }
            }
        })
        pin_four.addTextChangedListener(object : TextWatcher {
            internal var old = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                old = charSequence.toString()
            }


            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val u = 0
            }

            override fun afterTextChanged(editable: Editable) {

                if (pin_four.text.length > 0) {
                    if (editable.toString().length > 1) {
                        val arr =
                            editable.toString().trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var s = editable.toString()[0].toString()

                        for (i in arr.indices) {
                            if (arr[i] != "") {
                                if (arr[i] != old) {
                                    s = arr[i]
                                }
                            }

                        }
                        editable.clear()
                        editable.insert(0, s)
                    }
                    pin_five.requestFocus(pin_five.text.length)
                } else {
                    pin_three.requestFocus(0)
                }
            }
        })
        pin_five.addTextChangedListener(object : TextWatcher {
            internal var old = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                old = charSequence.toString()
            }


            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {

                if (pin_five.text.length > 0) {
                    if (editable.toString().length > 1) {
                        val arr =
                            editable.toString().trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var s = editable.toString()[0].toString()

                        for (i in arr.indices) {
                            if (arr[i] != "") {
                                if (arr[i] != old) {
                                    s = arr[i]
                                }
                            }

                        }
                        editable.clear()
                        editable.insert(0, s)
                    }
                    pin_six.requestFocus(pin_six.text.length)
                    //                    hideKeyboard(getActivity());
                } else {
                    pin_four.requestFocus(0)
                }
            }
        })
        pin_six.addTextChangedListener(object : TextWatcher {
            internal var old = ""

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                old = charSequence.toString()
            }


            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (pin_six.text.length > 0) {
                    if (editable.toString().length > 1) {
                        val arr =
                            editable.toString().trim { it <= ' ' }.split("".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var s = editable.toString()[0].toString()

                        for (i in arr.indices) {
                            if (arr[i] != "") {
                                if (arr[i] != old) {
                                    s = arr[i]
                                }
                            }

                        }
                        editable.clear()
                        editable.insert(0, s)
                    }
                } else if (!TextUtils.isEmpty(old) && old.length == 1) {
                    pin_five.requestFocus(0)
                }

            }
        })


        pin_one.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                pin_one.requestFocus(View.FOCUS_LEFT)
                pin_one.setText("")
            }
            false
        }
        pin_two.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                pin_one.requestFocus(View.FOCUS_LEFT)
                pin_two.setText("")
            }
            false
        }
        pin_three.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                pin_two.requestFocus(View.FOCUS_LEFT)
                pin_three.setText("")
            }
            false
        }
        pin_four.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                pin_three.requestFocus(View.FOCUS_LEFT)
                pin_four.setText("")
            }
            false
        }
        pin_five.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                pin_four.requestFocus(View.FOCUS_LEFT)
                pin_five.setText("")
            }
            false
        }
        pin_six.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                pin_five.requestFocus(View.FOCUS_LEFT)
                pin_six.setText("")
            }
            false
        }

//        confirm_info!!.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//
//                CommonMethods.SnackBar(view_parent, resources.getString(R.string.cpassword_hint),true)
//
//            }
//
//        })

        UserID = mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY)
        var OTPStr: String = mPrefMgr.getString(AppConstant.OTP, AppConstant.EMPTY)
        UserCountry = mPrefMgr.getString(AppConstant.Country, AppConstant.EMPTY)
        txt_resend_otp!!.setOnClickListener(View.OnClickListener {
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
            }
            var data: ResendOTPReq = ResendOTPReq()
            //data.OTP  = mPinFirstDigitEditText!!.text.toString().trim()+mPinSecondDigitEditText!!.text.toString().trim()+mPinThirdDigitEditText!!.text.toString().trim()+mPinForthDigitEditText!!.text.toString().trim()+mPinFifthDigitEditText!!.text.toString().trim()+mPinSixthDigitEditText!!.text.toString().trim()
            data.UserID = UserID
            data.UserCountry = UserCountry
            ResendOTP(data)

        })
        if (!TextUtils.isEmpty(OTPStr) && OTPStr.length >= 6) {
            OTP = OTPStr
            pin_one!!.setText(OTPStr.split("")[1])
            pin_two!!.setText(OTPStr.split("")[2])
            pin_three!!.setText(OTPStr.split("")[3])
            pin_four!!.setText(OTPStr.split("")[4])
            pin_five!!.setText(OTPStr.split("")[5])
            pin_six!!.setText(OTPStr.split("")[6])


            layout_request_mail!!.visibility = View.GONE
            layout_otp_password!!.visibility = View.VISIBLE
            title!!.text = "Reset Password"
            mPrefMgr.setString(AppConstant.USERID, AppConstant.EMPTY)
            mPrefMgr.setString(AppConstant.OTP, AppConstant.EMPTY)
        }

        reset_btn!!.setOnClickListener(View.OnClickListener {


            val pin =
                pin_one!!.text.toString().trim() + pin_two!!.text.toString().trim() + pin_three!!.text.toString().trim() + pin_four!!.text.toString().trim() + pin_five!!.text.toString().trim() + pin_six!!.text.toString().trim()
            if (pin!!.trim().length >= 6) {
                if (TextUtils.isEmpty(new_password_edittext!!.text.toString())) {
                    if (new_password_edittext != null) {
                        new_password_edittext!!.requestFocus()
                    }
                    CommonMethods.SnackBar(view_parent, "Please enter Password.", false)
                    return@OnClickListener
                }
                if (new_password_edittext!!.text.toString().length < 6) {
                    if (new_password_edittext!!.text.toString() != null) {
                        new_password_edittext!!.requestFocus()
                    }
                    CommonMethods.SnackBar(view_parent, "Password should have minimum 6 characters.", false)
                    return@OnClickListener
                }
                if (!CommonMethods.isValidPassword(new_password_edittext!!.text.toString())) {
                    if (new_password_edittext != null) {
                        new_password_edittext!!.requestFocus()
                    }
                    CommonMethods.SnackBar(view_parent, "Please enter valid New Password", false)
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(confirm_password_edittext!!.text.toString().trim())) {
                    if (confirm_password_edittext != null) {
                        confirm_password_edittext!!.requestFocus()
                    }
                    CommonMethods.SnackBar(view_parent, "Please enter Confirm Password", false)
                    return@OnClickListener
                }
//            if (confirm_password_edittext!!.text.toString().length <6) {
//                if (confirm_password_edittext!!.text.toString() != null) {
//                    confirm_password_edittext!!.requestFocus()
//                }
//                CommonMethods.SnackBar(view_parent, "Confirm Password contain atleast 6 character.", false)
//                return@OnClickListener
//            }
                if (!new_password_edittext!!.text.toString().equals(confirm_password_edittext!!.text.toString())) {
                    if (confirm_password_edittext != null) {
                        confirm_password_edittext!!.setText("")
                    }
                    if (new_password_edittext != null) {
                        new_password_edittext!!.setText("")
                        new_password_edittext!!.requestFocus()
                    }
                    CommonMethods.SnackBar(view_parent, "Password and Confirm Password doesn’t match", false)
                    return@OnClickListener
                }

                var input: ResetPasswordReq = ResetPasswordReq()
                input.NewPassword = new_password_edittext!!.text.toString().trim()
                //input.OTP = mPinFirstDigitEditText!!.text.toString().trim()+mPinSecondDigitEditText!!.text.toString().trim()+mPinThirdDigitEditText!!.text.toString().trim()+mPinForthDigitEditText!!.text.toString().trim()+mPinFifthDigitEditText!!.text.toString().trim()+mPinSixthDigitEditText!!.text.toString().trim()
                input.OTP =
                    pin_one!!.text.toString().trim() + pin_two!!.text.toString().trim() + pin_three!!.text.toString().trim() + pin_four!!.text.toString().trim() + pin_five!!.text.toString().trim() + pin_six!!.text.toString().trim()
                input.UserCountry = UserCountry
                input.UserID = UserID

                var deviceData: DeviceData = DeviceData()
                val android_id = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
                deviceData.DeviceID = android_id
                deviceData.DeviceType = "APHN"
                input.DeviceDetails = deviceData
                ResetPassword(input)
            } else {
                CommonMethods.SnackBar(view_parent, "Please enter OTP", false)
            }

        })
    }

    private fun ResendOTP(data: ResendOTPReq) {
        try {
            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.ResendOTP(data)
            call.enqueue(object : Callback<ResendOTPResponse> {
                override fun onResponse(call: Call<ResendOTPResponse>, response: Response<ResendOTPResponse>) {
                    val res = response.body()
                    progress_lyt!!.visibility = View.GONE
                    if (res != null && res.ServerMessage != null) {
                        if (res.ServerMessage.Status.equals(AppConstant.SUCCESS)) {
                            pin_one!!.setText(res.OTP.split("")[1])
                            pin_two!!.setText(res.OTP.split("")[2])
                            pin_three!!.setText(res.OTP.split("")[3])
                            pin_four!!.setText(res.OTP.split("")[4])
                            pin_five!!.setText(res.OTP.split("")[5])
                            pin_six!!.setText(res.OTP.split("")[6])
                            CommonMethods.SnackBar(view_parent, res.ServerMessage!!.DisplayMsg, false)
                        } else {
                            CommonMethods.SnackBar(view_parent, res.ServerMessage.DisplayMsg, true)
                        }
                    } else {
                        CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<ResendOTPResponse>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(view_parent, "Something went wrong", false)
        }
    }

    fun showSoftKeyboard(editText: EditText?) {
        if (editText == null)
            return

        val imm = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(editText, 0)
    }


    fun hideSoftKeyboard(editText: EditText?) {
        if (editText == null)
            return

        val imm = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun ForgotPasswordService() {
        try {
            progress_lyt!!.visibility = View.VISIBLE
            // progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)

            val call = service.VerifyForgotPassword(email_id_edittext!!.text.toString().trim())
            call.enqueue(object : Callback<PasswordOTPResponse> {
                override fun onResponse(call: Call<PasswordOTPResponse>, response: Response<PasswordOTPResponse>) {
                    var res = response.body();
                    progress_lyt!!.visibility = View.GONE
                    if (res.ServerMessage != null) {
                        if (res.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {
//                            var mPrefMgr = PreferenceManager.getInstance();
                            OTP = res.OTP!!
                            pin_one!!.setText( res.OTP!!.split("")[1])
                            pin_two!!.setText( res.OTP!!.split("")[2])
                            pin_three!!.setText( res.OTP!!.split("")[3])
                            pin_four!!.setText( res.OTP!!.split("")[4])
                            pin_five!!.setText( res.OTP!!.split("")[5])
                            pin_six!!.setText( res.OTP!!.split("")[6])
                            title!!.text = "Reset Password"
                            UserCountry = res.UserCountry
                            UserID = res.UserID
                            if (TextUtils.isEmpty(UserID) || TextUtils.isEmpty(UserCountry)) {
                                CommonMethods.SnackBar(view_parent, "Something went wrong.", false)
                                return
                            }
                            CommonMethods.SnackBar(view_parent, res.ServerMessage!!.DisplayMsg, false)


                            Handler().postDelayed({
                                layout_otp_password!!.visibility = View.VISIBLE
                                layout_request_mail!!.visibility = View.GONE
                                title!!.text = "Reset Password"
                                if (!TextUtils.isEmpty(OTP) && OTP!!.length >= 5) {

                                }
                            }, 1500)

                        } else {
                            CommonMethods.SnackBar(view_parent, res.ServerMessage!!.DisplayMsg, true)
                        }
                    } else {
                        CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<PasswordOTPResponse>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(view_parent, "Something went wrong", false)
        }
    }

    private fun ResetPassword(input: ResetPasswordReq) {
        try {
            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)

            val call = service.ResetPassword(input)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    var res = response.body();
                    progress_lyt!!.visibility = View.GONE
                    if (res.ServerMessage != null) {
                        if (res.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {
                            var mPrefMgr = PreferenceManager.getInstance();
                            mPrefMgr!!.setString(AppConstant.USERID, res.UserID)
                            mPrefMgr!!.setBoolean(AppConstant.IsLogin, true)
                            if (res.UserDoc != null) {
                                val g = Gson()
                                mPrefMgr!!.setString(AppConstant.EmailID, res.UserDoc.EmailID)
                                mPrefMgr!!.setString(AppConstant.SMEmailID, res.UserDoc.EmailID)
                                mPrefMgr!!.setString(AppConstant.PASSWORD, input.NewPassword)
                                mPrefMgr!!.setString(AppConstant.LOGINTYPE, AppConstant.LJI)
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
                            val intent =
                                Intent(
                                    this@ForgotActivity,
                                    NavigationActivity::class.java
                                )
                            startActivity(intent)
                            finish()
                        } else {
                            CommonMethods.SnackBar(view_parent, res.ServerMessage!!.DisplayMsg, true)
                        }
                    } else {
                        CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(view_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(view_parent, "Something went wrong", false)
        }
    }

    class ViewPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val COUNT = 2

        override fun getItem(position: Int): Fragment? {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = ForgotFragment()
                1 -> fragment = OTPFragment()

            }

            return fragment
        }

        override fun getCount(): Int {
            return COUNT
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "Tab " + (position + 1)
        }

    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

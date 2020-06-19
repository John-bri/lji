package au.com.letsjoinin.android.UI.activity

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.LoginResponse
import au.com.letsjoinin.android.MVP.model.OTPData
import au.com.letsjoinin.android.MVP.model.ResendOTPReq
import au.com.letsjoinin.android.MVP.model.ResendOTPResponse
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.Pinview
import au.com.letsjoinin.android.utils.PreferenceManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.otp_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RedeemActivity : AppCompatActivity() {

    internal lateinit var pin_one: EditText
    internal lateinit var pin_two: EditText
    internal lateinit var pin_three: EditText
    internal lateinit var pin_four: EditText
    internal lateinit var pin_five: EditText
    internal lateinit var pin_six: EditText
    private var txt_resend_otp: TextView? = null
    private var progress_lyt: RelativeLayout? = null
    private var otp_parent: RelativeLayout? = null
    private var otp_Layout_loading: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_layout)
        progress_lyt = findViewById(R.id.progress_lyt);
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener(View.OnClickListener
        {
            finish()
        })

        otp_parent = findViewById(R.id.otp_parent);
        txt_resend_otp = findViewById(R.id.txt_resend_otp);
        pin_one = findViewById(R.id.pin_one)
        pin_two = findViewById(R.id.pin_two)
        pin_three = findViewById(R.id.pin_three)
        pin_four = findViewById(R.id.pin_four)
        pin_five = findViewById(R.id.pin_five)
        pin_six = findViewById(R.id.pin_six)
        otp_Layout_loading = findViewById(R.id.otp_Layout_loading);
        otp_Layout_loading!!.bringToFront()
        var mPrefMgr = PreferenceManager.getInstance();
        var USERID: String = mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY)
        var OTP: String = mPrefMgr.getString(AppConstant.OTP, AppConstant.EMPTY)



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
                            editable.toString().trim { it <= ' ' }.split("".toRegex())
                                .dropLastWhile { it.isEmpty() }
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
                            editable.toString().trim { it <= ' ' }.split("".toRegex())
                                .dropLastWhile { it.isEmpty() }
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
                            editable.toString().trim { it <= ' ' }.split("".toRegex())
                                .dropLastWhile { it.isEmpty() }
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
                            editable.toString().trim { it <= ' ' }.split("".toRegex())
                                .dropLastWhile { it.isEmpty() }
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
                            editable.toString().trim { it <= ' ' }.split("".toRegex())
                                .dropLastWhile { it.isEmpty() }
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
                            editable.toString().trim { it <= ' ' }.split("".toRegex())
                                .dropLastWhile { it.isEmpty() }
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


        if (!TextUtils.isEmpty(OTP) && OTP.length >= 5) {
            pin_one!!.setText(OTP.split("")[1])
            pin_two!!.setText(OTP.split("")[2])
            pin_three!!.setText(OTP.split("")[3])
            pin_four!!.setText(OTP.split("")[4])
            pin_five!!.setText(OTP.split("")[5])
            pin_six!!.setText(OTP.split("")[6])
            mPrefMgr.setString(AppConstant.USERID, AppConstant.EMPTY)
            mPrefMgr.setString(AppConstant.OTP, AppConstant.EMPTY)
        }

        val EditTopicContentID = intent?.getStringExtra("ProgramDataID").toString()
        val ContentType = intent?.getStringExtra("ContentType").toString()
        val PKChannelId = intent?.getStringExtra("PKChannelId").toString()
        verify_btn.setOnClickListener(View.OnClickListener {
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
            }
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("ProgramDataID", EditTopicContentID)
            intent.putExtra("ContentType", ContentType)
            intent.putExtra("PKChannelId", PKChannelId)
            intent.putExtra("BlockPosition", 0)
            startActivity(intent)
            val pin =
                pin_one!!.text.toString().trim() + pin_two!!.text.toString().trim() + pin_three!!.text.toString().trim() + pin_four!!.text.toString().trim() + pin_five!!.text.toString().trim() + pin_six!!.text.toString().trim()
//            if (pin!!.trim().length >= 6) {
//                var data: OTPData = OTPData()
//                data.OTP =
//                    pin_one!!.text.toString().trim() + pin_two!!.text.toString().trim() + pin_three!!.text.toString().trim() + pin_four!!.text.toString().trim() + pin_five!!.text.toString().trim() + pin_six!!.text.toString().trim()
//                data.UserID = USERID
//                data.UserCountry = mPrefMgr.getString(AppConstant.Country, AppConstant.EMPTY)
////                OTPService(data)
//
//
//
//            } else {
//                CommonMethods.SnackBar(otp_parent, "Please enter OTP", false)
//            }

            finish()
        })

        txt_resend_otp!!.setOnClickListener(View.OnClickListener {
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
            }
            var data: ResendOTPReq = ResendOTPReq()
            //data.OTP  = mPinFirstDigitEditText!!.text.toString().trim()+mPinSecondDigitEditText!!.text.toString().trim()+mPinThirdDigitEditText!!.text.toString().trim()+mPinForthDigitEditText!!.text.toString().trim()+mPinFifthDigitEditText!!.text.toString().trim()+mPinSixthDigitEditText!!.text.toString().trim()
            data.UserID = USERID
            data.UserCountry = "AUS"
//            ResendOTP(data)
            finish()

        })

    }

    fun showSoftKeyboard(editText: EditText?) {
        if (editText == null)
            return

        val imm = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(editText, 0)
    }

    fun setFocus(editText: EditText?) {
        if (editText == null)
            return

        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
    }


    fun hideSoftKeyboard(editText: EditText?) {
        if (editText == null)
            return

        val imm = getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(editText.windowToken, 0)
    }


    private fun setFocusedPinBackground(editText: EditText) {
//                setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_focused_holo_light));
    }

    private fun setDefaultPinBackground(editText: EditText) {
//                setViewBackground(editText, getResources().getDrawable(R.drawable.holo_li));
    }

    private fun OTPService(data: OTPData) {
        try {
            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.ValidateOTP(data)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val res = response.body()
                    progress_lyt!!.visibility = View.GONE
                    if (res.ServerMessage != null) {
                        if (res.ServerMessage.Status.equals(AppConstant.SUCCESS)) {
                            mPrefMgr.setString(AppConstant.USERID, res.UserID)
                            mPrefMgr.setBoolean(AppConstant.IsLogin, true)
                            if (res.UserDoc != null) {
                                val g = Gson()
                                mPrefMgr!!.setString(AppConstant.LJIID, res.UserDoc.LJIID)
                                mPrefMgr!!.setString(AppConstant.TimeZone, res.UserDoc.TimeZone)
                                mPrefMgr!!.setString(AppConstant.GenderCode, res.UserDoc.GenderCode)
                                mPrefMgr!!.setString(AppConstant.Country, res.UserDoc.Country)
                                mPrefMgr!!.setString(AppConstant.FirstName, res.UserDoc.FirstName)
                                mPrefMgr!!.setString(AppConstant.LastName, res.UserDoc.LastName)
                                mPrefMgr!!.setString(AppConstant.EmailID, res.UserDoc.EmailID)
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
                            val intent =
                                Intent(
                                    this@RedeemActivity,
                                    au.com.letsjoinin.android.UI.activity.NavigationActivity::class.java
                                )
                            startActivity(intent)
                            finish()
                        } else {
                            CommonMethods.SnackBar(otp_parent, res.ServerMessage.DisplayMsg, true)
                        }
                    } else {
                        CommonMethods.SnackBar(otp_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(otp_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(otp_parent, "Something went wrong", false)
        }
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
                override fun onResponse(
                    call: Call<ResendOTPResponse>,
                    response: Response<ResendOTPResponse>
                ) {
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
                            CommonMethods.SnackBar(
                                otp_parent,
                                res.ServerMessage!!.DisplayMsg,
                                false
                            )
                        } else {
                            CommonMethods.SnackBar(otp_parent, res.ServerMessage.DisplayMsg, true)
                        }
                    } else {
                        CommonMethods.SnackBar(otp_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<ResendOTPResponse>, t: Throwable) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(otp_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(otp_parent, "Something went wrong", false)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

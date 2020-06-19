package com.developers.viewpager


import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.GetEnterpriseSecurityQuestionData
import au.com.letsjoinin.android.MVP.model.SearchSuburbRes
import au.com.letsjoinin.android.MVP.model.SuburbList
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.SignUpActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.Holder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignUpPage2 : Fragment() {
    private lateinit var builder: DialogPlus
    private var txt_search_suburb: EditText? = null
    private var sign_up_tv_last_field: EditText? = null
    private var sign_up_tv_last_field_date: EditText? = null
    private var close_dialog: TextView? = null
    // TODO: Rename and change types of parameters
    private var layout_question_date: RelativeLayout? = null
    private var layout_parent: RelativeLayout? = null
    private var txt_enterprise_question_date: TextView? = null
    private var txt_enterprise_question_text: TextView? = null
    private var progress_lyt: RelativeLayout? = null
    private var layout_question_text: LinearLayout? = null
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var gender: Spinner? = null
    var countrySpin: Spinner? = null
    var timezone: Spinner? = null
    internal var ListOfSuburb = ArrayList<SuburbList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    open fun Update() {
        showSequence()
    }

    fun showSequence() {
//        MaterialTapTargetSequence()
//            .addPrompt(
//                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
//                    .setTarget(mailID)
//                    .setPrimaryText("Step 1")
//                    .setSecondaryText("Enter Email-ID")
//                    .setBackgroundColour(resources.getColor(R.color.colorPrimary))
//                    .setPromptBackground(RectanglePromptBackground())
//                    .setPromptFocal(RectanglePromptFocal())
//                    .create(), 4000)
//            .addPrompt(
//                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
//                    .setTarget(password)
//                    .setPrimaryText("Step 2")
//                    .setSecondaryText("Enter Password")
//                    .setBackgroundColour(resources.getColor(R.color.colorPrimary))
//                    .setPromptBackground(RectanglePromptBackground())
//                    .setPromptFocal(RectanglePromptFocal())
//                    .create(), 4000)
//            .addPrompt(
//                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
//                    .setPrimaryText("Info")
//                    .setSecondaryText(resources.getString(R.string.password_hint))
//                    .setAnimationInterpolator(FastOutSlowInInterpolator())
//                    .setMaxTextWidth(R.dimen.max_prompt_width)
//                    .setTarget(pasword_info)
//                    .setBackgroundColour(resources.getColor(R.color.colorPrimary))
//                    .create(), 8000)
//
//            .addPrompt(
//                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
//                    .setTarget(confirmPassword)
//                    .setPrimaryText("Step 3")
//                    .setSecondaryText("Enter Password again to confirm")
//                    .setBackgroundColour(resources.getColor(R.color.colorPrimary))
//                    .setPromptBackground(RectanglePromptBackground())
//                    .setPromptFocal(RectanglePromptFocal())
//                    .create(), 4000)
//            .addPrompt(
//                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
//                    .setPrimaryText("Profile Picture")
//                    .setSecondaryText("Pick or capture a picture to upload(optional)")
//                    .setAnimationInterpolator(FastOutSlowInInterpolator())
//                    .setMaxTextWidth(R.dimen.max_prompt_width)
//                    .setTarget(imageView)
//                    .setBackgroundColour(resources.getColor(R.color.colorPrimary))
//                    .create(), 4000)
//
//            .addPrompt(
//                MaterialTapTargetPrompt.Builder(activity as SignUpActivity)
//                    .setTarget( (activity as SignUpActivity).signup_btn)
//                    .setPrimaryText("Next")
//                    .setSecondaryText("Click Next to move second page")
//                    .setBackgroundColour(resources.getColor(R.color.colorPrimary))
//                    .setPromptBackground(RectanglePromptBackground())
//                    .setPromptFocal(RectanglePromptFocal())
//                    .create(), 4000)
//            .show()
    }

    fun pxTodp(dip: Float): Int {
        val resources = resources
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            resources.displayMetrics
        ).toInt()
                )
    }

    private var lv: ListView? = null
    internal var suburb: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.sign_up_page2, container, false)
        try {
            layout_question_date = v.findViewById(R.id.layout_question_date)
            txt_enterprise_question_date = v.findViewById(R.id.txt_enterprise_question_1)
            layout_parent = v.findViewById(R.id.sign2_parent)
            sign_up_tv_last_field_date = v.findViewById(R.id.sign_up_tv_last_field_date)
            layout_question_text = v.findViewById(R.id.layout_question_text)
            progress_lyt = v.findViewById(R.id.progress_lyt)
            val sign_up_tv_first_name: EditText = v.findViewById(R.id.sign_up_tv_first_name)
            txt_enterprise_question_text = v.findViewById(R.id.txt_enterprise_question)
            sign_up_tv_last_field = v.findViewById(R.id.sign_up_tv_last_field)
            val sign_up_tv_last_name: EditText = v.findViewById(R.id.sign_up_tv_last_name)
            val sign_up_tv_year: EditText = v.findViewById(R.id.sign_up_tv_year)
            val sign_up_tv_mobile_number: EditText = v.findViewById(R.id.sign_up_tv_mobile_number)
            val img_search_suburb: ImageView = v.findViewById(R.id.img_search_suburb)
            var mPrefsMgr = PreferenceManager.getInstance();
            var login_lusername =
                mPrefsMgr!!.getString(AppConstant.SignUPLastName, AppConstant.EMPTY).trim()
            var login_fusername =
                mPrefsMgr!!.getString(AppConstant.SignUPFirstName, AppConstant.EMPTY).trim()
            if (login_lusername != null) {
                (activity as SignUpActivity).signUpData.LastName = login_lusername
                sign_up_tv_last_name.setText(login_lusername)
            }
            if (login_fusername != null) {
                (activity as SignUpActivity).signUpData.FirstName = login_fusername
                sign_up_tv_first_name.setText(login_fusername)
            }
            mPrefsMgr!!.setString(AppConstant.SignUPFirstName, "")
            mPrefsMgr!!.setString(AppConstant.SignUPLastName, "")
            gender = v.findViewById(R.id.gender_input) as Spinner
            countrySpin = v.findViewById(R.id.sign_up_tv_country_input) as Spinner
            timezone = v.findViewById(R.id.sign_up_tv_time_input) as Spinner
            val countries = ArrayList<String>()
            countries.add("Country")
            countries.add("Australia")
            val adapter = ArrayAdapter(activity!!, R.layout.signup_spinner_item, countries)
            countrySpin!!.setAdapter(adapter)
            val genderMap = HashMap<Int, String>()
            val timezoneMap = HashMap<Int, String>()

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


            val TZ1 = ArrayList<String>()
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
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    val textView = adapterView.getChildAt(0) as TextView
                    if (i == 0) {
                        (activity as SignUpActivity).signUpData.Country = null
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                    } else {
                        (activity as SignUpActivity).signUpData.Country = "AUS"
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                    }
                    (adapterView.getChildAt(0) as TextView).textSize = 15f
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            })
            suburb = v.findViewById(R.id.suburb)

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
                    close_dialog = view.findViewById(R.id.close_dialog) as TextView
                    txt_no_suburbs = view.findViewById(R.id.txt_no_suburbs) as TextView
                    txt_search_suburb = view.findViewById(R.id.txt_search_suburb) as EditText
                    lv = view.findViewById(R.id.list_view) as ListView
                    txt_no_suburbs!!.visibility = View.GONE
                    txt_search_suburb!!.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {

                        }

                        override fun afterTextChanged(s: Editable) {

                            try {
                                txt_no_suburbs!!.visibility = View.GONE
                                if (!TextUtils.isEmpty(s.toString().trim())) {
                                    SearchSuburb(s.toString())
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
                .setPadding(0, 0, 0, 0)
                .setGravity(Gravity.TOP)
                .setCancelable(false)
                .create();

            close_dialog!!.setOnClickListener(View.OnClickListener {

                CommonMethods.hideKeyboardFrom(activity, txt_search_suburb!!)
                Handler().postDelayed({
                    builder.dismiss()

                }, 800)

            })
            suburb!!.setText((activity as SignUpActivity).signUpData.SuburbName+"-"+(activity as SignUpActivity).signUpData.SuburbCode)
            suburb!!.setOnClickListener(View.OnClickListener {

                val inputMethodManager =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                txt_search_suburb!!.setText("")

                Handler().postDelayed({
                    txt_search_suburb!!.requestFocus()
                }, 500)

                builder.show()
            })


            img_search_suburb!!.setOnClickListener(View.OnClickListener {

                val inputMethodManager =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                txt_search_suburb!!.setText("")

                Handler().postDelayed({
                    txt_search_suburb!!.requestFocus()
                }, 500)

                builder.show()
            })
            timezone!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    val textView = adapterView.getChildAt(0) as TextView
                    (activity as SignUpActivity).signUpData.TimeZone = null
                    if (i == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                    } else {
                        (activity as SignUpActivity).signUpData.TimeZone = timezoneMap.get(i)
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                    }
                    (adapterView.getChildAt(0) as TextView).textSize = 15f
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            })
            gender!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    val textView = adapterView.getChildAt(0) as TextView
                    (activity as SignUpActivity).signUpData.gender = null
                    if (i == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                    } else {
                        (activity as SignUpActivity).signUpData.gender = genderMap.get(i)
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                    }
                    (adapterView.getChildAt(0) as TextView).textSize = 15f

                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            })

            sign_up_tv_first_name.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    (activity as SignUpActivity).signUpData.FirstName = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })

            sign_up_tv_last_name.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    (activity as SignUpActivity).signUpData.LastName = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })

            sign_up_tv_year.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    (activity as SignUpActivity).signUpData.DOB = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
            sign_up_tv_mobile_number.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    (activity as SignUpActivity).signUpData.Mobile = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })

//            if (CommonMethods.isNetworkAvailable(activity!!)) {
//                SignUpService()
//            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }

        return v
    }

    private fun SignUpService() {
        try {
            progress_lyt!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            val call = service.SecurityQuestion("c9ff2fca-d3e7-4913-87f2-638e226d0105")
            call.enqueue(object : Callback<GetEnterpriseSecurityQuestionData> {
                override fun onResponse(
                    call: Call<GetEnterpriseSecurityQuestionData>,
                    response: Response<GetEnterpriseSecurityQuestionData>
                ) {
                    var res = response.body();
                    progress_lyt!!.visibility = View.GONE
                    if (res.ServerMessage != null) {

                        if (res.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {

                            if (res.EnterpriseSecQ != null && res.EnterpriseSecQ!!.RegSecurityQuestion!!.Enabled != null && res.EnterpriseSecQ!!.RegSecurityQuestion!!.Enabled.equals(
                                    "Y"
                                )
                            ) {

                                if (res.EnterpriseSecQ!!.RegSecurityQuestion!!.DataType.equals("TEXT")) {
                                    layout_question_text!!.visibility = View.VISIBLE
                                    layout_question_date!!.visibility = View.GONE
                                    txt_enterprise_question_text!!.text =
                                        res.EnterpriseSecQ!!.RegSecurityQuestion!!.Question


                                    val lgth = res.EnterpriseSecQ!!.RegSecurityQuestion!!.Length!!.toInt()
                                    sign_up_tv_last_field!!.setFilters(
                                        arrayOf<InputFilter>(
                                            InputFilter.LengthFilter(
                                                lgth
                                            )
                                        )
                                    )
                                } else if (res.EnterpriseSecQ!!.RegSecurityQuestion!!.DataType.equals("DATE")
                                ) {

                                    layout_question_date!!.setOnClickListener(View.OnClickListener {



                                        val cldr = Calendar.getInstance()
                                        val day = cldr[Calendar.DAY_OF_MONTH]
                                        val month = cldr[Calendar.MONTH]
                                        val year = cldr[Calendar.YEAR]
                                        // date picker dialog
                                        // date picker dialog
                                        val picker =
                                            DatePickerDialog(
                                                activity!!,
                                                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                                    //                        eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                                }, year, month, day
                                            )
                                        picker.show()
                                    })
                                    layout_question_text!!.visibility = View.GONE
                                    layout_question_date!!.visibility = View.VISIBLE
                                    txt_enterprise_question_date!!.text =
                                        res.EnterpriseSecQ!!.RegSecurityQuestion!!.Question
                                }


                            }
                        }
                    } else {
                        CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(
                    call: Call<GetEnterpriseSecurityQuestionData>,
                    t: Throwable
                ) {
                    progress_lyt!!.visibility = View.GONE
                    CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            progress_lyt!!.visibility = View.GONE
            CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
        }
    }

    private var SuburbSK: Long = 0

    private var txt_no_suburbs: TextView? = null
    private fun SearchSuburb(str: String) {
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://ljiservicesdev.azurewebsites.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)

        val call = service.SearchSuburb(str)
        call.enqueue(object : Callback<SearchSuburbRes> {
            override fun onResponse(
                call: Call<SearchSuburbRes>,
                response: Response<SearchSuburbRes>
            ) {
                if (activity != null) {
                    val suburbRes: SearchSuburbRes = response.body();
                    lv!!.setAdapter(null)
                    if (suburbRes.ServerStatus.Status.equals(AppConstant.SUCCESS)) {
                        if (suburbRes.ListOfSuburb != null) {
                            ListOfSuburb = suburbRes.ListOfSuburb
                            val subUrbArr = arrayOfNulls<String?>(suburbRes.ListOfSuburb.size)
                            for (i in 0 until suburbRes.ListOfSuburb.size) {
                                subUrbArr[i] = suburbRes.ListOfSuburb.get(i).PostalCodeLocality
                            }
                            // Adding items to listview
                            var arrayAdapter: ArrayAdapter<String?> =
                                ArrayAdapter(
                                    activity!!,
                                    R.layout.suburb_list_item,
                                    R.id.tv_suburb_name,
                                    subUrbArr
                                )
                            lv!!.setAdapter(arrayAdapter)
                            txt_no_suburbs!!.visibility = View.GONE
                            if (ListOfSuburb!!.size == 0) {
                                lv!!.visibility = View.GONE
                                txt_no_suburbs!!.visibility = View.VISIBLE
                            } else {
                                lv!!.visibility = View.VISIBLE
                            }
                            lv!!.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                                SuburbSK = ListOfSuburb.get(position).SuburbSK
                                suburb!!.setText(ListOfSuburb.get(position).PostalCodeLocality)
                                (activity as SignUpActivity).signUpData.SuburbCode =
                                    ListOfSuburb.get(position).PostalCode
                                (activity as SignUpActivity).signUpData.SuburbName =
                                    ListOfSuburb.get(position).Locality
                                CommonMethods.hideKeyboardFrom(activity, txt_search_suburb!!)
                                builder.dismiss()
                            })
                        } else {
                            lv!!.visibility = View.GONE
                            txt_no_suburbs!!.visibility = View.VISIBLE
                        }
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
            SignUpPage2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

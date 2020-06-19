package com.developers.viewpager


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import au.com.letsjoinin.android.MVP.model.SuburbList
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.SignUpActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.orhanobut.dialogplus.DialogPlus
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignUpPage0 : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.sign_up_page0, container, false)
        try {
            var mPrefsMgr = PreferenceManager.getInstance();
            var login_lusername =
                mPrefsMgr!!.getString(AppConstant.SignUPLastName, AppConstant.EMPTY).trim()
            var login_fusername =
                mPrefsMgr!!.getString(AppConstant.SignUPFirstName, AppConstant.EMPTY).trim()

            (activity as SignUpActivity).signup_btn.visibility= View.GONE
            val signup_btn_proceed = v.findViewById<TextView>(R.id.signup_btn_proceed)
            val sign_up_tv_last_field = v.findViewById<EditText>(R.id.sign_up_tv_last_field)


            val filter =
                InputFilter { source, start, end, dest, dstart, dend ->
                    for (i in start until end) {
                        if (!Character.isLetterOrDigit(source[i])
                        ) {
                            return@InputFilter ""
                        }
                    }
                    null
                }

//            sign_up_tv_last_field.filters = arrayOf(filter)
            sign_up_tv_last_field.filters = arrayOf(
                InputFilter.AllCaps(),
                filter, InputFilter.LengthFilter(18)
            )
//            sign_up_tv_last_field.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
//            sign_up_tv_last_field.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
            signup_btn_proceed!!.setOnClickListener(View.OnClickListener {

                if(sign_up_tv_last_field.text!!.toString().length>0) {

                    (activity as SignUpActivity).ValidateEnterpriseCode(sign_up_tv_last_field.text!!.toString())
                }else{
                    if (layout_parent != null) {
                        CommonMethods.SnackBar(layout_parent, "Please enter Enterprise ID", false)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }

        return v
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

package au.com.letsjoinin.android.UI.fragment

import android.animation.Animator
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.NotificationData
import au.com.letsjoinin.android.MVP.model.NotificationRes
import au.com.letsjoinin.android.MVP.model.ResendOTPReq
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.UI.adapter.NotificationAdapter
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
class NotificationFrag : Fragment() {
    private var spruceAnimator2: Animator? = null
    // TODO: Rename and change types of parameters
    var mPrefMgr = PreferenceManager.getInstance();
    private var param1: String? = null
    private lateinit var notification_list_view: RecyclerView
    private var notificationAdapter: NotificationAdapter? = null
    private var layout_parent: RelativeLayout? = null
    private var layout_list: LinearLayout? = null
    private var txt_no_content: TextView? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var notification_shimmer_view_container: ShimmerFrameLayout? = null;
    //    val androidName = listOf<String>("Cupcake", "Donut", "Eclair", "Froyo",
//        "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "JellyBean", "Kitkat",
//        "Lollipop", "Marshmallow", "Nougat", "Oreo")as ArrayList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            if ((activity as NavigationActivity).img_back_arrow != null) {
                (activity as NavigationActivity).img_back_arrow.visibility = View.GONE
            }
//            if((activity as NavigationActivity).relative_tool_bar != null) {
//                (activity as NavigationActivity).relative_tool_bar.visibility = View.VISIBLE
//            }
        }
        if (notification_shimmer_view_container != null)
            notification_shimmer_view_container!!.startShimmerAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onPause() {
        if (notification_shimmer_view_container != null)
            notification_shimmer_view_container!!.stopShimmerAnimation()
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_notification, container, false)
        try {
            layout_parent = view.findViewById(R.id.notification_parent)
            notification_list_view = view.findViewById(R.id.notification_list_view)
            layout_list = view.findViewById(R.id.layout_list)
            txt_no_content = view.findViewById(R.id.txt_no_content)
            notification_shimmer_view_container = view.findViewById(R.id.notification_shimmer_view_container)
            notification_list_view.layoutManager = LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL, false
            ) as RecyclerView.LayoutManager?
            notification_list_view.setHasFixedSize(true)

            getListData()


        } catch (e: Exception) {
            layout_list!!.visibility = View.GONE
            txt_no_content!!.visibility = View.VISIBLE
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
            }
        }
        return view
    }

    private fun getListData() {
        if (notification_shimmer_view_container != null) {
            layout_list!!.visibility = View.GONE
            notification_shimmer_view_container!!.visibility = View.VISIBLE
            notification_shimmer_view_container!!.startShimmerAnimation()
        }

        val input: ResendOTPReq = ResendOTPReq()
        input.UserID = mPrefMgr.getString(AppConstant.USERID, AppConstant.EMPTY)
        input.UserCountry = mPrefMgr.getString(AppConstant.Country, AppConstant.EMPTY)
        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(AppConstant.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        var service: ApiService = retrofit.create(ApiService::class.java)
        val call = service.GetUserNotifications(input)
        call.enqueue(object : Callback<NotificationRes> {
            override fun onResponse(call: Call<NotificationRes>, response: Response<NotificationRes>) {
                if (activity != null) {
                    if (response.body() != null) {
                        var res = response.body()
                        if (res != null  && res.ServerMessage != null && res.ServerMessage.Status.equals(AppConstant.SUCCESS)) {
                            val items: ArrayList<NotificationData> = res.UserNotificationData

                            notificationAdapter = NotificationAdapter(items, activity!!, layout_parent!!)
                            notification_list_view.adapter = notificationAdapter
                            if (notification_shimmer_view_container != null) {

                                notification_shimmer_view_container!!.stopShimmerAnimation()
                                notification_shimmer_view_container!!.visibility = View.GONE
                                layout_list!!.visibility = View.VISIBLE
                            }
                            if(items != null && items.size ==0)
                            {
                                layout_list!!.visibility = View.GONE
                                txt_no_content!!.visibility = View.VISIBLE
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<NotificationRes>, t: Throwable) {

                if (notification_shimmer_view_container != null) {
                    layout_list!!.visibility = View.GONE
                    txt_no_content!!.visibility = View.VISIBLE
                    notification_shimmer_view_container!!.stopShimmerAnimation()
                    notification_shimmer_view_container!!.visibility = View.GONE
                }
                if (layout_parent != null) {
                    CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
                }
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
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

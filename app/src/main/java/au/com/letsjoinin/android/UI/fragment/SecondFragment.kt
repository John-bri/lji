package au.com.letsjoinin.android.UI.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.SplashActivity
import com.sackcentury.shinebuttonlib.ShineButton
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BlankFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var favourite: ShineButton? = null
    private var txt_green_top_count: TextView? = null
    private var txt_red_top_count: TextView? = null
    private var txt_yellow_top_count: TextView? = null
    private var txt_red_part_count: TextView? = null
    private var txt_green_part_count: TextView? = null
    private var txt_yellow_part_count: TextView? = null
    private var listener: OnFragmentInteractionListener? = null
    private val mHandlerCount = Handler()
    internal var delay = 400 //milliseconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private var mFabPrompt: MaterialTapTargetPrompt? = null
    var i = 0
    var ibool = false
    var jbool = false
    var j = 0
    var k = 0
    private var mUpdateUI = object : Runnable {
        override fun run() {


            if (i <= 10) {
                txt_red_part_count!!.setText(i.toString())
            }
            if (j <= 7) {
                txt_yellow_top_count!!.setText(j.toString())
            }

            if (i <= 5) {
                txt_green_part_count!!.setText(i.toString())
            }
            if (!jbool) {
                i = i + 1
            }
            if (i == 11) {
                jbool = true
            }




            if (jbool) {
                if (j == 0) {
                    showPrompt(txt_yellow_top_count!!,"G2")
                }
                j = j + 1
            }
            if (j == 8) {
                if (mFabPrompt != null) {
                    mFabPrompt!!.dismiss()
                }

                Update()
                showPrompt(favourite!!,"fav")
                Handler().postDelayed({
                    favourite!!.isChecked = true
                    favourite!!.showAnim()
                }, 500)

            } else {
                mHandlerCount.postDelayed(this, delay.toLong())
            }
        }
    }

    open fun ViewUpdate() {
        favourite!!.isChecked = false
        jbool = false
        i = 0
        j = 0
        mHandlerCount.removeCallbacks(mUpdateUI)
        mHandlerCount.post(mUpdateUI)

        Handler().postDelayed({
            showPrompt(txt_red_part_count!!,"G1")
        }, 700)

    }

    open fun Update() {
        jbool = false
        i = 0
        j = 0

        mHandlerCount.removeCallbacks(mUpdateUI)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Update()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.second_frag, container, false)
        txt_green_top_count = v.findViewById(R.id.txt_green_top_count)
        txt_red_top_count = v.findViewById(R.id.txt_red_top_count)
        txt_yellow_top_count = v.findViewById(R.id.txt_yellow_top_count)
        favourite = v.findViewById(R.id.item_favourite)

        txt_green_part_count = v.findViewById(R.id.txt_green_part_count)
        txt_red_part_count = v.findViewById(R.id.txt_red_part_count)
        txt_yellow_part_count = v.findViewById(R.id.txt_yellow_part_count)



        return v
    }

    fun showPrompt(view: View, str : String) {
        if (mFabPrompt != null) {
            mFabPrompt!!.dismiss()
            mFabPrompt = null
        }
        if (activity != null && view != null) {
            var primaryText  = "Group Count"
            var SecondaryText  = str
            if(str.equals("G2" ,true))
            {
                primaryText  = "Rating"
            } else if(str.equals("fav" ,true))
            {
                primaryText  = "Set Favourite"
                SecondaryText = ""
            }

            mFabPrompt =  MaterialTapTargetPrompt.Builder(activity as SplashActivity)
                .setPrimaryText(primaryText)
                .setSecondaryText(SecondaryText)
                .setAnimationInterpolator(FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen.max_prompt_width)
                .setTarget(view)
                .setBackgroundColour(resources.getColor(R.color.transcolorPrimary))
                .show()

        }
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
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

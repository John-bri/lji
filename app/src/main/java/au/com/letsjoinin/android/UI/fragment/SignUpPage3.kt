package com.developers.viewpager


//import android.app.Fragment
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.SignUpActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.PreferenceManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * A simple [Fragment] subclass.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignUpPage3 : Fragment() {
    var dialog: Dialog? = null
    private var Layout_loading: RelativeLayout? = null
    private var dialog_close: ImageView? = null
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var layout_signup_category_parent: LinearLayout? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    val arrayList = ArrayList<Int>()
    lateinit var layout_selected_category: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    internal var gridview: GridView? = null
    var itemList = ArrayList<SignUpCategoryList>()
    var itemListStr = ArrayList<GetAllSubCategoryDataList>()
    private val itemText = arrayOf(
        "Arts",
        "Economics",
        "Education",
        "Politics",
        "Sports",
        "Science",
        "Tourism",
        "Others",
        "Country",
        "NBA",
        "AUSTRALIA",
        "Cricket",
        "UI",
        "BasketBall",
        "Soccer",
        "Design"
    )


    override fun onResume() {
        super.onResume()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.sign_up_page3, container, false)
        try {
            gridview = v.findViewById<GridView>(R.id.gridView)
//            val sub_category_gridView = v.findViewById<GridView>(R.id.sub_category_gridView)
            val btn_categories = v.findViewById<TextView>(R.id.btn_categories)
            layout_signup_category_parent =
                v.findViewById<LinearLayout>(R.id.layout_signup_category_parent)
            layout_selected_category = v.findViewById(R.id.layout_selected_category)

            (activity as SignUpActivity).add_spinner!!.setOnClickListener {

                dialog = Dialog(activity!!)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.setCancelable(true)
                dialog!!.setContentView(R.layout.category_dialog)

                val window: Window = dialog!!.window!!
                window.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog_close = dialog!!.findViewById<View>(R.id.close) as ImageView
                back = dialog!!.findViewById<View>(R.id.imageView2) as ImageView
                back1 = dialog!!.findViewById<View>(R.id.back1) as ImageView

                list_parent = dialog!!.findViewById<View>(R.id.list_parent) as LinearLayout
                Layout_loading = dialog!!.findViewById<View>(R.id.Layout_loading) as RelativeLayout
                Layout_loading!!.bringToFront()
                val done = dialog!!.findViewById<View>(R.id.apply) as Button
                done!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        list_parent!!.removeAllViews()
                        dialog!!.dismiss()
                    }
                })
                back1!!.visibility = View.GONE
                back!!.visibility = View.VISIBLE
                back!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        list_parent!!.removeAllViews()
                        dialog!!.dismiss()
                    }
                })


                back1!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        list_parent!!.removeAllViews()
                        dialog_close!!.visibility = View.GONE
                        back1!!.visibility = View.GONE
                        back!!.visibility = View.VISIBLE
                        MainCategory()
                    }
                })

                dialog_close!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        back1!!.visibility = View.GONE
                        back!!.visibility = View.VISIBLE
                        list_parent!!.removeAllViews()
                        dialog_close!!.visibility = View.GONE
                        MainCategory()
                    }
                })
                list_parent!!.removeAllViews()
                Layout_loading!!.visibility = View.VISIBLE
                GetAllCategories()
                dialog!!.show()

            }

            btn_categories!!.setOnClickListener {

                (activity as SignUpActivity).bottomSheetBehavior!!.state =
                    BottomSheetBehavior.STATE_EXPANDED

            }
            (activity as SignUpActivity).apply!!.setOnClickListener {
                itemList.clear()
                for (i in CategoryList.indices ) {
                    val data = SignUpCategoryList()
                    data.Name = CategoryList.get(
                        i
                    ).Name

                    data.RoleCode = CategoryList.get(
                        i
                    ).SubCategoryCode

                    data.ImagePath = CategoryList.get(
                        i
                    ).ImagePath
                    itemList.add(data)
                }
                val adapter = CustomAdapter((activity as AppCompatActivity), itemList)
                gridview!!.adapter = adapter
                (activity as SignUpActivity).selected_list_parent!!.removeAllViews()
                (activity as SignUpActivity).bottomSheetBehavior!!.state =
                    BottomSheetBehavior.STATE_COLLAPSED
            }
//            sub_category_gridView.adapter = SubCategoryadapter
            var categoryPosition = 0
        } catch (e: Exception) {
            CommonMethods.SnackBar(layout_signup_category_parent, "Something went wrong", false)
        }
        return v

    }

    var list_parent: LinearLayout? = null
    var back: ImageView? = null
    var back1: ImageView? = null

    var getAllCategoryData =
        java.util.ArrayList<GetAllCategoryDataList>()

    var SelectedList =
        java.util.ArrayList<String>()
    var getAllSubCategoryData =
        java.util.ArrayList<GetAllSubCategoryDataList>()
    var CategoryList =
        java.util.ArrayList<GetAllSubCategoryDataList>()

    private fun GetAllSubCategories(
        CategoryID: String,
        StatusCode: String
    ) {
        try {
            val data = CategoryReqData()
            data.CategoryID = CategoryID
            data.StatusCode = StatusCode
            val retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ApiService::class.java)
            val call = service.GetSubCategoryList(data)
            call.enqueue(object : Callback<GetAllSubCategoryData> {
                override fun onResponse(
                    call: Call<GetAllSubCategoryData>,
                    response: Response<GetAllSubCategoryData>
                ) {
                    back1!!.visibility = View.VISIBLE
                    back!!.visibility = View.GONE
                    Layout_loading!!.visibility = View.GONE
                    getAllSubCategoryData = response.body().CategoryList
                    list_parent!!.removeAllViews()
                    for ((i, item) in getAllSubCategoryData.withIndex()) {
                        if (activity != null) {
                            val child =
                                activity!!.getLayoutInflater().inflate(
                                    R.layout.filter_child_lyt,
                                    null
                                );
                            val lay1: RelativeLayout =
                                child.findViewById(R.id.lay1)
                            lay1.visibility = View.VISIBLE
                            val lay2: RelativeLayout =
                                child.findViewById(R.id.lay2)
                            lay2.visibility = View.GONE
                            dialog_close!!.visibility = View.VISIBLE
                            val img_main_backgroud: ImageView =
                                child.findViewById(R.id.img_1)
                            val filtertxt: TextView = child.findViewById(R.id.spiner_txt_1)
                            if (item.ImagePath != null) {
                                if (activity != null) {
                                    Glide.with(activity!!)
                                        .load(item.ImagePath)
                                        .into(img_main_backgroud)
                                }
                            }
                            child.setTag(item)
                            filtertxt.text = item.Name + "-" + item.SubCategoryCode

                            child.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View?) {


                                    if (!SelectedList.contains(
                                            item.Name +"-"+ item.SubCategoryCode
                                        )
                                    ) {
                                        if(CategoryList.size < 6) {
                                            SelectedList.add( item.Name +"-"+ item.SubCategoryCode)
                                            CategoryList.add(item)
                                            (activity as SignUpActivity).signUpData.Categories.add(item)
                                            SelectedCategory()
                                        }else{
                                            CommonMethods.SnackBar(list_parent,"Maximum 5 Categories",false)
                                        }
                                    }


                                    if (dialog != null) {
                                        dialog!!.dismiss()
                                    }

                                }

                            })


                            list_parent!!.addView(child);
                        }
                    }

                }

                override fun onFailure(
                    call: Call<GetAllSubCategoryData>,
                    t: Throwable
                ) {
                }
            })
        } catch (e: java.lang.Exception) {
        }
    }

    private fun GetAllCategories() {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(ApiService::class.java)
            val call = service.GetAllCategories("ACTV")
            call.enqueue(object : Callback<GetAllCategoryData> {
                override fun onResponse(
                    call: Call<GetAllCategoryData>,
                    response: Response<GetAllCategoryData>
                ) {
                    Layout_loading!!.visibility = View.GONE
                    getAllCategoryData = response.body().CategoryList


                    MainCategory()


                }

                override fun onFailure(
                    call: Call<GetAllCategoryData>,
                    t: Throwable
                ) {
                }
            })
        } catch (e: java.lang.Exception) {
        }
    }

    fun MainCategory() {
        for ((i, item) in getAllCategoryData.withIndex()) {
            if (activity != null) {
                val child =
                    activity!!.getLayoutInflater().inflate(
                        R.layout.filter_child_lyt,
                        null
                    );
                val lay1: RelativeLayout =
                    child.findViewById(R.id.lay1)
                lay1.visibility = View.GONE
                val lay2: RelativeLayout =
                    child.findViewById(R.id.lay2)
                lay2.visibility = View.VISIBLE
                val spiner_txt_2: TextView = child.findViewById(R.id.spiner_txt_2)
                child.setTag(item)
                spiner_txt_2.text = item.Name

                child.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        Layout_loading!!.visibility = View.VISIBLE
                        GetAllSubCategories(item.id, "ACTV")

                    }

                })


                list_parent!!.addView(child);
            }
        }

    }

    fun SelectedCategory() {
        (activity as SignUpActivity).selected_list_parent!!.removeAllViews()
        for ((i, item) in CategoryList.withIndex()) {
            if (activity != null) {
                val child =
                    activity!!.getLayoutInflater().inflate(
                        R.layout.category_selected_view,
                        null
                    );
                val img_main_backgroud: ImageView =
                    child.findViewById(R.id.img_1)
                val close_1: ImageView = child.findViewById(R.id.close_1)
                val selected_meal: TextView = child.findViewById(R.id.selected_meal)
                val selected_cat: TextView = child.findViewById(R.id.selected_cat)
                if (item.ImagePath != null) {
                    if (activity != null) {
                        Glide.with(activity!!)
                            .load(item.ImagePath)
                            .into(img_main_backgroud)
                    }
                }
                child.setTag(item)

                selected_meal.text = item.SubCategoryCode
                selected_cat.text = item.CategoryCode
                close_1.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {

                        (activity as SignUpActivity).signUpData.Categories.remove(
                            item
                        )
                        SelectedList.remove(
                                item.Name +"-"+ item.SubCategoryCode
                            )
                        CategoryList.remove(item)
                        (activity as SignUpActivity).selected_list_parent!!.removeAllViews()
                        SelectedCategory()
                    }

                })


                (activity as SignUpActivity).selected_list_parent!!.addView(child);
            }
        }

    }


    fun addCategory() {
        val manager =
            PreferenceManager.getInstance()
        val from = manager.getBoolean("FromCategoryListOnDestroy", false)
        if (from) {
            manager.setBoolean("FromCategoryListOnDestroy", false)
            val gson = Gson()
            val json = manager.getString("CategoryListOnDestroy", null)
            if (json != null) {
                val CategoryList: ArrayList<GetAllSubCategoryDataList> =
                    gson.fromJson<java.util.ArrayList<GetAllSubCategoryDataList>>(
                        json,
                        object :
                            TypeToken<List<GetAllSubCategoryDataList?>?>() {}.type
                    )
                itemListStr.addAll(CategoryList)

                for (i in itemListStr.indices - 1) {
                    val data = SignUpCategoryList()
                    data.Name = itemListStr.get(
                        i
                    ).Name

                    data.RoleCode = itemListStr.get(
                        i
                    ).SubCategoryCode

                    data.ImagePath = itemListStr.get(
                        i
                    ).ImagePath
                    itemList.add(data)
                }
                val adapter = CustomAdapter((activity as AppCompatActivity), itemList)
//            val SubCategoryadapter = SubCategoryAdapter((activity as AppCompatActivity), itemList)
                gridview!!.adapter = adapter

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 190) {

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


    class CustomAdapter(context: Context, arrayList: ArrayList<SignUpCategoryList>) :
        BaseAdapter() {

        var context = context
        var arrayList = arrayList


        //Auto Generated Method
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var myView = convertView
            var holder: ViewHolder


            if (myView == null) {

                //If our View is Null than we Inflater view using Layout Inflater

                val mInflater = (context as Activity).layoutInflater

                //Inflating our grid_item.
                myView = mInflater.inflate(R.layout.signup_grid_item, parent, false)

                //Create Object of ViewHolder Class and set our View to it

                holder = ViewHolder()


                //Find view By Id For all our Widget taken in grid_item.

                /*Here !! are use for non-null asserted two prevent From null.
                 you can also use Only Safe (?.)

                */


                holder.icon =
                    myView!!.findViewById<ImageView>(R.id.grid_item_category_image) as ImageView
                holder.name =
                    myView!!.findViewById<TextView>(R.id.grid_item_category_name) as TextView
                holder.nameFullView =
                    myView!!.findViewById<TextView>(R.id.grid_item_category_name_full_view) as TextView

                //Set A Tag to Identify our view.
                myView.setTag(holder)


            } else {

                //If Our View in not Null than Just get View using Tag and pass to holder Object.

                holder = myView.getTag() as ViewHolder

            }
            val signList: SignUpCategoryList = arrayList!!.get(position)
            holder.name!!.text = signList.Name
            holder.nameFullView!!.text = signList.RoleCode
//            holder.icon!!.setImageResource(IMAGES[position])
//            if (signList.isClicked) {
//                holder.name!!.visibility = View.GONE
//                holder.nameFullView!!.visibility = View.VISIBLE
//            } else {
//                holder.name!!.visibility = View.VISIBLE
//                holder.nameFullView!!.visibility = View.GONE
//            }
            return myView

        }

        //Auto Generated Method
        override fun getItem(p0: Int): Any {
            return arrayList.get(p0)
        }

        //Auto Generated Method
        override fun getItemId(p0: Int): Long {
            return 0
        }

        //Auto Generated Method
        override fun getCount(): Int {
            return arrayList.size
        }


        //Create A class To hold over View like we taken in grid_item.xml

        class ViewHolder {

            var name: TextView? = null
            var nameFullView: TextView? = null
            var icon: ImageView? = null

        }

    }

    class SubCategoryAdapter(context: Context, arrayList: ArrayList<SignUpCategoryList>) :
        BaseAdapter() {
//        @DrawableRes
//        val IMAGES = intArrayOf(
//            R.drawable.photo_2,
//            R.drawable.photo_3,
//            R.drawable.photo_4,
//            R.drawable.photo_5,
//            R.drawable.photo_6,
//            R.drawable.photo_7,
//            R.drawable.photo_8,
//            R.drawable.photo_9,
//            R.drawable.photo_10,
//            R.drawable.photo_11,
//            R.drawable.photo_12,
//            R.drawable.photo_13,
//            R.drawable.photo_14,
//            R.drawable.photo_15,
//            R.drawable.photo_16,
//            R.drawable.photo_17,
//            R.drawable.photo_1,
//            R.drawable.photo_2,
//            R.drawable.photo_3,
//            R.drawable.photo_4,
//            R.drawable.photo_5,
//            R.drawable.photo_6,
//            R.drawable.photo_7,
//            R.drawable.photo_8,
//            R.drawable.photo_9,
//            R.drawable.photo_10,
//            R.drawable.photo_11,
//            R.drawable.photo_12,
//            R.drawable.photo_13,
//            R.drawable.photo_14,
//            R.drawable.photo_15,
//            R.drawable.photo_16,
//            R.drawable.photo_17
//        )

        //Passing Values to Local Variables
        private val itemCountry = arrayOf(
            "USA",
            "India",
            "UK",
            "China",
            "AUSTRALIA",
            "Japan",
            "Russia",
            "Iraq",
            "Isreal"
        )
        private val itemPolitics = arrayOf(
            "Fascist movements",
            "Nationalist movements",
            "Sports",
            "Defense policy",
            "AUSTRALIA",
            "Cricket",
            "UI",
            "BasketBall",
            "Soccer",
            "Design",
            "Item 12",
            "Item 13",
            "Item 14",
            "Item 15",
            "Item 16",
            "Item 17",
            "Item 18",
            "Item 19",
            "Item 20",
            "Item 21",
            "Item 22"
        )
        private val itemCricket = arrayOf(
            "Test",
            "ODI",
            "T20",
            "WorldCup",
            "IPL",
            "League",
            "Innings",
            "BasketBall",
            "Soccer",
            "Design",
            "Item 12",
            "Item 13",
            "Item 14",
            "Item 15",
            "Item 16",
            "Item 17",
            "Item 18",
            "Item 19",
            "Item 20",
            "Item 21",
            "Item 22"
        )
        var context = context
        var arrayList = arrayList

        public var categoryPosition = 0
        //Auto Generated Method
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var myView = convertView
            var holder: ViewHolder


            if (myView == null) {

                //If our View is Null than we Inflater view using Layout Inflater

                val mInflater = (context as Activity).layoutInflater

                //Inflating our grid_item.
                myView = mInflater.inflate(R.layout.signup_grid_item, parent, false)

                //Create Object of ViewHolder Class and set our View to it

                holder = ViewHolder()


                //Find view By Id For all our Widget taken in grid_item.

                /*Here !! are use for non-null asserted two prevent From null.
                 you can also use Only Safe (?.)

                */


                holder.icon =
                    myView!!.findViewById<ImageView>(R.id.grid_item_category_image) as ImageView
                holder.name =
                    myView!!.findViewById<TextView>(R.id.grid_item_category_name) as TextView
                holder.nameFullView =
                    myView!!.findViewById<TextView>(R.id.grid_item_category_name_full_view) as TextView

                //Set A Tag to Identify our view.
                myView.setTag(holder)


            } else {

                //If Our View in not Null than Just get View using Tag and pass to holder Object.

                holder = myView.getTag() as ViewHolder

            }
            val signList: SignUpCategoryList = arrayList!!.get(position)
            holder.name!!.text = signList.RoleCode
            holder.nameFullView!!.text = signList.RoleCode
            // holder.icon!!.setImageResource(IMAGES[position])
            if (signList.isClicked) {
                holder.name!!.visibility = View.GONE
                holder.nameFullView!!.visibility = View.VISIBLE
            } else {
                holder.name!!.visibility = View.VISIBLE
                holder.nameFullView!!.visibility = View.GONE
            }
            if (categoryPosition == 0) {
                holder.name!!.text = itemCountry[position]
            }
            if (categoryPosition == 1) {
                holder.name!!.text = itemPolitics[position]
            }
            if (categoryPosition == 5) {
                holder.name!!.text = itemCricket[position]
            }
            return myView

        }

        //Auto Generated Method
        override fun getItem(p0: Int): Any {
            return arrayList.get(p0)
        }

        //Auto Generated Method
        override fun getItemId(p0: Int): Long {
            return 0
        }

        //Auto Generated Method
        override fun getCount(): Int {
//            return arrayList.size
            return 6
        }


        //Create A class To hold over View like we taken in grid_item.xml

        class ViewHolder {

            var name: TextView? = null
            var nameFullView: TextView? = null
            var icon: ImageView? = null

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
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpPage3().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package au.com.letsjoinin.android.UI.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import au.com.letsjoinin.android.BuildConfig
import au.com.letsjoinin.android.MVP.interfaces.ApiService
import au.com.letsjoinin.android.MVP.model.*
import au.com.letsjoinin.android.R
import au.com.letsjoinin.android.UI.activity.NavigationActivity
import au.com.letsjoinin.android.utils.AppConstant
import au.com.letsjoinin.android.utils.CommonMethods
import au.com.letsjoinin.android.utils.Compressor
import au.com.letsjoinin.android.utils.PreferenceManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.xw.repo.BubbleSeekBar
import com.yalantis.ucrop.UCrop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddTopicFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddTopicFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddTopicFragment : Fragment() {
    private var mPrefMgr: PreferenceManager? = PreferenceManager.getInstance();
    // TODO: Rename and change types of parameters
    var dialog : Dialog? = null
    private var param1: String? = null
    private var layout_parent: RelativeLayout? = null
    private var add_spinner: RelativeLayout? = null
    private var relative_add_image: RelativeLayout? = null
    private var Layout_loading: RelativeLayout? = null
    private var topic_layout_loading: RelativeLayout? = null
    private var img_attach_layout: RelativeLayout? = null
    private var title_txt: EditText? = null
    private var desc_txt: EditText? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    internal var layoutBottomSheet: LinearLayout? = null
    private var category_spinner: Spinner? = null
    private var txt_invite: EditText? = null
    private var source_txt: EditText? = null
    private var group_spin: Spinner? = null
    private var seekbar: BubbleSeekBar? = null
    private var topic_avatar: ImageView? = null
    private var dialog_close: ImageView? = null
    private var create_topic: TextView? = null
    private var btn_categories: TextView? = null
    private var btn_add: Button? = null
    private var param2: String? = null
    private var toShowRemove: Boolean? = false
    private var isLJILibEnabled: Boolean? = false
    private var isImageAdded: Boolean? = false
    private var scrol_lyt: ScrollView? = null
    var back: ImageView? = null
    var back1: ImageView? = null

    val REQUEST_CAMERA = 1
    val REQUEST_GALLERY = 2
    // private var listener: OnFragmentInteractionListener? = null
    lateinit var currentPhotoPath: String
    var imgEncodedString: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view: View = inflater.inflate(R.layout.fragment_add_topic, container, false)
        try {
            checkPermissions()


            layoutBottomSheet = view.findViewById(R.id.bottom_sheet)
            bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)
            bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED);

            val layout_closed_group =
                view.findViewById(R.id.layout_closed_group) as RelativeLayout
            add_spinner = layoutBottomSheet!!.findViewById(R.id.add_spinner)
            selected_list_parent = layoutBottomSheet!!.findViewById<View>(R.id.selected_list_parent) as LinearLayout
           val apply = layoutBottomSheet!!.findViewById<View>(R.id.apply) as Button
            layout_parent = view.findViewById(R.id.layout_parent_rel)
            btn_categories = view.findViewById(R.id.btn_categories)
            relative_add_image = view.findViewById(R.id.relative_add_image)
            topic_layout_loading = view.findViewById(R.id.topic_layout_loading)
            txt_invite = view.findViewById(R.id.txt_invite)
            btn_add = view.findViewById(R.id.btn_add)
            img_attach_layout = view.findViewById(R.id.img_attach_layout)
            img_attach_layout!!.bringToFront()
            title_txt = view.findViewById(R.id.title_txt)
            desc_txt = view.findViewById(R.id.desc_txt)
            category_spinner = view.findViewById(R.id.category_spinner)
            source_txt = view.findViewById(R.id.source_txt)
            group_spin = view.findViewById(R.id.group_spin)
            seekbar = view.findViewById(R.id.seekbar)
            scrol_lyt = view.findViewById(R.id.scrol_lyt)
            topic_avatar = view.findViewById(R.id.topic_avatar)
            topic_avatar!!.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.colorPrimaryDark),
                android.graphics.PorterDuff.Mode.MULTIPLY
            );
            create_topic = view.findViewById(R.id.create_topic)
            topic_layout_loading!!.bringToFront()
            scrol_lyt!!.getViewTreeObserver().addOnScrollChangedListener(object :
                ViewTreeObserver.OnScrollChangedListener {
                override fun onScrollChanged() {
                    seekbar!!.correctOffsetWhenContainerOnScrolling();
                }
            })
            group_spin!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    val textView = adapterView.getChildAt(0) as TextView
                    if (i == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                    } else {
                        if (textView != null) {
                            if (textView.text.toString().equals("open", ignoreCase = true)) {
//                            GroupType = "OPEN"
                                layout_closed_group.visibility = View.GONE
                            } else {
//                            GroupType = "CLOSED"
                                layout_closed_group.visibility = View.VISIBLE
                                Toast.makeText(
                                    activity,
                                    "For Closed group, please enter friends mail id's or select Open Group",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                    }
                    (adapterView.getChildAt(0) as TextView).textSize = 13f




                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
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

            (view.findViewById(R.id.gallery) as LinearLayout).setOnClickListener {
                img_attach_layout!!.setVisibility(View.GONE)
                try {
                    galleryImageIntent()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            img_attach_layout!!.setOnClickListener {
                img_attach_layout!!.setVisibility(View.GONE)
            }
            btn_categories!!.setOnClickListener {

                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED

            }


            apply!!.setOnClickListener {

                list_parent!!.removeAllViews()
                selected_list_parent!!.removeAllViews()
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED

            }
            add_spinner!!.setOnClickListener {

             dialog = Dialog(activity!!)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.setCancelable(true)
                dialog!!.setContentView(R.layout.category_dialog)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog_close = dialog!!.findViewById<View>(R.id.close) as ImageView
                back = dialog!!.findViewById<View>(R.id.imageView2) as ImageView
                back1 = dialog!!.findViewById<View>(R.id.back1) as ImageView
                back1!!.visibility = View.GONE
                back!!.visibility = View.VISIBLE
                back1!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        list_parent!!.removeAllViews()
                        dialog_close!!.visibility = View.GONE
                        back1!!.visibility = View.GONE
                        back!!.visibility = View.VISIBLE
                        MainCategory()
                    }
                })
                val window: Window = dialog!!.window!!
                window.setLayout(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
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
                back!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        list_parent!!.removeAllViews()
                        dialog!!.dismiss()
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

            (view.findViewById(R.id.lji_lib) as LinearLayout).setOnClickListener {

                img_attach_layout!!.setVisibility(View.GONE)
                if(!isLJILibEnabled!!) {
                    CommonMethods.SnackBar(layout_parent, "Please choose a category", false)
                }else {

                    try {
                        val intent =
                            Intent(
                                activity,
                                au.com.letsjoinin.android.UI.activity.TopicLibraryImageActivity::class.java
                            )
                        intent.putExtra("CategorySKImage", CategorySK)
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            (view.findViewById(R.id.camera) as LinearLayout).setOnClickListener {
                img_attach_layout!!.setVisibility(View.GONE)
                try {
                    TakePictureIntent()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            (view.findViewById(R.id.remove) as LinearLayout).setOnClickListener {
                img_attach_layout!!.setVisibility(View.GONE)
                toShowRemove = false
                var density = context!!.getResources().getDisplayMetrics().density
                val paddingPixel = (42 * density).toInt();
                topic_avatar!!.setImageResource(R.drawable.img_camera);
                relative_add_image!!.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                topic_avatar!!.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.colorPrimaryDark),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                );
            }

            relative_add_image!!.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(v: View?) {


                    val hasAndroidPermissions = CommonMethods.hasPermissions(
                        activity!!,
                        *arrayOf<String>(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )

                    if (img_attach_layout!!.visibility == View.VISIBLE) {
                        img_attach_layout!!.visibility = View.GONE
                    } else {
                        if (hasAndroidPermissions) {
//                            if (toShowRemove!!) {
//                                (view.findViewById(R.id.remove) as LinearLayout).visibility = View.VISIBLE
//                            } else {
//                                (view.findViewById(R.id.remove) as LinearLayout).visibility = View.GONE
//                            }
                            img_attach_layout!!.visibility = View.VISIBLE
                        } else {
                            val intent = Intent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", activity!!.getPackageName(), null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }

                }
            })

            create_topic!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    if(CommonMethods.isNetworkAvailable(context)) {

                    }else{
                        if(layout_parent != null) {
                            CommonMethods.SnackBar(layout_parent,context!!.getString(R.string.network_error),false)
                        }
                    }
                    if(CommonMethods.isNetworkAvailable(context)) {
                        if (TextUtils.isEmpty(title_txt!!.text.toString().trim())) {
                            title_txt!!.requestFocus()
                            CommonMethods.SnackBar(layout_parent, "Please enter Title", false)
                            return
                        }

                        if (TextUtils.isEmpty(desc_txt!!.text.toString().trim())) {
                            desc_txt!!.requestFocus()
                            CommonMethods.SnackBar(layout_parent, "Please enter Description", false)
                            return
                        }


                        if (btn_categories!!.text.toString().trim().equals("Choose Categories", true)) {
                            category_spinner!!.requestFocus()
                            CommonMethods.SnackBar(layout_parent, "Please select Category", false)
                            return
                        }
                        if (TextUtils.isEmpty(source_txt!!.text.toString().trim())) {
                            source_txt!!.requestFocus()
                            CommonMethods.SnackBar(layout_parent, "Please enter Source", false)
                            return
                        }

                        if (group_spin!!.getSelectedItem().toString().trim().equals("Select", true)) {
                            group_spin!!.requestFocus()
                            CommonMethods.SnackBar(layout_parent, "Please select Group", false)
                            return
                        }


                        if (!isImageAdded!!) {
                            CommonMethods.SnackBar(layout_parent, "Please upload image", false)
                            return
                        }

                        var input: CreateContentData = CreateContentData()
                        input.ContentType = "TOPIC"
                        input.Title = title_txt!!.text.toString().trim()
                        input.Description = desc_txt!!.text.toString().trim()
                        input.GroupSize = seekbar!!.progress.toString()
                        input.GroupType = group_spin!!.getSelectedItem().toString();
                        input.MediaPath = imgEncodedString
                        input.PKUserCountry = mPrefMgr!!.getString(AppConstant.Country, AppConstant.EMPTY)
                        input.Source = source_txt!!.text.toString().trim()
                        input.ModifiedBy = null
                        val username = mPrefMgr!!.getString(
                            AppConstant.FirstName,
                            AppConstant.EMPTY
                        ) + " " + mPrefMgr!!.getString(AppConstant.LastName, AppConstant.EMPTY)

                        var userRef: UserRef = UserRef()

                        userRef.Name = username
                        userRef.UserID = mPrefMgr!!.getString(
                            AppConstant.USERID,
                            AppConstant.EMPTY
                        )
                        userRef.AvatarPath = mPrefMgr!!.getString(
                            AppConstant.AvatarPath,
                            AppConstant.EMPTY
                        )
                        input.CreatedBy = userRef

                        var categoryRef: CategoryRef = CategoryRef()
                        categoryRef.CategoryID = CategorySK.toString()
                        categoryRef.Name = categoryName;
                        input.Categories.add(categoryRef)

                        var EnterpriseInfo = EnterpriseInfoClass()
                        EnterpriseInfo.EnterpriseID= mPrefMgr!!.getString(
                            AppConstant.EnterpriseID,
                            AppConstant.EMPTY
                        )
                        EnterpriseInfo.PKCountry= mPrefMgr!!.getString(
                            AppConstant.EnterPrisePKCountry,
                            AppConstant.EMPTY
                        )
                        EnterpriseInfo.Name= mPrefMgr!!.getString(
                            AppConstant.EnterPriseName,
                            AppConstant.EMPTY
                        )
                        EnterpriseInfo.LogoPath=  mPrefMgr!!.getString(
                            AppConstant.EnterPriseLogoPath,
                            AppConstant.EMPTY
                        )
                        input.EnterpriseInfo = EnterpriseInfo
                        if (create_topic!!.text.toString().equals("create", true)) {
                            CreateContent(input)
                        } else {
                            input.id = mPrefMgr!!.getString(AppConstant.EditTopicContentID, AppConstant.EMPTY)
                            input.ModifiedBy = userRef
                            UpdateContent(input)
                        }
                    }else{
                        if(layout_parent != null) {
                            CommonMethods.SnackBar(layout_parent,context!!.getString(R.string.network_error),false)
                        }
                    }


                }
            })
            spinnerArray.clear()
            spinnerArray.add("Select Category")
            val OpenEditTopic = mPrefMgr!!.getBoolean(AppConstant.OpenEditTopic, false)
            var CategoryList: ArrayList<CategoryData>? = ArrayList()

            val itemText = arrayOf(
                "Arts",
                "Economics",
                "Education",
                "Politics",
                "Science",
                "Sports",
                "Tourism",
                "Others"
            )
            val itemCode = arrayOf(
                "ART",
                "ECN",
                "EDU",
                "POL",
                "SCI",
                "SPO",
                "TUR",
                "OTH"
            )

            for (i in 0..itemText.size-1) {
                val cat : CategoryData = CategoryData()
                cat.CategoryCode = itemCode[i]
                cat.CategoryDesc = itemText[i]
                CategoryList!!.add(cat)

            }
            isLJILibEnabled = false
            (view.findViewById(R.id.lji_lib) as LinearLayout).getBackground().setColorFilter(Color.parseColor("#757575"), PorterDuff.Mode.SRC_ATOP);
            if (CategoryList != null && CategoryList!!.size > 0) {
                for (i in CategoryList!!.indices) {
                    val category = CategoryList!!.get(i)
                    spinnerArray.add(category.CategoryDesc)
                    categoryHashMap.put(category.CategoryDesc, category)
                }
                val spinnerArrayAdapter = ArrayAdapter(
                    activity!!, R.layout
                        .signup_spinner_item,
                    spinnerArray
                ) //selected item will look like a spinner set from XML
//                                spinnerArrayAdapter.setDropDownViewResource(R.layout
//                                        .signup_spinner_item);
                category_spinner!!.setAdapter(spinnerArrayAdapter)
                category_spinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>, v: View, i: Int, l: Long) {
                        val textView = adapterView.getChildAt(0) as TextView
                        img_attach_layout!!.setVisibility(View.GONE)
                        toShowRemove = false
                        var density = context!!.getResources().getDisplayMetrics().density
                        val paddingPixel = (42 * density).toInt();
                        topic_avatar!!.setImageResource(R.drawable.img_camera);
                        topic_avatar!!.setScaleType(ImageView.ScaleType.CENTER);
                        relative_add_image!!.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                        topic_avatar!!.setColorFilter(
                            ContextCompat.getColor(activity!!, R.color.colorPrimaryDark),
                            android.graphics.PorterDuff.Mode.MULTIPLY
                        );
                        isImageAdded = false
                        if (i == 0) {
                            (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.hint_clr))
                        } else {
                            isLJILibEnabled = true
                            (view.findViewById(R.id.lji_lib) as LinearLayout).isEnabled = true
                            (view.findViewById(R.id.lji_lib) as LinearLayout).getBackground().setColorFilter(Color.parseColor("#592384"), PorterDuff.Mode.SRC_ATOP);
                            CategorySK = categoryHashMap[textView.text.toString()]!!.CategoryCode;
                            (adapterView.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.black))
                        }
                        (adapterView.getChildAt(0) as TextView).textSize = 13f
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {

                    }
                })
            }
            if (OpenEditTopic) {

                val ContentID = mPrefMgr!!.getString(AppConstant.EditTopicContentID, AppConstant.EMPTY)
                val PKContentType =
                    mPrefMgr!!.getString(AppConstant.EditTopicPKContentType, AppConstant.EMPTY)
                var input: GetContentByIDReq = GetContentByIDReq()
                input.ContentID = ContentID
                input.PKContentType = PKContentType
                GetContentByID(input)
                create_topic!!.text = "UPDATE"
                mPrefMgr!!.setBoolean(AppConstant.OpenEditTopic, false)
                isLJILibEnabled = true
                (view.findViewById(R.id.lji_lib) as LinearLayout).isEnabled = true
                (view.findViewById(R.id.lji_lib) as LinearLayout).getBackground().setColorFilter(Color.parseColor("#592384"), PorterDuff.Mode.SRC_ATOP);
            }




        } catch (e: Exception) {
            if (layout_parent != null) {
                // CommonMethods.SnackBar(layout_parent, getString(R.string.error), false)
            }
        }
        return view
    }

    val spinnerArray = ArrayList<String>()
    private var CategorySK: String = "OTH"
    private var categoryName: String = "OTH"
    private val categoryHashMap = HashMap<String, CategoryData>()
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onResume() {
        super.onResume()
        if ((activity as NavigationActivity).menu_title != null) {
            (activity as NavigationActivity).menu_title.setText("Add a Topic")
        }
        if (mPrefMgr!!.getBoolean(AppConstant.TopicImageSelected, false)) {
            toShowRemove = true;
            var imageURL = mPrefMgr!!.getString(AppConstant.TopicImageURL, AppConstant.EMPTY)
            imgEncodedString = imageURL
            isImageAdded = true
            if (!TextUtils.isEmpty(imageURL)) {
                var density = context!!.getResources().getDisplayMetrics().density
                val paddingPixel = (3 * density).toInt();
                relative_add_image!!.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                topic_avatar!!.setColorFilter(null)
                topic_avatar!!.setScaleType(ImageView.ScaleType.FIT_XY);
                Picasso.with(activity).load(imageURL).into(topic_avatar)
                mPrefMgr!!.setBoolean(AppConstant.TopicImageSelected, false)
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )


    protected fun checkPermissions() {
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(activity!!, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(activity!!, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }

    var list_parent: LinearLayout? = null
    var selected_list_parent: LinearLayout? = null
    var getAllCategoryData =
        java.util.ArrayList<GetAllCategoryDataList>()
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
                            dialog_close!!.visibility  = View.VISIBLE
                            val img_main_backgroud: ImageView =
                                child.findViewById(R.id.img_1)
                            val filtertxt: TextView = child.findViewById(R.id.spiner_txt_1)
                            if (item.ImagePath != null) {
                                if (activity != null) {
                                    Glide.with(activity!! )
                                        .load(item.ImagePath)
                                        .into(img_main_backgroud)
                                }
                            }
                            child.setTag(item)
                            filtertxt.text = item.Name +"-"+item.SubCategoryCode

                            child.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View?) {

//                                    bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                                    CategoryList.clear()
                                    CategoryList.add(item)
                                    categoryName = item.Name
                                    CategorySK = item.CategoryID
                                    btn_categories!!.text = categoryName
                                    SelectedCategory()

                                    if(dialog != null) {
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
         selected_list_parent!!.removeAllViews()
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
                 val line: View = child.findViewById(R.id.line)
                 line.visibility = View.GONE
                 val selected_cat: TextView = child.findViewById(R.id.selected_cat)
                 if (item.ImagePath != null) {
                     if (activity != null) {
                         Glide.with(activity!! )
                             .load(item.ImagePath)
                             .into(img_main_backgroud)
                     }
                 }
                 child.setTag(item)

                 selected_meal.text = item.SubCategoryCode
                 selected_cat.text = item.CategoryCode
                 close_1.setOnClickListener(object : View.OnClickListener {
                     override fun onClick(v: View?) {
                         btn_categories!!.text = "Choose Categories"
                         categoryName = ""
                         CategorySK =""
                         CategoryList.remove(item)
                         selected_list_parent!!.removeAllViews()
                         SelectedCategory()
                     }

                 })


                 selected_list_parent!!.addView(child);
             }
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
                    Toast.makeText(
                        activity, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG
                    ).show()
                    //                        finish();
                    return
                }
            }
        }// all permissions were granted
    }


    fun handleCropError() {


    }
    private fun GetContentByID(input: GetContentByIDReq) {
        try {
            topic_layout_loading!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)
            var mPrefMgr = PreferenceManager.getInstance();
            val call = service.GetContentByID(input!!)
            call.enqueue(object : Callback<GetContentByIdRes> {
                override fun onResponse(call: Call<GetContentByIdRes>, response: Response<GetContentByIdRes>) {
                    topic_layout_loading!!.visibility = View.GONE
                    val res = response.body()
                    if (res != null && res.ServerMessage != null && res.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {
                        imgEncodedString = res.ContentData!!.MediaPath

                        isImageAdded = true
                        title_txt!!.setText(res.ContentData!!.Title)
                        desc_txt!!.setText(res.ContentData!!.Description)
                        source_txt!!.setText(res.ContentData!!.Source)
                        seekbar!!.setProgress(res.ContentData!!.GroupSize!!.toFloat())
                        if (res.ContentData!!.GroupType.equals("Open", true)) {
                            group_spin!!.setSelection(1)
                        } else if (res.ContentData!!.GroupType.equals("closed", true)) {
                            group_spin!!.setSelection(2)
                        } else {
                            group_spin!!.setSelection(0)
                        }
                        imgEncodedString = res.ContentData!!.MediaPath
                        if (!TextUtils.isEmpty(imgEncodedString)) {
                            Picasso.with(activity).load(imgEncodedString).into(topic_avatar)
                            var density = context!!.getResources().getDisplayMetrics().density
                            val paddingPixel = (3 * density).toInt();
                            relative_add_image!!.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                            topic_avatar!!.setColorFilter(null)
                            topic_avatar!!.setScaleType(ImageView.ScaleType.FIT_XY);

                        }
                        if (spinnerArray.contains(res.ContentData!!.Categories[0].Name)) {
                            category_spinner!!.setSelection(spinnerArray.indexOf(res.ContentData!!.Categories[0].Name))
                        } else {
                            category_spinner!!.setSelection(0)
                        }
                    }
                }

                override fun onFailure(call: Call<GetContentByIdRes>, t: Throwable) {
                    topic_layout_loading!!.visibility = View.GONE
                }
            })
        } catch (e: Exception) {
            topic_layout_loading!!.visibility = View.GONE
        }
    }

    /*{"Categories":[{"CategoryID":"3","Name":"Science"}],"ContentID":"ad13f232-5d6d-452e-adc9-93b079c1576f","ContentType":"TOPIC","CreatedBy":null,"Description":"Topic","GroupSize":"1","GroupType":"Open","MediaPath":"https://letsjoinin.blob.core.windows.net/lji/d3a222ea-4f08-4e48-b5ba-1c7bb782a4eb.png","ModifiedBy":{"AvatarPath":"https://letsjoinin.blob.core.windows.net/lji/Users/7d389f86-6280-46ed-915d-3301e1cc0a77.png","Name":"John  Lji","UserID":"b9a6d965-8298-410c-b9b0-6a81285b5185"},"PKUserCountry":"AUS","Source":"Channel 8 News","Title":"Test"}*/
    private fun galleryImageIntent() {
        try {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(galleryIntent, REQUEST_GALLERY)
        } catch (e: Exception) {

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity!!.getPackageName(), null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun TakePictureIntent() {
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            activity as AppCompatActivity,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                    }
                }
            }
        } catch (e: Exception) {

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity!!.getPackageName(), null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun startCropActivity(@NonNull uri: Uri) {
        var destinationFileName = "CropImage"
        val inputFileName = "SampleCropImage_Dup.jpg"
        destinationFileName += ".jpg"
        var uCrop = UCrop.of(
            uri,
            Uri.fromFile(File(activity!!.getCacheDir(), inputFileName)),
            Uri.fromFile(File(activity!!.getCacheDir(), destinationFileName))
        )
        uCrop = basisConfig(uCrop)
        uCrop = advancedConfig(uCrop)
        UCrop.CROP_PAGE = 3
        uCrop.start(activity!!)
    }

    private fun basisConfig(@NonNull uCrop: UCrop): UCrop {
        var uCrop = uCrop
        uCrop = uCrop.withAspectRatio(16f, 9f)
        return uCrop
    }

    private fun advancedConfig(@NonNull uCrop: UCrop): UCrop {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setToolbarColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
        options.setStatusBarColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
        options.setActiveWidgetColor(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
        options.setToolbarWidgetColor(ContextCompat.getColor(activity!!, R.color.white))
        options.setTitleColor(ContextCompat.getColor(activity!!, R.color.white))
        options.setTitleColor(ContextCompat.getColor(activity!!, R.color.white))

        return uCrop.withOptions(options)
    }

    fun handleCropResult(@NonNull result: Intent) {

        try {
            val resultUri = UCrop.getOutput(result)
            if (resultUri != null) {

                object : AsyncTask<Void, Void, String>() {
                    private var result_thumbnail: Bitmap? = null

                    override fun doInBackground(vararg params: Void): String {
                        var msg = ""
                        try {
                            val file = File(resultUri.path!!)
                            val compressedImage = Compressor(activity)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                .compressToFile(file)
                            result_thumbnail = BitmapFactory.decodeFile(compressedImage.getAbsolutePath())
                            if (result_thumbnail != null) {
                                imgEncodedString =
                                    CommonMethods.encodeTobase64(result_thumbnail)
                            }
                            if (!TextUtils.isEmpty(imgEncodedString)) {
                                isImageAdded = true
                            }
                        } catch (ex: Exception) {
                            msg = "Error :" + ex.message
                        }

                        return msg
                    }

                    override fun onPostExecute(msg: String) {
                        toShowRemove = true
                        var density = context!!.getResources().getDisplayMetrics().density
                        val paddingPixel = (3 * density).toInt();
                        relative_add_image!!.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
                        topic_avatar!!.setColorFilter(null)
                        topic_avatar!!.setImageBitmap(result_thumbnail)
                        //                        File del = new File(Image_Path);
                        //                        if(!del.exists()) {
                        //                            del.delete();
                        //                        }
                    }
                }.execute(null, null, null)
            } else {
                Toast.makeText(activity, "Cannot retrive croped image", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

                val file = File(currentPhotoPath)

                startCropActivity(Uri.fromFile(file))
            } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    val contentURI = data!!.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, contentURI)
//                    val path = saveImage(bitmap)
                        val uri: Uri = CommonMethods.getImageUri(activity!!, bitmap)
                        if (uri != null) {
                            startCropActivity(uri)
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: Exception) {
            if (layout_parent != null) {
                CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
            }
        }
    }

    private fun CreateContent(input: CreateContentData) {
        try {
            topic_layout_loading!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)

            val call = service.CreateContent(input)
            call.enqueue(object : Callback<CreateTopicResult> {
                override fun onResponse(call: Call<CreateTopicResult>, response: Response<CreateTopicResult>) {
                    var res = response.body();
                    topic_layout_loading!!.visibility = View.GONE
                    if (res != null && res.ServerMessage != null) {
//                        CommonMethods.SnackBar(layout_parent, res!!.ServerMessage!!.DisplayMsg, true)
                        if (res!!.ServerMessage!!.Status.equals(AppConstant.SUCCESS)) {
//                            val fragment = ProfileDetails()
//                            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
//                            transaction.replace(R.id.container_fragment, fragment)
//                            transaction.commit()
                            val snackbar = Snackbar
                                .make(layout_parent!!, res!!.ServerMessage!!.DisplayMsg, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", View.OnClickListener {
                                    (activity as NavigationActivity).SetTabPosition(2)
                                })
                            val snackbarView = snackbar.getView()
                            val tv = snackbarView.findViewById(R.id.snackbar_text) as TextView
                            tv.maxLines = 5
                            snackbar.setActionTextColor(Color.WHITE)
                            snackbar.show()
                        }
                    } else {
                        CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<CreateTopicResult>, t: Throwable) {
                    topic_layout_loading!!.visibility = View.GONE
                    CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            topic_layout_loading!!.visibility = View.GONE
            CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
        }
    }

    private fun UpdateContent(input: CreateContentData) {
        try {
            topic_layout_loading!!.visibility = View.VISIBLE
            var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(AppConstant.BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            var service: ApiService = retrofit.create(ApiService::class.java)

            val call = service.UpdateContent(input)
            call.enqueue(object : Callback<ServerMessageData> {
                override fun onResponse(call: Call<ServerMessageData>, response: Response<ServerMessageData>) {
                    var res = response.body();
                    topic_layout_loading!!.visibility = View.GONE
                    if (res != null) {
                        if (res!!.Status.equals(AppConstant.SUCCESS)) {
                            val snackbar = Snackbar
                                .make(layout_parent!!, res!!.DisplayMsg, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", View.OnClickListener {
                                    (activity as NavigationActivity).SetTabPosition(2)
                                })
                            val snackbarView = snackbar.getView()
                            val tv = snackbarView.findViewById(R.id.snackbar_text) as TextView
                            tv.maxLines = 5
                            snackbar.setActionTextColor(Color.WHITE)
                            snackbar.show()

                        }
                    } else {
                        CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                    }
                }

                override fun onFailure(call: Call<ServerMessageData>, t: Throwable) {
                    topic_layout_loading!!.visibility = View.GONE
                    CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
                }
            })
        } catch (e: Exception) {
            topic_layout_loading!!.visibility = View.GONE
            CommonMethods.SnackBar(layout_parent, "Something went wrong", false)
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }

    override fun onDetach() {
        super.onDetach()
        //listener = null
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
         * @return A new instance of fragment AddTopicFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddTopicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

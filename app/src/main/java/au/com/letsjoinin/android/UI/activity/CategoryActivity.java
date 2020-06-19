package au.com.letsjoinin.android.UI.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import au.com.letsjoinin.android.MVP.interfaces.ApiService;
import au.com.letsjoinin.android.MVP.model.CategoryReqData;
import au.com.letsjoinin.android.MVP.model.GetAllCategoryData;
import au.com.letsjoinin.android.MVP.model.GetAllCategoryDataList;
import au.com.letsjoinin.android.MVP.model.GetAllSubCategoryData;
import au.com.letsjoinin.android.MVP.model.GetAllSubCategoryDataList;
import au.com.letsjoinin.android.R;
import au.com.letsjoinin.android.UI.adapter.MyAdapter1;
import au.com.letsjoinin.android.UI.adapter.MyAdapter2;
import au.com.letsjoinin.android.UI.adapter.MyAdapter3;
import au.com.letsjoinin.android.utils.AppConstant;
import au.com.letsjoinin.android.utils.CommonMethods;
import au.com.letsjoinin.android.utils.PreferenceManager;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryActivity extends AppCompatActivity {

    TextView txt1,apply;
    RelativeLayout lay1, lay2, lay3,Layout_loading;
    ImageView img_back_1, img_back_2, img_close;
    ArrayList<GetAllCategoryDataList> getAllCategoryData = new ArrayList<>();
    ArrayList<GetAllSubCategoryDataList> getAllSubCategoryData = new ArrayList<>();
    ArrayList<GetAllSubCategoryDataList> CategoryList = new ArrayList<>();


    private RecyclerView recyclerView1;
    private MyAdapter1 mAdapter1;
    private RecyclerView.LayoutManager layoutManager1;


    private RecyclerView recyclerView2;
    private MyAdapter2 mAdapter2;
    private RecyclerView.LayoutManager layoutManager2;


    private RecyclerView recyclerView3;
    private MyAdapter3 mAdapter3;
    private RecyclerView.LayoutManager layoutManager3;


    int width1;
    int width2;
    public boolean layB1Expand = false;
    public boolean layB2Expand = false;
    public boolean layB3Expand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        txt1 = (TextView) findViewById(R.id.txt1);
        apply = (TextView) findViewById(R.id.apply);


        Layout_loading = (RelativeLayout) findViewById(R.id.Layout_loading);
        Layout_loading.bringToFront();
        img_back_1 = (ImageView) findViewById(R.id.img_back);
        img_back_2 = (ImageView) findViewById(R.id.img_back_1);
        img_close = (ImageView) findViewById(R.id.img_back_2);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CategoryList.size() > 0) {
                    Gson gson = new Gson();
                    String json = gson.toJson(CategoryList);
                    PreferenceManager  manager =   PreferenceManager.getInstance();
                    manager.setString("CategoryListOnDestroy",json);
                    manager.setBoolean("FromCategoryListOnDestroy",true);
                    finish();
                }else{
                    CommonMethods.SnackBar(apply,"Choose one category",false);
                }
            }
        });
        img_back_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layB3Expand = false;


                img_back_1.setVisibility(View.GONE);
                txt1.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams rel_btn0 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.9f
                );
                lay1.setLayoutParams(rel_btn0);


                LinearLayout.LayoutParams rel_btn2 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.05f
                );
                lay2.setLayoutParams(rel_btn2);


                LinearLayout.LayoutParams rel_btn3 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.05f
                );
                lay3.setLayoutParams(rel_btn3);

                layB1Expand = false;
                mAdapter1 = new MyAdapter1(getAllCategoryData, CategoryActivity.this);
                mAdapter1.setHasStableIds(true);
                recyclerView1.setAdapter(mAdapter1);
                mAdapter2.notifyDataSetChanged();

//                getAllSubCategoryData.clear();
//                mAdapter2.notifyDataSetChanged();
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layB3Expand = false;
//                CategoryList.clear();
                mAdapter3.notifyDataSetChanged();
                img_back_1.setVisibility(View.VISIBLE);
                img_back_2.setVisibility(View.GONE);
                img_close.setVisibility(View.GONE);
                LinearLayout.LayoutParams rel_btn1 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.15f
                );
                lay1.setLayoutParams(rel_btn1);


                LinearLayout.LayoutParams rel_btn2 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.7f
                );
                lay2.setLayoutParams(rel_btn2);


                LinearLayout.LayoutParams rel_btn3 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.15f
                );
                lay3.setLayoutParams(rel_btn3);
                layB1Expand = true;
                mAdapter1.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
            }
        });
        img_back_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layB3Expand = false;
//                CategoryList.clear();
                mAdapter3.notifyDataSetChanged();
                img_back_2.setVisibility(View.GONE);
                img_close.setVisibility(View.GONE);
                img_back_1.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams rel_btn1 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.15f
                );
                lay1.setLayoutParams(rel_btn1);


                LinearLayout.LayoutParams rel_btn2 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.7f
                );
                lay2.setLayoutParams(rel_btn2);


                LinearLayout.LayoutParams rel_btn3 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.15f
                );
                lay3.setLayoutParams(rel_btn3);
                layB1Expand = true;
                mAdapter1.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
            }
        });

        img_back_1.setVisibility(View.GONE);
        img_back_2.setVisibility(View.GONE);
        img_close.setVisibility(View.GONE);

        lay1 = (RelativeLayout) findViewById(R.id.lay1);
        lay2 = (RelativeLayout) findViewById(R.id.lay2);
        lay3 = (RelativeLayout) findViewById(R.id.lay3);


        recyclerView1 = (RecyclerView) findViewById(R.id.list1);
        recyclerView2 = (RecyclerView) findViewById(R.id.list2);
        recyclerView3 = (RecyclerView) findViewById(R.id.list3);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView1.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView3.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager1 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager2 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager3 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView3.setLayoutManager(layoutManager3);
        recyclerView1.addItemDecoration(new DividerItemDecoration(recyclerView1.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView2.addItemDecoration(new DividerItemDecoration(recyclerView2.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView3.addItemDecoration(new DividerItemDecoration(recyclerView3.getContext(), DividerItemDecoration.VERTICAL));
        // specify an adapter (see also next example)


        mAdapter3 = new MyAdapter3(CategoryList, this);
        mAdapter3.setHasStableIds(true);
        recyclerView3.setAdapter(mAdapter3);

        width1 = width - convertPixelsToDp(40, CategoryActivity.this);
        width2 = width1 - convertPixelsToDp(40, CategoryActivity.this);

//        ViewGroup.LayoutParams rel_btn0 = lay1.getLayoutParams();
//        rel_btn0.width = width1;
//        lay1.setLayoutParams(rel_btn0);

        LinearLayout.LayoutParams rel_btn0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        lay1.setLayoutParams(rel_btn0);

        lay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!layB3Expand) {
                    if (CategoryList.size() > 0) {
                        txt1.setVisibility(View.GONE);
                        img_back_2.setVisibility(View.VISIBLE);
                        img_close.setVisibility(View.VISIBLE);

                        layB3Expand = true;
                        LinearLayout.LayoutParams rel_btn1 = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                0f
                        );
                        lay1.setLayoutParams(rel_btn1);

                        LinearLayout.LayoutParams rel_btn2 = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1.0f
                        );
                        lay2.setLayoutParams(rel_btn2);

                        LinearLayout.LayoutParams rel_btn3 = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1.0f
                        );
                        lay3.setLayoutParams(rel_btn3);


                        mAdapter3.notifyDataSetChanged();
                        mAdapter2.notifyDataSetChanged();
                    }
                }
            }
        });


        GetAllCategories();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void GetAllCategories() {
        try {
            Layout_loading.setVisibility(View.VISIBLE);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstant.BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService service = retrofit.create(ApiService.class);
            Call<GetAllCategoryData> call = service.GetAllCategories("ACTV");
            call.enqueue(new retrofit2.Callback<GetAllCategoryData>() {
                @Override
                public void onResponse(Call<GetAllCategoryData> call, Response<GetAllCategoryData> response) {
                    getAllCategoryData = response.body().CategoryList;
                    mAdapter1 = new MyAdapter1(getAllCategoryData, CategoryActivity.this);
                    mAdapter1.setHasStableIds(true);
                    recyclerView1.setAdapter(mAdapter1);
                    Layout_loading.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<GetAllCategoryData> call, Throwable t) {

                }
            });


        } catch (Exception e) {
        }
    }

    public void callSubCategory(String id) {

        img_back_1.setVisibility(View.VISIBLE);
        txt1.setVisibility(View.GONE);
        if(!layB1Expand) {

            if (CategoryList.size() > 0) {
                LinearLayout.LayoutParams rel_btn1 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.15f
                );
                lay1.setLayoutParams(rel_btn1);


                LinearLayout.LayoutParams rel_btn2 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.7f
                );
                lay2.setLayoutParams(rel_btn2);


                LinearLayout.LayoutParams rel_btn3 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.15f
                );
                lay3.setLayoutParams(rel_btn3);
            }else{
                LinearLayout.LayoutParams rel_btn1 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.2f
                );
                lay1.setLayoutParams(rel_btn1);


                LinearLayout.LayoutParams rel_btn2 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.7f
                );
                lay2.setLayoutParams(rel_btn2);


                LinearLayout.LayoutParams rel_btn3 = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.1f
                );
                lay3.setLayoutParams(rel_btn3);
            }
            layB1Expand = true;
        }
        mAdapter1.notifyDataSetChanged();
        GetAllSubCategories(id, "ACTV");

    }
    public void addCategory(GetAllSubCategoryDataList item) {

                    boolean isHave = false;
                    for(GetAllSubCategoryDataList list : CategoryList)
                    {
                        if(list.CategoryID.equals(item.CategoryID)){
                            isHave = true;
                            if(!list.Name.equals(item.Name))
                            {
                                isHave = false;
                            }else{
                                isHave = true;
                                break;
                            }
                        }
                    }
                    if(!isHave) {
                        CategoryList.add(item);
                        mAdapter3.notifyDataSetChanged();
                    }


                if(!layB3Expand) {
                    LinearLayout.LayoutParams rel_btn1 = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0.15f
                    );
                    lay1.setLayoutParams(rel_btn1);


                    LinearLayout.LayoutParams rel_btn2 = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0.7f
                    );
                    lay2.setLayoutParams(rel_btn2);


                    LinearLayout.LayoutParams rel_btn3 = new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0.15f
                    );
                    lay3.setLayoutParams(rel_btn3);
                }

    }

    private void GetAllSubCategories(String CategoryID, String StatusCode) {
        try {
            Layout_loading.setVisibility(View.VISIBLE);
            CategoryReqData data = new CategoryReqData();
            data.CategoryID = CategoryID;
            data.StatusCode = StatusCode;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstant.BaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService service = retrofit.create(ApiService.class);
            Call<GetAllSubCategoryData> call = service.GetSubCategoryList(data);
            call.enqueue(new retrofit2.Callback<GetAllSubCategoryData>() {
                @Override
                public void onResponse(Call<GetAllSubCategoryData> call, Response<GetAllSubCategoryData> response) {

                    getAllSubCategoryData = response.body().CategoryList;
                    mAdapter2 = new MyAdapter2(getAllSubCategoryData, CategoryActivity.this);
                    mAdapter2.setHasStableIds(true);
                    recyclerView2.setAdapter(mAdapter2);
                    Layout_loading.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<GetAllSubCategoryData> call, Throwable t) {

                }
            });


        } catch (Exception e) {
        }
    }

    public static int convertDpToPixel(float dp, Context context) {
        return (int) dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, Context context) {
        return (int) px / (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


}

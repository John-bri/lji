package au.com.letsjoinin.android.UI.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

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

public class CategoryActivity2 extends AppCompatActivity {

    ArrayList<GetAllCategoryDataList> getAllCategoryData = new ArrayList<>();
    ArrayList<GetAllSubCategoryDataList> getAllSubCategoryData = new ArrayList<>();
    ArrayList<GetAllSubCategoryDataList> CategoryList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_dialog);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;




        GetAllCategories();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void GetAllCategories() {
        try {
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
                }

                @Override
                public void onFailure(Call<GetAllCategoryData> call, Throwable t) {

                }
            });


        } catch (Exception e) {
        }
    }


    private void GetAllSubCategories(String CategoryID, String StatusCode) {
        try {
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

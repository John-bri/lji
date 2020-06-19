package au.com.letsjoinin.android.MVP.model;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 */
public class CategoryData {

    public String CategoryCode, CategoryDesc;
    public long CategorySK;
    @Override
    public String toString() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}
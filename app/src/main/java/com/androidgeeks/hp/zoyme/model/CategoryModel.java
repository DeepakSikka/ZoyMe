package com.androidgeeks.hp.zoyme.model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Sikka on 11/10/2017.
 */


public class CategoryModel {
    private String id;
    private String name;
    private String description;

    public int getmSetBackGround() {
        return mSetBackGround;
    }

    public void setmSetBackGround(int mSetBackGround) {
        this.mSetBackGround = mSetBackGround;
    }

    private int mSetBackGround;


    List<SubCategoryModel> mSubcategoryModel = new ArrayList<>();

    public CategoryModel(){

    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    private String image_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SubCategoryModel> getSubCategory() {
        return mSubcategoryModel;
    }

    public void setSubCategory(List<SubCategoryModel> questionType) {
        mSubcategoryModel = questionType;
    }


    public static class SubCategoryModel {

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        private String id;
        private String name;
        private String image_url;




        public SubCategoryModel() {

        }

        public SubCategoryModel(String id, String name, String image_url) {
            this.id = id;
            this.name = name;
            this.image_url = image_url;
        }


    }

}



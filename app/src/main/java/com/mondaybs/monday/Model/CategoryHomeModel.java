package com.mondaybs.monday.Model;

public class CategoryHomeModel {

    private String CategoryImg;
    private String CategoryName;

    public CategoryHomeModel(String categoryImg, String categoryName) {
        CategoryImg = categoryImg;
        CategoryName = categoryName;
    }

    public String getCategoryImg() {
        return CategoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        CategoryImg = categoryImg;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}

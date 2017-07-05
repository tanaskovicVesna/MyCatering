package com.vesna.tanaskovic.scatering.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Tanaskovic on 6/18/2017.
 */
@DatabaseTable(tableName = Product.TABLE_NAME_USERS)
public class Product {
    public static final String TABLE_NAME_USERS = "products";

    public static final String FIELD_NAME_ID     = "id";
    public static final String FIELD_NAME_NAME   = "name";
    public static final String FIELD_NAME_DESCRIPTION   = "description";
    public static final String FIELD_NAME_IMAGE  = "image";
    public static final String FIELD_NAME_CATEGORY = "category";
    public static final String FIELD_NAME_PRICE = "price";


    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String mName;

    @DatabaseField(columnName = FIELD_NAME_DESCRIPTION)
    private String description;

    @DatabaseField(columnName = FIELD_NAME_IMAGE)
    private String image;

    @DatabaseField(columnName = FIELD_NAME_PRICE)
    private float price;

    @DatabaseField(columnName = FIELD_NAME_CATEGORY, foreign = true, foreignAutoCreate = true,foreignAutoRefresh = true)
    private Category category;

    //ORMLite data base requires an empty constructor
    public Product() {
    }

    /** Getters & Setters **/

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getPrice() {return price;}

    public void setPrice(float price) {this.price = price;}


    @Override
    public String toString() {
        return  mName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}

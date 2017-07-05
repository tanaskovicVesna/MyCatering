package com.vesna.tanaskovic.scatering.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Tanaskovic on 6/18/2017.
 */
@DatabaseTable(tableName = Category.TABLE_NAME_USERS)
public class Category {


    public static final String TABLE_NAME_USERS = "categories";
    public static final String FIELD_NAME_ID     = "id";
    public static final String FIELD_NAME_NAME   = "name";

    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String name;

    //ORMLite data base requires an empty constructor
    public Category() {
    }

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {return id;}

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}

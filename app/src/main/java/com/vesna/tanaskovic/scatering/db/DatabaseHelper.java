package com.vesna.tanaskovic.scatering.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vesna.tanaskovic.scatering.db.model.Category;
import com.vesna.tanaskovic.scatering.db.model.Product;

import java.sql.SQLException;

/**
 * Created by Tanaskovic on 6/18/2017.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {


    private static final String DATABASE_NAME    = "ormlite.db";

    //Data base starts version. Usually starts from 1
    private static final int    DATABASE_VERSION = 1;

    private Dao<Product, Integer> mProductDao = null;
    private Dao<Category, Integer> mCategoryDao = null;

    //For correct initialization of the library, it is necessary to add a constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //When creating a database, it is necessary to call the appropriate methods of the library
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            //When creating a database, it is necessary to call TableUtils.createTable for every existing table
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, Product.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //When changing the contents of the tables, it is necessary to call TableUtils.dropTable for every existing table
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Category.class, true);
            TableUtils.dropTable(connectionSource, Product.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //One Dao object that provides access to the data base
   // If there are several tables, it is necessary to create an Dao object for every table
    public Dao<Product, Integer> getProductDao() throws SQLException {
        if (mProductDao == null) {
            mProductDao = getDao(Product.class);
        }

        return mProductDao;
    }

    public Dao<Category, Integer> getCategoryDao() throws SQLException {
        if (mCategoryDao == null) {
            mCategoryDao = getDao(Category.class);
        }

        return mCategoryDao;
    }

    //Necessarily release resources after finishing communication with database
    @Override
    public void close() {
        mProductDao = null;
        mCategoryDao = null;

        super.close();
    }
}

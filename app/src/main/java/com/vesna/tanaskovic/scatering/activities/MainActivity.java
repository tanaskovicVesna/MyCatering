package com.vesna.tanaskovic.scatering.activities;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.vesna.tanaskovic.scatering.R;
import com.vesna.tanaskovic.scatering.adapters.DrawerListAdapter;
import com.vesna.tanaskovic.scatering.db.DatabaseHelper;
import com.vesna.tanaskovic.scatering.db.model.Category;
import com.vesna.tanaskovic.scatering.db.model.Product;
import com.vesna.tanaskovic.scatering.dialogs.AboutDialog;
import com.vesna.tanaskovic.scatering.fragments.DetailFragment;
import com.vesna.tanaskovic.scatering.fragments.ListFragment;
import com.vesna.tanaskovic.scatering.fragments.ListFragment.OnProductSelectedListener;
import com.vesna.tanaskovic.scatering.model.NavigationItem;
import com.vesna.tanaskovic.scatering.preferences.PrefsActivity;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnProductSelectedListener {

    private SharedPreferences prefs;
    private static int   NOTIFICATION_ID = 1;
    //keys of toast and  statusbar notification
    public static String NOTIF_TOAST = "notif_toast";
    public static String NOTIF_STATUS = "notif_status";


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;
    private CharSequence drawerTitle;
    private CharSequence title;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();

    private boolean landscapeMode = false;
    private boolean listShown = false;
    private boolean detailShown = false;

    private int productId;
    private DatabaseHelper databaseHelper;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // draws navigation items
        navigationItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.drawer_home_long), R.drawable.ic_action_product));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_Settings_long), R.drawable.ic_action_settings));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about));

        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);

        // populate the Navigtion Drawer with options
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ListFragment listFragment = new ListFragment();
            ft.add(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            selectItemFromDrawer(0);
        }

        listShown = true;
        detailShown = false;
        productId = 0;

        //settings data is stored in SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        addInitCateogry();

    }

    //method that sets category types that user can choose
    public void addInitCateogry(){
        try {
            if (getDatabaseHelper().getCategoryDao().queryForAll().size() == 0){
                Category other= new Category();
                other.setName(getString(R.string.other));

                Category salty = new Category();
                salty.setName(getString(R.string.category_type1));

                Category sweety = new Category();
                sweety.setName(getString(R.string.category_type2));

                getDatabaseHelper().getCategoryDao().create(other);
                getDatabaseHelper().getCategoryDao().create(salty);
                getDatabaseHelper().getCategoryDao().create(sweety);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_add:

                try {
                    addItem();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //method that provides notificatin and toast message
    private void showMessage(String message){
        //check  Application settings
        boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
        boolean status = prefs.getBoolean(NOTIF_STATUS, false);

        if (toast){Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();}
        if (status){showStatusMessage(message);}
    }



   private void showStatusMessage(String message){
        // creates notification with the notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        builder
                .setContentTitle(getString(R.string.drawer_home))
                .setContentText(message)
                .setLargeIcon(bm)
                .setSmallIcon(R.drawable.ic_status_icon);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

   }

   // method adds  data to the data base by making object of the class that represents data table
   // and populate  data table with data
    private void addItem() throws SQLException {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_product);

        //------------image--------------
        final Spinner imagesSpinner = (Spinner) dialog.findViewById(R.id.product_image);
        // Load images from array resource
        String[] imagesList = getResources().getStringArray(R.array.images);
        ArrayAdapter<String> imagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, imagesList);
        imagesSpinner.setAdapter(imagesAdapter);
        imagesSpinner.setSelection(0);

        //-------------category--------------
        final Spinner productsSpinner = (Spinner) dialog.findViewById(R.id.product_category);
        List<Category> list = getDatabaseHelper().getCategoryDao().queryForAll();
        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        productsSpinner.setAdapter(dataAdapter);
        productsSpinner.setSelection(0);

        //-------------name--------------------
        final EditText productName =  (EditText) dialog.findViewById(R.id.product_name);
        //filter allows that max 40 characters can be entered in editName field
        InputFilter[] fa= new InputFilter[1];
        fa[0] = new InputFilter.LengthFilter(40);
        productName.setFilters(fa);

        //------------description--------------
        final EditText productDescr = (EditText) dialog.findViewById(R.id.product_description);
        productDescr.setScroller(new Scroller(this));
        productDescr.setVerticalScrollBarEnabled(true);
        productDescr.setMovementMethod(new ScrollingMovementMethod());

        //------------price--------------------
        final EditText productPrice = (EditText) dialog.findViewById(R.id.product_price);

        // filter which only allows max 5 digits before the decimal point and max 1 digit after that
        //doesn't allow that entered value begins with 0
        InputFilter filter = new InputFilter() {
            final int maxDigitsBeforeDecimalPoint=5;
            final int maxDigitsAfterDecimalPoint=1;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder(dest);
                builder.replace(dstart, dend, source
                        .subSequence(start, end).toString());
                if (!builder.toString().matches(
                        "(([1-9]{1})([0-9]{0,"+(maxDigitsBeforeDecimalPoint-1)+"})?)?(\\.[0-9]{0,"+maxDigitsAfterDecimalPoint+"})?"

                )) {
                    if(source.length()==0)
                        return dest.subSequence(dstart, dend);
                    return "";
                }
                return null;
            }
        };
        productPrice.setFilters(new InputFilter[] { filter });
        //---------------------------------------------------------------

        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validates productPrice  field which has to be filled with certain value
                if (productPrice.getText().toString().trim().length() == 0 || productPrice.getText().toString().equalsIgnoreCase(".") ) {
                    productPrice.setError(getString(R.string.exception_price));
                    return;
                }

                String name = productName.getText().toString();
                float price = Float.parseFloat(productPrice.getText().toString());
                String desct = productDescr.getText().toString();
                Category categoty = (Category) productsSpinner.getSelectedItem();
                String image = (String) imagesSpinner.getSelectedItem();

                //validates productName field which has to be filled with certain value
                if(TextUtils.isEmpty(name)) {
                    productName.setError(getString(R.string.exception_name));
                    return;
                }

                Product product = new Product();
                product.setmName(name);
                product.setPrice(price);
                product.setDescription(desct);
                product.setImage(image);
                product.setCategory(categoty);

                try {
                    getDatabaseHelper().getProductDao().create(product);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //shows new created product by pulling data from tha data base and populate list
                refresh();
                //Toast and notification message
                showMessage(getString(R.string.add_item));

                dialog.dismiss();
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0){
            if (detailShown == true){ onBackPressed();
            }
        } else if (position == 1){
            Intent settings = new Intent(MainActivity.this, PrefsActivity.class);
            startActivity(settings);
        } else if (position == 2){
            if (dialog == null){
                dialog = new AboutDialog(MainActivity.this).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            dialog.show();
        }
        drawerList.setItemChecked(position, true);
        setTitle(navigationItems.get(position).getTitle());
        drawerLayout.closeDrawer(drawerPane);
    }

    @Override
    public void onProductSelected(int id) {
        productId=id;
        try {
            Product product = getDatabaseHelper().getProductDao().queryForId(id);
            if (landscapeMode) {
            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
            detailFragment.updateProduct(product);
        } else {
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setProduct(product);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, detailFragment, "Detail_Fragment2");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack("Detail_Fragment2");
            ft.commit();
            listShown = false;
            detailShown = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (landscapeMode) {
            openDialogFinish();
        } else if (listShown == true) {
            openDialogFinish();
        } else if (detailShown == true) {
            getFragmentManager().popBackStack();
            ListFragment listFragment = new ListFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            listShown = true;
            detailShown = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    //method shows new data by pulling data from tha data base and populate list
    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.products);
        if (listview != null){
            ArrayAdapter<Product> adapter = (ArrayAdapter<Product>) listview.getAdapter();
            if(adapter!= null)
            {
                try {
                    adapter.clear();
                    List<Product> list = getDatabaseHelper().getProductDao().queryForAll();

                    adapter.addAll(list);

                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //method that communicates with the data base
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    //method shows dialog to confirm closing app
    public void openDialogFinish(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setNegativeButton(R.string.dialog_about_no, null)
                .setPositiveButton(R.string.dialog_about_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // release resources after finishing communication with database
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}

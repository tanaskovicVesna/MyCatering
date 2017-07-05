package com.vesna.tanaskovic.scatering.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.vesna.tanaskovic.scatering.R;
import com.vesna.tanaskovic.scatering.activities.MainActivity;
import com.vesna.tanaskovic.scatering.db.DatabaseHelper;
import com.vesna.tanaskovic.scatering.db.model.Category;
import com.vesna.tanaskovic.scatering.db.model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;



/**
 * Created by Tanaskovic on 6/16/2017.
 */

public class DetailFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private SharedPreferences prefs;
    private static int   NOTIFICATION_ID = 1;

    public static String NOTIF_TOAST = "notif_toast";
    public static String NOTIF_STATUS = "notif_status";

    private Product product = null;

    private TextView description;
    private DatabaseHelper databaseHelper;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("DetailFragment", "onCreateView()");

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(product.getmName());

        TextView price= (TextView) view.findViewById(R.id.price);
        price.setText(Float.toString(product.getPrice()));

        description = (TextView) view.findViewById(R.id.description);
        //allows scrolling the text of description in TextView, using appendTextAndScroll() method
        description.setMovementMethod(new ScrollingMovementMethod());
        appendTextAndScroll(product.getDescription());

        TextView category = (TextView) view.findViewById(R.id.category);
        category.setText(product.getCategory().getName());


        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        InputStream is = null;
        try {
            is = getActivity().getAssets().open(product.getImage());
            Drawable drawable = Drawable.createFromStream(is, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.buy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates notification with the notification builder
                showMessage(getString(R.string.ordered_item));
            }
        });

        return view;
    }

   //method that appends text to the TextView
    private void appendTextAndScroll(String text)
    {
        if(description != null){
            description.append(text + "\n");
            final Layout layout = description.getLayout();
            if(layout != null){
                int scrollDelta = layout.getLineBottom(description.getLineCount() - 1)
                        - description.getScrollY() - description.getHeight();
                if(scrollDelta > 0)
                    description.scrollBy(0, scrollDelta);
            }
        }
    }

    private void showMessage(String message){
        //settings data is stored in SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //check  Application settings
        boolean toast = prefs.getBoolean(NOTIF_TOAST, false);
        boolean status = prefs.getBoolean(NOTIF_STATUS, false);

        if (toast){Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();}
        if (status){showStatusMessage(message);}
    }
    private void showStatusMessage(String message){
        // creates notification with the notification builder
        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(getActivity());
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        builder
                .setContentTitle(getString(R.string.drawer_home))
                .setContentText(message)
                .setLargeIcon(bm)
                .setSmallIcon(R.drawable.ic_status_icon);
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    public void setProduct(Product product ) {
               this.product = product;
           }


    public void updateProduct(Product product) {
        this.product = product;

        TextView name = (TextView) getActivity().findViewById(R.id.name);
        name.setText(product.getmName());

        TextView price= (TextView) getActivity().findViewById(R.id.price);
        price.setText(Float.toString(product.getPrice()));

        TextView description = (TextView) getActivity().findViewById(R.id.description);
        description.setText(product.getDescription());

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.image);
        InputStream is = null;
        try {
            is = getActivity().getAssets().open(product.getImage());
            Drawable drawable = Drawable.createFromStream(is, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.detail_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // elements are added on fragment to enable item deleting and editing
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove:
                try {
                    removeItem();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.edit:
                try {
                    editItem();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
    }

        return super.onOptionsItemSelected(item);
    }

    //method removes data from data base by removing object of the class that represents data table
    //and shows dialog to confirm deleting item
    private void removeItem() throws SQLException {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_delete)
                .setMessage(R.string.dialog_message_delete)
                .setNegativeButton(R.string.dialog_about_no, null)
                .setPositiveButton(R.string.dialog_about_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (product != null) { //delete item
                                ((MainActivity) getActivity()).getDatabaseHelper().getProductDao().delete(product);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        showMessage(getString(R.string.item_delete));

                        getActivity().onBackPressed();
                    }
                })
                .show();
    }

    //get index value of spinners field which should be edited
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

     /*method edits data from data base by geting object of the class that represents data table
     and populate  dialog fields with edited data*/
     private void editItem() throws SQLException {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_product);

        //------------image--------------
        final Spinner imagesSpinner = (Spinner) dialog.findViewById(R.id.product_edit_image);
        // Load images from array resource
        String[] imagesList = getResources().getStringArray(R.array.images);
        ArrayAdapter<String> imagesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, imagesList);
        imagesSpinner.setAdapter(imagesAdapter);
         //populate spinner field with edited  image, using getIndex() method
        imagesSpinner.setSelection(getIndex(imagesSpinner,product.getImage()));


        //-------------category--------------
        final Spinner productsSpinner = (Spinner) dialog.findViewById(R.id.product_edit_category);
        List<Category> list = null;
        try {
            list = ((MainActivity) getActivity()).getDatabaseHelper().getCategoryDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayAdapter<Category> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        productsSpinner.setAdapter(dataAdapter);
         //populate spinner field with edited  category value , using getIndex() method
        productsSpinner.setSelection(getIndex(productsSpinner,product.getCategory().getName()));


         //-------------name--------------------
        final EditText editName =  (EditText) dialog.findViewById(R.id.product_edit_name);
         //filter allows that max 40 characters can be entered in editName field
        InputFilter[] fa= new InputFilter[1];
        fa[0] = new InputFilter.LengthFilter(40);
        editName.setFilters(fa);


         //-----------description----------------
        final EditText editDescr = (EditText) dialog.findViewById(R.id.product_edit_description);
         //allows scrolling the text of editDescr field
        editDescr.setScroller(new Scroller(getActivity()));
        editDescr.setVerticalScrollBarEnabled(true);
        editDescr.setMovementMethod(new ScrollingMovementMethod());


        //--------------price--------------------
         final EditText editPrice = (EditText) dialog.findViewById(R.id.product_edit_price);

         editPrice.setText(Float.toString(product.getPrice()));
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

         editPrice.setFilters(new InputFilter[] { filter });

        //-----------------------------------------------------------

        editName.setText(product.getmName());
        editDescr.setText(product.getDescription());

        Button edit = (Button) dialog.findViewById(R.id.edit_ok);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validates editPrice field which has to be filled with certain value
                if (editPrice.getText().toString().trim().length() == 0 || editPrice.getText().toString().equalsIgnoreCase(".") ) {
                    editPrice.setError(getString(R.string.exception_price));
                    return;
                }

                String name = editName.getText().toString();
                Float  price =  Float.parseFloat(editPrice.getText().toString());
                String desct = editDescr.getText().toString();
                Category categoty = (Category) productsSpinner.getSelectedItem();
                String image = (String) imagesSpinner.getSelectedItem();

                //validates editName field which has to be filled with certain value
                if(TextUtils.isEmpty(name)) {
                    editName.setError(getString(R.string.exception_name));
                    return;
                }

                product.setmName(name);
                product.setPrice(price);
                product.setDescription(desct);
                product.setImage(image);
                product.setCategory(categoty);

                try {
                    getDatabaseHelper().getProductDao().update(product);
                    getDatabaseHelper().getCategoryDao().update(categoty);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                showMessage(getString(R.string.item_edit));

                dialog.dismiss();
            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    //method that communicates with the data base
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // release resources after finishing communication with  database
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

}



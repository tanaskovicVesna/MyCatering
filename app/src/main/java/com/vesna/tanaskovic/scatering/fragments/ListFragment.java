package com.vesna.tanaskovic.scatering.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.vesna.tanaskovic.scatering.R;
import com.vesna.tanaskovic.scatering.db.DatabaseHelper;
import com.vesna.tanaskovic.scatering.db.model.Product;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Tanaskovic on 6/16/2017.
 */

@CoordinatorLayout.DefaultBehavior(FloatingActionButton.Behavior.class)
public class ListFragment extends Fragment {

    private DatabaseHelper databaseHelper;

    // Container Activity must implement this interface
    public interface OnProductSelectedListener {
        void onProductSelected(int id);
    }

    OnProductSelectedListener listener;
    ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            List<Product> list = getDatabaseHelper().getProductDao().queryForAll();

            adapter = new ArrayAdapter<Product>(getActivity(), R.layout.list_item, list);

            final ListView listView = (ListView)getActivity().findViewById(R.id.products);

            // Assign adapter to ListView
            listView.setAdapter(adapter);
            //Get item from the list to get his key
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Product p = (Product) listView.getItemAtPosition(position);

                    listener.onProductSelected(p.getmId());
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.list_fragment, container, false);

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sanjaratkov/"));
                startActivity(i);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnProductSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProductSelectedListener");
        }
    }


    //Method that communicates with the data base
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
}


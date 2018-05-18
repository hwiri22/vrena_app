package com.vrena.vrena_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard 'menu' of available sampler activities
 */
public class Activity_Dashboard extends FragmentActivity
{
    protected ListAdapter mAdapter;
    protected ListView mList;

    //Initialize the list
    @SuppressWarnings("serial") //Suppress warnings about hash maps not having custom UIDs
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            Log.i("ANT+ Plugin Sampler", "Version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (NameNotFoundException e)
        {
            Log.i("ANT+ Plugin Sampler", "Version: " + e.toString());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        List<Map<String,String>> menuItems = new ArrayList<Map<String,String>>();
        menuItems.add(new HashMap<String,String>(){{put("title","Bike Cadence Display");put("desc","Receive from Bike Cadence sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Bike Speed and Distance Display");put("desc","Receive from Bike Speed sensors");}});
        menuItems.add(new HashMap<String,String>(){{put("title","Launch ANT+ Plugin Manager");put("desc","Controls device database and default settings");}});

        SimpleAdapter adapter = new SimpleAdapter(this, menuItems, android.R.layout.simple_list_item_2, new String[]{"title","desc"}, new int[]{android.R.id.text1,android.R.id.text2});
        setListAdapter(adapter);

    }

    //Launch the appropriate activity/action when a selection is made
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        int j=0;
        if(position == j++)
        {
            Intent i = new Intent(this, Activity_BikeCadenceSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            Intent i = new Intent(this, Activity_BikeSpeedDistanceSampler.class);
            startActivity(i);
        }
        else if(position == j++)
        {
            /**
             * Launches the ANT+ Plugin Manager. The ANT+ Plugin Manager provides access to view and modify devices
             * saved in the plugin device database and control default plugin settings. It is also available as a
             * stand alone application, but the ability to launch it from your own application is useful in situations
             * where a user wants extra convenience or doesn't already have the stand alone launcher installed. For example,
             * you could place this launch command in your application's own settings menu.
             */
            if(!AntPluginPcc.startPluginManagerActivity(this))
            {
                AlertDialog.Builder adlgBldr = new AlertDialog.Builder(this);
                adlgBldr.setTitle("Missing Dependency");
                adlgBldr.setMessage("This application requires the ANT+ Plugins, would you like to install them?");
                adlgBldr.setCancelable(true);
                adlgBldr.setPositiveButton("Go to Store", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent startStore = null;
                        startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.dsi.ant.plugins.antplus"));
                        startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Activity_Dashboard.this.startActivity(startStore);
                    }
                });
                adlgBldr.setNegativeButton("Cancel", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                final AlertDialog waitDialog = adlgBldr.create();
                waitDialog.show();
            }
        }
        else
        {
            Toast.makeText(this, "This menu item is not implemented", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the list display to the give adapter
     * @param adapter Adapter to set list display to
     */
    public void setListAdapter(ListAdapter adapter)
    {
        synchronized (this)
        {
            if (mList != null)
                return;
            mAdapter = adapter;
            mList = (ListView)findViewById(android.R.id.list);
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?>  parent, View v, int position, long id)
                {
                    onListItemClick((ListView)parent, v, position, id);
                }
            });
            mList.setAdapter(adapter);
        }
    }
}
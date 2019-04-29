package com.smarthome.smartHomeArduinoProject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    TextView timeView;
    TextView dateView;
    TextView tempView;
    Toolbar toolbar;
    FloatingActionButton fab;

    Handler m_handler;
    Runnable m_handlerTask ;

    String temperature;

    String address = null;

    final Context context = this;

    String[] rooms = new String[] {
            "Wohnzimmer", "Küche", "Schlafzimmer"};

    public static String EXTRA_ADDRESS = "device_address";

    final List<String> roomList = new ArrayList<String>(Arrays.asList(rooms));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        timeView = (TextView)findViewById(R.id.timeView);
        dateView = (TextView)findViewById(R.id.dateView);
        tempView = (TextView)findViewById(R.id.tempView);

        Intent newint = getIntent();
        address = newint.getStringExtra(Devicelist.EXTRA_ADDRESS); //receive the address of the bluetooth device

        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeView.setText(new SimpleDateFormat("HH:mm", Locale.GERMAN).format(new Date()));
                dateView.setText(new SimpleDateFormat("dd. MMMM yyyy", Locale.GERMAN).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        gridView = (GridView) findViewById(R.id.gridView1);

        gridView.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, roomList) {
            public View getView(int position, View convertView, ViewGroup parent) {

                // Return the GridView current item as a View
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view;

                tv.setText(roomList.get(position));

                tv.setBackgroundColor(Color.parseColor("#FFF5F5F5"));
                tv.setGravity(Gravity.CENTER);

                ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
                layoutParams.height = 250; //this is in pixels
                tv.setLayoutParams(layoutParams);

                // Return the TextView widget as GridView item
                return tv;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                switch (position) {
                    case 0:
                        Intent i = new Intent(MainActivity.this, Wohnzimmer.class);
                        m_handler.removeCallbacks(m_handlerTask);
                        //Change the activity.
                        startActivity(i);
                        overridePendingTransition(R.animator.activity_in, R.animator.activity_out);
                }

                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });


        // Temperatur
        // runs in seperate thread
        m_handler = new Handler();
        m_handlerTask = new Runnable()
        {
            @Override
            public void run() {

                temperature = Connection.getTemperature();
                tempView.setText(temperature);
                m_handler.postDelayed(m_handlerTask, 5000);
            }
        };
        m_handlerTask.run();
    }


    public void showDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View dialogView = li.inflate(R.layout.addroomlayout, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.roomName);

        // set dialog message
        alertDialogBuilder
                .setTitle("Add a new room")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text

                                //result.setText(userInput.getText());

                                roomList.add(rooms.length, String.valueOf(userInput.getText()));
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_disconnect) {
            Connection.Disconnect();
            Intent i = new Intent(MainActivity.this, Devicelist.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
            m_handler.removeCallbacks(m_handlerTask);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Connection.Disconnect();
        // app icon in action bar clicked; goto parent activity.
        this.finish();
        //NavUtils.navigateUpFromSameTask(this);
        Intent intent = NavUtils.getParentActivityIntent(this);
        NavUtils.navigateUpTo(this, intent);
        overridePendingTransition(R.animator.activity_back_in, R.animator.activity_back_out);

    }

}

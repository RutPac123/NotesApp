package com.example.sai.notesapp;

import android.content.DialogInterface;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder builder; //creates a builder for the alert dialog box.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav); // finding the nav view.
        bottomNavigationView.setOnNavigationItemSelectedListener(navListner);  // handles the clicks on the items in the navigation bar.

        getSupportFragmentManager().beginTransaction().replace(R.id.fragcontainer,new HomeFragment()).commit();  // replaces our framelayout with the homefragment layout at start.
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner = //this is passed to the above onclicklistner method as a parameter.
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedone = new HomeFragment();
                    switch (menuItem.getItemId()){  // it will get the menu item id.
                        case R.id.nav_home:
                            selectedone = new HomeFragment(); // selectedone is the instance of the HomeFragment class here.
                            break;
                        case R.id.nav_notes:
                            selectedone = new NotesFragment();
                            break;
                        case R.id.nav_exit:
                            builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Do you want to exit?"); // title of the dialog.
                            builder.setCancelable(true);  // the dialog can be cancelled by clicking outside of it.
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // this button will handle the positive response
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    finishAndRemoveTask();  // exits the application and removes it from the recents screen.
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();  // cancels the dialog box.
                                }
                            });
                            AlertDialog alertDialog = builder.create();  //creates the alert dialog using the builder object.
                            alertDialog.show();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragcontainer,selectedone).commit();
                    return true;  // to highlight the selected item.
            }
            };
}

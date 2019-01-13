package com.example.sai.notesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private Activity activity;
    private String heading,note;
    EditText text,title;
    public AlertDialog.Builder alertDialog;
    public    ArrayList<String> arrayList;
    public ListView mlist;
    private FloatingActionButton mybtn;
    private Firebase firebase;
    private  ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View myView = inflater.inflate(R.layout.notes_frag,container,false); // Layout inflater is a class that reads the xml appearance description and convert them into java based View objects.
        activity = getActivity(); // Returns the Activity this fragment is currently associated with
        mlist = myView.findViewById(R.id.noteslist);   // in a fragment we have to get the root view for the fragment to access the methods.
        mybtn = myView.findViewById(R.id.maddbtn);
        text = myView.findViewById(R.id.mtext);
        title = myView.findViewById(R.id.mheading);

       Firebase.setAndroidContext(activity); //setting the context

        firebase = new Firebase("https://notesapp-b8544.firebaseio.com");  // creating the object by passing the url of our app ( Project -> app -> src -> google-services.json -> firebase_url.
        mybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heading = title.getText().toString(); // title of the note.
                note = text.getText().toString(); // note content.
                if(!heading.isEmpty() && !note.isEmpty()){
                    Firebase childRef = firebase.child(heading);  // setting the child node to the 'title name' given by the user.
                    childRef.setValue(note); // setting the child's data as the note content.
                    // e.g. Structure is ->      MyTitle (Child node/key)
                    //                              |
                    //                              ->  This is my note. (data/value)
                }else {
                    Toast.makeText(activity, "Empty fileds!", Toast.LENGTH_SHORT).show();
                }


        }
        });
        arrayList = new ArrayList<>();
       arrayAdapter = new ArrayAdapter(activity,android.R.layout.simple_list_item_1,arrayList);

        mlist.setAdapter(arrayAdapter);
        final ArrayList<String> keyList = new ArrayList<>();  //use to keep track of the keys for the deletion purpose.
        new Async().execute();
        firebase.getRoot().addListenerForSingleValueEvent(new ValueEventListener() {  // for tracking the data changes in the local disk cache.

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot values : dataSnapshot.getChildren()){  // DataSnapshot instance will contain the data from the firebase db.
                    // here we are looping through our data in the firebase db. dataspapshot.getChildren() -> gives the limit of the looping.(no. of childrens in db)
                    keyList.add(values.getKey()); // adding the keys in the arraylist named 'keylist'.

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(activity, "error "+ firebaseError, Toast.LENGTH_SHORT).show();
            }
        });


        mlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("Do you want to delete this note?");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {                  // try catch is used to handle the exceptions in our app. If exception occures, then it throws an object.
                            arrayAdapter.notifyDataSetChanged();
                            firebase.getRoot().child(keyList.get(position)).removeValue();
                            keyList.remove(position);
                            Toast.makeText(activity, "Note is deleted!", Toast.LENGTH_SHORT).show();


                            arrayAdapter.remove(arrayAdapter.getItem(position));
                            mlist.setAdapter( arrayAdapter );
                        }catch (IndexOutOfBoundsException e){  // the type of the exception here is IndexOutOfBoundException which is associated with the arraylist size.
                            // catch(Exception exception_object) -> catches the exception object thrown by 'try' block.
                            Toast.makeText(activity, "kindly reload!", Toast.LENGTH_SHORT).show();  // action to be performed after getting the exception.
                        }


                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
                AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.show();
                return true;
            }
        });
        return myView;
    }

     private class Async extends AsyncTask<Void,Void,Void>{  // to fetch the data in background.

         @Override
         protected Void doInBackground(Void... voids) {
             // listens to the events on the childs.
             firebase.addChildEventListener(new ChildEventListener() {
                 @Override
                 public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                     String myChildValues = dataSnapshot.getValue(String.class); // gets the data of the child i.e. our note
                     String title= dataSnapshot.getKey();    //  gets the key i.e. our title
                     arrayList.add(title + " : " + myChildValues); // adding the title along w
                     arrayAdapter.notifyDataSetChanged();  // changes in the list are notified to our adapter.
                 }

                 @Override
                 public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                     arrayAdapter.notifyDataSetChanged();
                 }

                 @Override
                 public void onChildRemoved(DataSnapshot dataSnapshot) {
                 }

                 @Override
                 public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                 }

                 @Override
                 public void onCancelled(FirebaseError firebaseError) {

                 }
             });
             return null;
         }

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
         }
     }


}

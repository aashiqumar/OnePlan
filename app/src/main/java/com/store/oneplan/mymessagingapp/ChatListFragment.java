package com.store.oneplan.mymessagingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.store.oneplan.R;
import java.util.ArrayList;

public class ChatListFragment extends Fragment {
    private ListView listView;
    private ArrayList<String> namesList;
    private ArrayAdapter mArrayAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    public  ArrayList<String> ids;
    private TextView txtName;
    private TextView txtEmail;

    public ChatListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Will inflate the LIST VIEW layout in the XML to display the user list
        View view = inflater.inflate(R.layout.fragment_chat_list_chat, container, false);

        listView = view.findViewById(R.id.ListviewChatList);
        namesList = new ArrayList<>();
        ids = new ArrayList<>();

        //The array adapter reads data to the list view
        mArrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1, namesList);
        listView.setAdapter(mArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //The the user data
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("ReceiverId", ids.get(position));
                intent.putExtra("ReceiverName", namesList.get(position));
                startActivity(intent);

            }
        });

        // firebase instance to get corect user id
        mAuth= FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userRef.child(mAuth.getCurrentUser().getUid()).child("ChatLists")
                .orderByValue().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    String usernameid = snapshot.getKey();

                    userRef.child(usernameid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists() && snapshot.hasChild("Name"))
                            {
                                ids.add(0,snapshot.getKey());
                                namesList.add(0,snapshot.child("Name").getValue().toString());
                                mArrayAdapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    final String usernameid = snapshot.getKey();
                    userRef.child(usernameid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists() && snapshot.hasChild("Name"))
                            {
                                int index =ids.indexOf(usernameid);
                                namesList.remove(index);
                                ids.remove(index);
                                ids.add(0,usernameid);
                                namesList.add(0,snapshot.child("Name").getValue().toString());

                                mArrayAdapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  view;
    }
}
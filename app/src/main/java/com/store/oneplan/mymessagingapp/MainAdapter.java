package com.store.oneplan.mymessagingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

class MainAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String > myList;
    private ArrayList<String > myEmail;

    public MainAdapter(Context context, ArrayList<String > myEmail, ArrayList<String> mylist) {
        this.context = context;
        this.myList = mylist;
        this.myEmail = myEmail;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return null;
    }
}

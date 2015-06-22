/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava.sample;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class PeopleAdapter extends ArrayAdapter<Person> {
    private final Activity activity;
    private final List<Person> dataset;

    public PeopleAdapter(Context context, List<Person> dataset) {
        super(context, R.layout.person_item, dataset);
        activity = (Activity) context;
        this.dataset = dataset;
    }

    @Override public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.person_item, parent, false);

            holder = new ViewHolder();
            holder.personName = (TextView) convertView.findViewById(R.id.person_name);
            holder.personAge = (TextView) convertView.findViewById(R.id.person_age);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Person person = dataset.get(pos);
        holder.personName.setText(person.getName());
        holder.personAge.setText(person.getAge() + " years old");

        return convertView;
    }

    static class ViewHolder {
        TextView personName;
        TextView personAge;
    }
}

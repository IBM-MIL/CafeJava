/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava.sample;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

class PeopleClickListener implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        Person person = (Person) parent.getItemAtPosition(pos);
        Toast.makeText(view.getContext(), "isDeveloper? " + person.isDeveloper(),
                Toast.LENGTH_SHORT).show();
    }

}

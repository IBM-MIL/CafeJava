/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ibm.mil.cafejava.CafeJava;
import com.worklight.wlclient.api.WLResponse;

import java.lang.reflect.Type;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView jsonPayload = (TextView) findViewById(R.id.json_payload);

        /*
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();
        */

        final Type peopleType = new TypeToken<List<Person>>(){}.getType();

        new CafeJava()
                .createConnectionObservable(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WLResponse>() {
                    @Override public void call(WLResponse wlResponse) {
                        new CafeJava()
                                .createProcedureObservable("SampleAdapter", "getPersonFlat")
                                .compose(CafeJava.serializeTo(Person.class))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Person>() {
                                    @Override public void call(Person person) {
                                        jsonPayload.setText(person.toString());
                                    }
                                }, new Action1<Throwable>() {
                                    @Override public void call(Throwable throwable) {
                                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Log.i("blah", throwable.getMessage());
                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ibm.mil.cafejava.CafeJava;
import com.worklight.wlclient.api.WLResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private CafeJava cafeJava;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private List<Person> peopleDataSet = new ArrayList<>();
    private ArrayAdapter<Person> peopleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize spinner (drop-down)
        final Spinner procedureSpinner = (Spinner) findViewById(R.id.procedure_spinner);
        ArrayAdapter<CharSequence> procedureAdapter = ArrayAdapter.createFromResource(this,
                R.array.procedure_array, android.R.layout.simple_spinner_item);
        procedureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        procedureSpinner.setAdapter(procedureAdapter);
        procedureSpinner.setOnItemSelectedListener(this);
        procedureSpinner.setEnabled(false);

        // initialize list
        ListView peopleList = (ListView) findViewById(R.id.people_list);
        peopleAdapter = new PeopleAdapter(this, peopleDataSet);
        peopleList.setAdapter(peopleAdapter);
        peopleList.setOnItemClickListener(new PeopleClickListener());

        cafeJava = new CafeJava().setTimeout(10_000);

        // establish WL connection
        Subscription connectionSubscription = cafeJava.connect(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WLResponse>() {
                    @Override public void call(WLResponse wlResponse) {
                        procedureSpinner.setEnabled(true);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Toast.makeText(MainActivity.this, "Could not connect to WL",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        subscriptions.add(connectionSubscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // handle spinner selections
        String procedureName = parent.getItemAtPosition(pos).toString();
        Observable<WLResponse> procedureObservable = cafeJava
                .invokeProcedure("SampleAdapter", procedureName);
        Observable<List<Person>> peopleObservable;

        switch (procedureName) {
            case "getPersonFlat":
                peopleObservable = procedureObservable
                        .compose(CafeJava.serializeTo(Person.class))
                        .map(new PersonListMapper());
                break;

            case "getPersonNested":
                peopleObservable = procedureObservable
                        .compose(CafeJava.serializeTo(Person.class, "person"))
                        .map(new PersonListMapper());
                break;

            case "getAllPersons":
            default:
                TypeToken<List<Person>> typeToken = new TypeToken<List<Person>>(){};
                peopleObservable = procedureObservable
                        .compose(CafeJava.serializeTo(typeToken, "persons"));
                break;
        }

        Subscription peopleSubscription = peopleObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new PeopleSubscriber());
        subscriptions.add(peopleSubscription);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // not implemented
    }

    private class PersonListMapper implements Func1<Person, List<Person>> {
        @Override public List<Person> call(Person person) {
            return Collections.singletonList(person);
        }
    }

    private class PeopleSubscriber extends Subscriber<List<Person>> {
        @Override public void onNext(List<Person> people) {
            peopleDataSet.clear();
            peopleDataSet.addAll(people);
            peopleAdapter.notifyDataSetChanged();
        }

        @Override public void onCompleted() {
            // not implemented
        }

        @Override public void onError(Throwable throwable) {
            Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

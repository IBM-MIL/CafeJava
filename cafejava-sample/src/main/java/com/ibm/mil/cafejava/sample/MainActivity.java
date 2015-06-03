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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ibm.mil.cafejava.CafeJava;
import com.worklight.wlclient.api.WLResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private CafeJava cafeJava;
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
        peopleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, peopleDataSet);
        peopleList.setAdapter(peopleAdapter);
        peopleList.setOnItemClickListener(this);

        cafeJava = new CafeJava().timeout(10_000);

        // establish WL connection
        cafeJava.createConnectionObservable(this)
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // handle spinner selections
        String procedureName = parent.getItemAtPosition(pos).toString();
        Observable<WLResponse> procedureObservable = cafeJava
                .createProcedureObservable("SampleAdapter", procedureName);
        Observable<List<Person>> peopleObservable = null;

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
                Type type = new TypeToken<List<Person>>(){}.getType();
                peopleObservable = procedureObservable
                        .compose(CafeJava.<List<Person>>serializeTo(type, "persons"));
                break;
        }

        peopleObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new PeopleSubscriber());

    }

    private class PersonListMapper implements Func1<Person, List<Person>> {
        @Override public List<Person> call(Person person) {
            return Arrays.asList(person);
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
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // handle list clicks
        Person person = (Person) parent.getItemAtPosition(pos);
        String message = person.getName() + " is " + person.getAge() + " years old";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("TAG", "NOTHING SELECTED!");
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

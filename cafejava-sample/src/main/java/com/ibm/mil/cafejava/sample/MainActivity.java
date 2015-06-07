/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ibm.mil.cafejava.CafeJava;
import com.worklight.wlclient.api.WLResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {

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
        peopleAdapter = new PeopleAdapter(this, peopleDataSet);
        peopleList.setAdapter(peopleAdapter);
        peopleList.setOnItemClickListener(this);

        cafeJava = new CafeJava().setTimeout(10_000);
        cafeJava.setTimeout(-1);

        // establish WL connection
        cafeJava.connect(this)
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

        peopleObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new PeopleSubscriber());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // not implemented
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        // handle list clicks
        Person person = (Person) parent.getItemAtPosition(pos);
        Toast.makeText(this, "isDeveloper? " + person.isDeveloper(), Toast.LENGTH_SHORT).show();
    }

    private static class PeopleAdapter extends ArrayAdapter<Person> {
        Activity activity;
        List<Person> dataset;

        public PeopleAdapter(Context context, List<Person> dataset) {
            super(context, R.layout.person_item, dataset);
            activity = (Activity) context;
            this.dataset = dataset;
        }

        static class ViewHolder {
            TextView personName;
            TextView personAge;
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

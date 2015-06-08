/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava.sample;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ibm.mil.cafejava.CafeJava;
import com.worklight.wlclient.api.WLResponse;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

class ProcedureSelectedListener implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // handle spinner selections
        String procedureName = parent.getItemAtPosition(pos).toString();
        Observable<WLResponse> procedureObservable = new CafeJava()
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

}

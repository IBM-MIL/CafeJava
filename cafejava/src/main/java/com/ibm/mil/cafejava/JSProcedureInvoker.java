/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementation for invoking a procedure from a JavaScript based adapter.
 *
 * @see JavaProcedureInvoker
 *
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public final class JSProcedureInvoker implements ProcedureInvoker {
    private final String adapterName;
    private final String procedureName;
    private Object[] parameters;

    public JSProcedureInvoker(String adapterName, String procedureName,
                              @Nullable Object... parameters) {
        this.adapterName = adapterName;
        this.procedureName = procedureName;
        this.parameters = parameters;
    }

    @Override
    public void invoke(WLResponseListener wlResponseListener) {
        try {
            URI path = new URI("/adapters/" + adapterName + "/" + procedureName);
            WLResourceRequest request = new WLResourceRequest(path, WLResourceRequest.GET);
            request.setQueryParameter("params", new Gson().toJson(parameters));
            request.send(wlResponseListener);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}

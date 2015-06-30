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
    private int timeout;
    private Object invocationContext;

    private JSProcedureInvoker(String adapterName, String procedureName) {
        this.adapterName = adapterName;
        this.procedureName = procedureName;
    }

    @Override
    public void invoke(WLResponseListener wlResponseListener) {
        try {
            URI path = new URI("/adapters/" + adapterName + "/" + procedureName);
            WLResourceRequest request = new WLResourceRequest(path, WLResourceRequest.GET);
            request.setQueryParameter("params", new Gson().toJson(parameters));
            request.setTimeout(timeout);
            request.send(wlResponseListener);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /** Configures and instantiates a {@code JSProcedureInvoker}. */
    public static class Builder {
        private final String adapterName;
        private final String procedureName;
        private Object[] parameters;
        private int timeout = 30_000;
        private Object invocationContext;

        public Builder(String adapterName, String procedureName) {
            this.adapterName = adapterName;
            this.procedureName = procedureName;
        }

        public Builder parameters(@Nullable Object... parameters) {
            this.parameters = parameters;
            return this;
        }

        /** Measured in millis. Negative values will be ignored. Default is 30ms. */
        public Builder timeout(int timeout) {
            if (timeout >= 0) {
                this.timeout = timeout;
            }
            return this;
        }

        /** Used as a tagging mechanism to determine the origin of a {@code WLResponseListener}. */
        public Builder invocationContext(@Nullable Object invocationContext) {
            this.invocationContext = invocationContext;
            return this;
        }

        public JSProcedureInvoker build() {
            JSProcedureInvoker invoker = new JSProcedureInvoker(adapterName, procedureName);
            invoker.parameters = parameters;
            invoker.timeout = timeout;
            invoker.invocationContext = invocationContext;
            return invoker;
        }
    }

}

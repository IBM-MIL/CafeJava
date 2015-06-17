/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.support.annotation.StringDef;

import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponseListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public final class JavaProcedureInvoker implements ProcedureInvoker {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            GET,
            POST,
            PUT,
            DELETE
    })
    public @interface HttpMethod {}
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private final String adapterName;
    private final String procedureName;
    private HashMap<String, String> parameters;
    private @HttpMethod String httpMethod;
    private int timeout;

    private JavaProcedureInvoker(String adapterName, String procedureName) {
        this.adapterName = adapterName;
        this.procedureName = procedureName;
    }

    @Override
    public void invoke(WLResponseListener wlResponseListener) {
        String url = "adapters/" + adapterName + "/" + procedureName;
        try {
            WLResourceRequest request = new WLResourceRequest(new URI(url), httpMethod);
            request.setQueryParameters(parameters);
            request.setTimeout(timeout);
            request.send(wlResponseListener);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static class Builder {
        private final String adapterName;
        private final String procedureName;
        private HashMap<String, String> parameters;
        private @HttpMethod String httpMethod = GET;
        private int timeout = 30_000;

        public Builder(String adapterName, String procedureName) {
            this.adapterName = adapterName;
            this.procedureName = procedureName;
        }

        public Builder parameters(HashMap<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder httpMethod(@HttpMethod String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder timeout(int timeout) {
            // negative values will be ignored
            if (timeout >= 0) {
                this.timeout = timeout;
            }
            return this;
        }

        public JavaProcedureInvoker build() {
            JavaProcedureInvoker invoker = new JavaProcedureInvoker(adapterName, procedureName);
            invoker.parameters = parameters;
            invoker.httpMethod = httpMethod;
            invoker.timeout = timeout;
            return invoker;
        }
    }

}

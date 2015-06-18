/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponseListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Implementation for invoking a procedure from a Java based adapter.
 *
 * @see JSProcedureInvoker
 *
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public final class JavaProcedureInvoker implements ProcedureInvoker {
    /** StringDef for basic HTTP method types: {@code GET, POST, PUT, DELETE}. */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            GET,
            POST,
            PUT,
            DELETE
    })
    public @interface HttpMethod {}
    /** Annotated with the {@code HttpMethod} StringDef */
    public static final String GET = "GET";
    /** Annotated with the {@code HttpMethod} StringDef */
    public static final String POST = "POST";
    /** Annotated with the {@code HttpMethod} StringDef */
    public static final String PUT = "PUT";
    /** Annotated with the {@code HttpMethod} StringDef */
    public static final String DELETE = "DELETE";

    private final String adapterName;
    private final String procedureName;
    private HashMap<String, String> pathParameters;
    private HashMap<String, String> queryParameters;
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
            request.setQueryParameters(queryParameters);
            request.setTimeout(timeout);
            request.send(pathParameters, wlResponseListener);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /** Configures and instantiates a {@code JavaProcedureInvoker}. */
    public static class Builder {
        private final String adapterName;
        private final String procedureName;
        private HashMap<String, String> pathParameters;
        private HashMap<String, String> queryParameters;
        private @HttpMethod String httpMethod = GET;
        private int timeout = 30_000;

        public Builder(String adapterName, String procedureName) {
            this.adapterName = adapterName;
            this.procedureName = procedureName;
        }

        public Builder pathParameters(@Nullable HashMap<String, String> parameters) {
            pathParameters = parameters;
            return this;
        }

        public Builder queryParameters(@Nullable HashMap<String, String> parameters) {
            queryParameters = parameters;
            return this;
        }

        /** Expects an {@code HttpMethod} StringDef constant value. Default is {@code GET}. */
        public Builder httpMethod(@NonNull @HttpMethod String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        /** Measured in millis. Negative values will be ignored. Default is 30ms. */
        public Builder timeout(int timeout) {
            if (timeout >= 0) {
                this.timeout = timeout;
            }
            return this;
        }

        public JavaProcedureInvoker build() {
            JavaProcedureInvoker invoker = new JavaProcedureInvoker(adapterName, procedureName);
            invoker.pathParameters = pathParameters;
            invoker.queryParameters = queryParameters;
            invoker.httpMethod = httpMethod;
            invoker.timeout = timeout;
            return invoker;
        }
    }

}

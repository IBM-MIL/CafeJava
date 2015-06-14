/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public final class JavaProcedureInvoker implements ProcedureInvoker {
    private final String adapterName;
    private final String procedureName;
    private HashMap<String, String> parameters;

    private JavaProcedureInvoker(String adapterName, String procedureName) {
        this.adapterName = adapterName;
        this.procedureName = procedureName;
    }

    @Override
    public void invoke(WLResponseListener wlResponseListener) {
        String url = "adapters/" + adapterName + "/" + "procedureName";
        try {
            WLResourceRequest request = new WLResourceRequest(new URI(url), WLResourceRequest.GET);
            request.setQueryParameters(parameters);
            request.send(wlResponseListener);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public class Builder {
        private final String adapterName;
        private final String procedureName;
        private HashMap<String, String> parameters;

        public Builder(String adapterName, String procedureName) {
            this.adapterName = adapterName;
            this.procedureName = procedureName;
        }

        public Builder parameters(HashMap<String, String> parameters) {
            this.parameters = parameters;
            return this;
        }

        public JavaProcedureInvoker build() {
            JavaProcedureInvoker invoker = new JavaProcedureInvoker(adapterName, procedureName);
            invoker.parameters = parameters;
            return invoker;
        }
    }

}

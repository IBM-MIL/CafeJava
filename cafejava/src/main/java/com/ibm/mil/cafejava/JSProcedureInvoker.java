/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.support.annotation.Nullable;

import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponseListener;

import org.codehaus.jackson.map.ObjectMapper;

import java.net.URI;

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
    private final Object[] params;

    public JSProcedureInvoker(String adapterName, String procedureName,
                              @Nullable Object... params) {
        this.adapterName = adapterName;
        this.procedureName = procedureName;
        this.params = params;
    }

    @Override
    public void invoke(WLResponseListener wlResponseListener) {
        try {
            URI path = new URI("/adapters/" + adapterName + "/" + procedureName);
            WLResourceRequest request = new WLResourceRequest(path, WLResourceRequest.GET);
            request.setQueryParameter("params", new ObjectMapper().writeValueAsString(params));
            request.send(wlResponseListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

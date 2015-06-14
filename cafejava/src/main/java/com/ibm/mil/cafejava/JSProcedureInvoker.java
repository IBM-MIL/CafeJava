/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLProcedureInvocationData;
import com.worklight.wlclient.api.WLResponseListener;

public final class JSProcedureInvoker implements ProcedureInvoker {
    private final String adapterName;
    private final String procedureName;
    private Object[] parameters;

    private JSProcedureInvoker(String adapterName, String procedureName) {
        this.adapterName = adapterName;
        this.procedureName = procedureName;
    }

    @Override
    public void invoke(WLResponseListener wlResponseListener) {
        // TODO: request options and compressed response
        WLProcedureInvocationData invocationData = new WLProcedureInvocationData(adapterName,
                procedureName);
        invocationData.setParameters(parameters);
        WLClient.getInstance().invokeProcedure(invocationData, wlResponseListener);
    }

    public static class Builder {
        private final String adapterName;
        private final String procedureName;
        private Object[] parameters;

        public Builder(String adapterName, String procedureName) {
            this.adapterName = adapterName;
            this.procedureName = procedureName;
        }

        public Builder parameters(Object... parameters) {
            this.parameters = parameters;
            return this;
        }

        public JSProcedureInvoker build() {
            JSProcedureInvoker invoker = new JSProcedureInvoker(adapterName, procedureName);
            invoker.parameters = parameters;
            return invoker;
        }
    }

}

/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLProcedureInvocationData;
import com.worklight.wlclient.api.WLRequestOptions;
import com.worklight.wlclient.api.WLResponseListener;

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
        WLProcedureInvocationData invocationData = new WLProcedureInvocationData(adapterName,
                procedureName);
        invocationData.setParameters(parameters);

        WLRequestOptions requestOptions = new WLRequestOptions();
        requestOptions.setTimeout(timeout);
        requestOptions.setInvocationContext(invocationContext);

        WLClient.getInstance().invokeProcedure(invocationData, wlResponseListener, requestOptions);
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

        public Builder parameters(Object... parameters) {
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

        /** Used a tagging mechanism to determine the origin of a {@code WLResponseListener}. */
        public Builder invocationContext(Object invocationContext) {
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

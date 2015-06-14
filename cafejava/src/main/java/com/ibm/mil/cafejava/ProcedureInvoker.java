/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLResponseListener;

public interface ProcedureInvoker {
    void invoke(WLResponseListener wlResponseListener);
}

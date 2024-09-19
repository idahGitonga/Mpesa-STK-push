package mobile.money.mobile.money.service;

import mobile.money.mobile.money.dtos.AccessTokenResponse;
import mobile.money.mobile.money.dtos.InternalPushRequest;
import mobile.money.mobile.money.dtos.StkPushSyncResponse;

public interface Daraja {
//this is an abstract method.
    AccessTokenResponse getAccessToken();
    StkPushSyncResponse performStkPushTransaction(InternalPushRequest internalPushRequest);

}

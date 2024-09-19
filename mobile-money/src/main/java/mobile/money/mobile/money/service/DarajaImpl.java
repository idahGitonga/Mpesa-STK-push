package mobile.money.mobile.money.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mobile.money.mobile.money.configurations.MpesaConfiguration;
import mobile.money.mobile.money.dtos.*;
import mobile.money.mobile.money.utils.Constants;
import mobile.money.mobile.money.utils.HelperUtility;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static mobile.money.mobile.money.utils.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
//implement the interface daraja
public class DarajaImpl implements Daraja {
    //reasons for autowiring - secret key, grant type, consumer key and the endpoint are needed when sending the request
    //okhttp client is responsible for sending the requests to the Sandbox api
    //object mapper is responsible for converting the object to JSON format for communication between our application and the Safaricom APIs
    @Autowired
    private final MpesaConfiguration mpesaConfiguration;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;


    @SneakyThrows
    @Override
    public AccessTokenResponse getAccessToken() {
//step 1 get a base 64 representation of the consumer key and the consumer secret
        //create a helper utility class to convert a string to a base 64 character ie the consumer key and secret key strings
        String encodedCredentials = "Basic " + HelperUtility.toBase64String(String.format("%s:%s", mpesaConfiguration.getConsumerKey(), mpesaConfiguration.getConsumerSecret()));
        Request request = new Request.Builder()
                .url(mpesaConfiguration.getEndpoint())
                .method("GET", null)
                .addHeader("Authorization", encodedCredentials)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            //AccessTokenResponse accessTokenResponse = objectMapper.readValue(response.body().string(), AccessTokenResponse.class);
           // return accessTokenResponse;
            return objectMapper.readValue(response.body().string(), AccessTokenResponse.class);

        } catch (IOException e) {
            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            System.out.println("Could not get access token: " + e);
            return null;
        }

    }

    @Override
    public StkPushSyncResponse performStkPushTransaction(InternalPushRequest internalPushRequest) {
        ExternalPushRequest externalStkPushRequest = new ExternalPushRequest();

        String transactionTimestamp = HelperUtility.getTransactionTimeStamp();
        String stkPushPassword = HelperUtility.getStkPushPassword(mpesaConfiguration.getStkPushCode(),
                mpesaConfiguration.getStkPassKey(), transactionTimestamp);

        externalStkPushRequest.setBusinessShortCode(mpesaConfiguration.getStkPushCode());
        externalStkPushRequest.setPassword(stkPushPassword);
        externalStkPushRequest.setTimeStamp(transactionTimestamp);
        externalStkPushRequest.setTransactionType(Constants.CUSTOMER_PAYBILL_ONLINE);
        externalStkPushRequest.setAmount(internalPushRequest.getAmount());
        externalStkPushRequest.setPartyA(internalPushRequest.getPhoneNumber());
        externalStkPushRequest.setPartyB(mpesaConfiguration.getStkPushCode());
        externalStkPushRequest.setPhoneNumber(internalPushRequest.getPhoneNumber());
        externalStkPushRequest.setCallBackUrl(mpesaConfiguration.getStkCallbackUrl());
        externalStkPushRequest.setAccountReference(HelperUtility.getTransactionUniqueNumber());
        externalStkPushRequest.setTransactionDesc(String.format("%s Transaction", internalPushRequest.getPhoneNumber()));


        AccessTokenResponse accessTokenResponse = getAccessToken();
        System.out.println(accessTokenResponse.getAccessToken());


        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(externalStkPushRequest)));


        Request request = new Request.Builder()
                .url(mpesaConfiguration.getStkRequestUrl())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessTokenResponse.getAccessToken()))
                .build();
        System.out.println("Request URL: " + request.url());
        System.out.println("Request Headers: " + request.headers());
        System.out.println("Request Body: " + body);

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            // use Jackson to Decode the ResponseBody ...
            //String responseBody = response.body().string();
            return objectMapper.readValue(response.body().string(), StkPushSyncResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not perform the STK push request -> %s", e.getLocalizedMessage()));
            return null;
        }

    }
    public void callback(StkCallbackRequest stkCallbackRequest) throws Exception {
        String jsonResponse = "{ \"Body\": { \"stkCallback\": { \"MerchantRequestID\": \"29115-34620561-1\", \"CheckoutRequestID\": \"ws_CO_191220191020363925\", \"ResultCode\": 0, \"ResultDesc\": \"The service request is processed successfully.\", \"CallbackMetadata\": { \"Item\": [ { \"Name\": \"Amount\", \"Value\": 1.00 }, { \"Name\": \"MpesaReceiptNumber\", \"Value\": \"NLJ7RT61SV\" }, { \"Name\": \"TransactionDate\", \"Value\": 20191219102115 }, { \"Name\": \"PhoneNumber\", \"Value\": 254708374149 } ] } } } }";

        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize JSON response into StkCallbackRequest object
        StkCallbackRequest StkCallbackRequest = objectMapper.readValue(jsonResponse, StkCallbackRequest.class);

        // Now you can access the parsed data

        StkCallback stkCallback = StkCallbackRequest.getBody().getStkCallback();

        System.out.println("Merchant Request ID: " + stkCallback.getMerchantRequestID());
        System.out.println("Checkout Request ID: " + stkCallback.getCheckoutRequestID());

    }
}

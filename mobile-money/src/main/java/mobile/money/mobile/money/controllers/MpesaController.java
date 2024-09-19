package mobile.money.mobile.money.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mobile.money.mobile.money.dtos.*;
import mobile.money.mobile.money.service.Daraja;
import mobile.money.mobile.money.service.DarajaImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("mpesa")
@RequiredArgsConstructor
@Slf4j
public class MpesaController {
    private final Daraja daraja;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;
    private final DarajaImpl darajaImpl;

    @GetMapping(path="/token",produces="application/json")
    public ResponseEntity<AccessTokenResponse> getAccessToken(){
        return ResponseEntity.ok(daraja.getAccessToken());
    }
    @PostMapping(path = "/stk-transaction-request", produces = "application/json")
    public ResponseEntity<StkPushSyncResponse> performStkPushTransaction(@RequestBody InternalPushRequest internalPushRequest) {
        return ResponseEntity.ok(daraja.performStkPushTransaction(internalPushRequest));
    } @SneakyThrows
    @PostMapping(path = "/stk-transaction-result", produces = "application/json")
    public ResponseEntity<AcknowledgeResponse> acknowledgeStkPushResponse(@RequestBody StkPushAsyncResponse stkPushAsyncResponse) {
        log.info("======= STK Push Async Response =====");
        log.info(objectMapper.writeValueAsString(stkPushAsyncResponse));
        return ResponseEntity.ok(acknowledgeResponse);

    }
    @PostMapping("/callback")
    public void callback(@RequestBody StkCallbackRequest stkCallbackRequest) throws Exception {
        darajaImpl.callback(stkCallbackRequest);

    }




}

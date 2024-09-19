package mobile.money.mobile.money.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Slf4j
public class HelperUtility {
    public static String toBase64String(String value){
byte[] data=value.getBytes(StandardCharsets.ISO_8859_1);
        return Base64.getEncoder().encodeToString(data);
    }
    public static String toJson(Object object) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
public static String getTransactionUniqueNumber(){
         RandomStringGenerator stringGenerator=new RandomStringGenerator.Builder()
                 .withinRange('0','z')
                 .filteredBy(CharacterPredicates.LETTERS,CharacterPredicates.DIGITS)
                 .build();
    String transactionNumber=stringGenerator.generate(12).toUpperCase();
    log.info(String.format("Transaction Number: %s", transactionNumber));
    return transactionNumber;
    }
    public static String getStkPushPassword(String shortCode,String passKey,String timeStamp){
        String concatString=String.format("%s,%s,%s",shortCode,passKey,timeStamp);
        return toBase64String(concatString);
    }
    public static String getTransactionTimeStamp(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }
}

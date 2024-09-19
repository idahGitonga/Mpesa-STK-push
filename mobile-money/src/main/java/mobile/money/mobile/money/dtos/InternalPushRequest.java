package mobile.money.mobile.money.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class InternalPushRequest {
    @JsonProperty("PhoneNumber")
    private String phoneNumber;
    @JsonProperty("Amount")
    private String amount;
}

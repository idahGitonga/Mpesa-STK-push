package mobile.money.mobile.money.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "safaricom")
//the configuration annotations are to specify this is a configuration class,it will be picking the values from the application properties for the application mobile money
public class MpesaConfiguration {
private String consumerKey;
private String consumerSecret;
private String endpoint;
private String grantType;
private String stkPassKey;
private String stkPushCode;
private String stkRequestUrl;
private String stkCallbackUrl;
@Override
    public String toString(){
    return String.format("{consumerKey='%s', consumerSecret='%s', grantType='%s', endpoint='%s'}",
            consumerKey, consumerSecret, grantType, endpoint);
}
}



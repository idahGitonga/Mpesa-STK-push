package mobile.money.mobile.money;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class
MobileMoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobileMoneyApplication.class, args);
	}
// in as much as these are libraries that can be injected declare the OKHttp and Object Mapper as beans to allow customization
	@Bean
	public OkHttpClient getOkHttpClient(){
		return new OkHttpClient();
}
@Bean
	public ObjectMapper getObjectMapper(){
		return new ObjectMapper();
}
}

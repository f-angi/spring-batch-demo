package org.example.springbatchdemo.ws;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
public class WsConfiguration {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("org.example.springbatchdemo.ws");
        return marshaller;
    }

    @Bean
    public CountryInfoClient countryInfoClient(Jaxb2Marshaller marshaller) {
        CountryInfoClient client = new CountryInfoClient();
        client.setDefaultUri("http://www.oorsprong.org/websamples.countryinfo");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}

package org.example.springbatchdemo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

//@Import(TestcontainersConfiguration.class)
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootTest
class SoapClientTest {

    @Autowired
    CountryInfoClient client;

    @Test
    void countryInfoClient() {
        var response = client.getCountry("US");
        Assertions.assertEquals("United States", response.getFullCountryInfoResult().getSName());
    }

}

package org.example.springbatchdemo;

import lombok.extern.slf4j.Slf4j;
import org.example.springbatchdemo.ws.FullCountryInfo;
import org.example.springbatchdemo.ws.FullCountryInfoResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@Slf4j
public class CountryInfoClient extends WebServiceGatewaySupport {

    private static final String URI = "http://www.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";

    public FullCountryInfoResponse getCountry(String countryISOCode) {

        FullCountryInfo request = new FullCountryInfo();
        request.setSCountryISOCode(countryISOCode);

        log.info("Requesting location for {}", countryISOCode);

        FullCountryInfoResponse response = (FullCountryInfoResponse) getWebServiceTemplate()
                .marshalSendAndReceive(URI, request,
                        new SoapActionCallback(""));

        return response;
    }

}

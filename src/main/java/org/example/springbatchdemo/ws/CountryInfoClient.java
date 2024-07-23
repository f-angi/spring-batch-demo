package org.example.springbatchdemo.ws;

import lombok.extern.slf4j.Slf4j;
import org.example.springbatchdemo.exception.CustomRuntimeException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@Slf4j
public class CountryInfoClient extends WebServiceGatewaySupport {

    private static final String URI = "http://www.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";

    public FullCountryInfoResponse getCountry(String countryISOCode) {
        log.info("Requesting location for {}", countryISOCode);

        if (Math.random() > 0.1) {
            throw new CustomRuntimeException("CustomRuntimeException " + countryISOCode);
        }

        FullCountryInfo request = new FullCountryInfo();
        request.setSCountryISOCode(countryISOCode);

        FullCountryInfoResponse response = (FullCountryInfoResponse) getWebServiceTemplate()
                .marshalSendAndReceive(URI, request,
                        new SoapActionCallback(""));

        return response;
    }

}

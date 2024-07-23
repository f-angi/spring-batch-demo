package org.example.springbatchdemo.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springbatchdemo.exception.CustomRuntimeException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import javax.swing.text.html.Option;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Retryable
@Service
public class CountryInfoClientAdapter extends WebServiceGatewaySupport {

    private static final String URI = "http://www.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";
    private final CountryInfoClient client;

    @Retryable(retryFor = CustomRuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public Optional<FullCountryInfoResponse> getCountry(String countryISOCode) {
        return Optional.of(client.getCountry(countryISOCode));
    }

    @Recover
    // must return same type as original method
    public Optional<FullCountryInfoResponse> recover(Exception e, String countryISOCode) {
        log.info("Recover {}", countryISOCode);
        return Optional.empty();
    }

}

package com.mycomp.b2borderservice.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VatService {

    private final RestTemplate restTemplate;

    private final String vatlayerUrl;
    private final String vatlayerKey;

    @Autowired
    public VatService(@Value("${b2b.vatlayer.url}") String vatlayerUrl,
                      @Value("${b2b.vatlayer.key}") String vatlayerKey,
                      RestTemplate restTemplate) {
        this.vatlayerUrl = vatlayerUrl;
        this.vatlayerKey = vatlayerKey;
        this.restTemplate = restTemplate;
    }

    public boolean validateVatNumber(String vatNumber) {
        Map<String, String> queryParamsMap = new HashMap<>();
        queryParamsMap.put("accessKey", vatlayerKey);
        queryParamsMap.put("vatNumber", vatNumber);
        String url = vatlayerUrl + "/validate?access_key={accessKey}&vat_number={vatNumber}&format=1";
        Optional<VatlayerValidationResponse> responseOptional = Optional.ofNullable(restTemplate.getForObject(url, VatlayerValidationResponse.class, queryParamsMap));
        return responseOptional.map(VatlayerValidationResponse::isValid).orElse(false);
    }
}

@Data
class VatlayerValidationResponse {
    private boolean valid;
}

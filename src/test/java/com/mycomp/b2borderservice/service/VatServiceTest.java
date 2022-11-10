package com.mycomp.b2borderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VatServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private VatService vatService;

    private static final String VATLAYER_URL_MOCK = "urlMock";
    private static final String VATLAYER_KEY_MOCK = "keyMock";

    @BeforeEach
    void beforeEach() {
        this.vatService = new VatService(VATLAYER_URL_MOCK, VATLAYER_KEY_MOCK, restTemplate);
    }

    @Test
    void validateVatNumber_ShouldReturnTrue_WhenVatlayerValidatesOk() {
        String vatNumber = "123abc";
        VatlayerValidationResponse vatlayerValidationResponse = new VatlayerValidationResponse();
        vatlayerValidationResponse.setValid(true);
        when(restTemplate.getForObject(anyString(), eq(VatlayerValidationResponse.class), anyMap())).thenReturn(vatlayerValidationResponse);

        boolean valid = vatService.validateVatNumber(vatNumber);

        assertThat(valid).isTrue();
        Map<String, String> queryParamsMap = new HashMap<>();
        queryParamsMap.put("accessKey", VATLAYER_KEY_MOCK);
        queryParamsMap.put("vatNumber", vatNumber);
        verify(restTemplate).getForObject(
                eq(VATLAYER_URL_MOCK + "/validate?access_key={accessKey}&vat_number={vatNumber}&format=1"),
                eq(VatlayerValidationResponse.class),
                eq(queryParamsMap));
    }
}
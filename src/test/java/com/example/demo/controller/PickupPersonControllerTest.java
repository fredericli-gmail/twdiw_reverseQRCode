package com.example.demo.controller;

import com.example.demo.service.ECCService;
import com.example.demo.service.HMACService;
import com.example.demo.service.QRCodeService;
import com.example.demo.service.TOTPService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PickupPersonControllerTest {

    @Mock
    private TOTPService totpService;

    @Mock
    private HMACService hmacService;

    @Mock
    private ECCService eccService;

    @Mock
    private QRCodeService qrCodeService;

    @InjectMocks
    private PickupPersonController controller;

    @Test
    void generatePickupPersonData_usesCustomDataType() throws Exception {
        when(totpService.generateTOTP("totpKey")).thenReturn("123456");
        when(hmacService.calculateHMAC(anyString(), eq("hmacKey"))).thenReturn("hmacValue");
        when(eccService.encrypt(anyString(), eq("rsaPublicKey"))).thenReturn("encryptedPayload");
        when(qrCodeService.generateQRCodeWithLogoSpace(anyString(), any(), anyInt(), anyInt(), anyInt()))
            .thenReturn("qrImage");

        Map<String, Object> request = new HashMap<>();
        Map<String, String> fields = new HashMap<>();
        fields.put("name", "測試");
        request.put("dynamicFields", fields);
        request.put("totpKey", "totpKey");
        request.put("hmacKey", "hmacKey");
        request.put("rsaPublicKey", "rsaPublicKey");
        request.put("keyCode", "customKey");
        request.put("dataType", "CUSTOM_TAG");

        ResponseEntity<Map<String, Object>> response = controller.generatePickupPersonData(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("encryptedData")).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, String> encrypted = (Map<String, String>) body.get("encryptedData");
        assertThat(encrypted.get("t")).isEqualTo("CUSTOM_TAG");
        assertThat(encrypted.get("d")).isEqualTo("encryptedPayload");
        assertThat(encrypted.get("h")).isEqualTo("hmacValue");
        assertThat(encrypted.get("k")).isEqualTo("customKey");
    }
}

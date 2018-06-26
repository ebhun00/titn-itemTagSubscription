package com.safeway.titan.dug.service;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConfigurationProperties("eom_rest")
public class StoreLayoutService {

	@NotNull
	@Setter
	private String storeLayoutUrl;

	@Autowired
	private RestTemplate restTemplate;

	public String sendXmlToEOM(String xmlMsg, String storeNum) {
		String resultMessage = "";
		log.debug("store layout xml for the store : {} and payload is : \n {}", storeNum, xmlMsg);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		try {
			HttpEntity<String> request = new HttpEntity<String>(xmlMsg, headers);

			ResponseEntity<String> response = restTemplate.exchange(storeLayoutUrl, HttpMethod.POST, request,
					String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				resultMessage = String.format("Store layout sent successfully for the store %s", storeNum);
				if (response.getBody().contains("<Response_Type>Error</Response_Type>")) {
					resultMessage = String.format(
							"Store layout sent successfully, but failed to process in EOM, check in EOM for error details");
				};
				log.info(resultMessage);
			}
		} catch (Exception e) {
			resultMessage = String.format("Store layout sending failed for the store %s", storeNum);
			log.info(resultMessage);
		}
		return resultMessage;

	}

}

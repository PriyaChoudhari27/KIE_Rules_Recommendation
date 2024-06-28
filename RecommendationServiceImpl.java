package com.example.AWSrecommendation.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.AWSrecommendation.model.Recommendation;
import com.example.AWSrecommendation.model.VirtualMachine;
import com.example.AWSrecommendation.service.Neo4jService;
import com.example.AWSrecommendation.service.RecommendationService;
import com.example.AWSrecommendation.utils.KieServerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

	@Autowired
	private KieServerClient kieServerClient;

	@Autowired
	private Neo4jService neo4jService;

	@Autowired
	private ObjectMapper objectMapper; // Inject ObjectMapper for JSON processing

	@Override
	public Recommendation generateRecommendation(String accountName) {
		logger.info("Fetching customer with accountName: {}", accountName);
		VirtualMachine user = neo4jService.findByAccountName(accountName);
		logger.info("Fetched customer: {}", user);

		if (user == null) {
			throw new RuntimeException("User not found");
		}

		Map<String, Object> facts = new HashMap<>();
		facts.put("vm", user);

		try {
			String payload = createPayload(facts);
			logger.info("Generated payload: {}", payload);

			ResponseEntity<String> response = kieServerClient.executeRules(payload);
			logger.info("Received response with status: {}", response.getStatusCode());

			if (response.getStatusCode().is2xxSuccessful()) {
				return processResponse(response.getBody(), user);
			} else {
				logger.error("Error response from KIE server: {}", response.getBody());
				throw new RuntimeException("Error executing rules: " + response.getBody());
			}
		} catch (Exception e) {
			logger.error("Exception while generating recommendation", e);
			return fallbackRecommendation(user);
		}
	}

	private String createPayload(Map<String, Object> facts) throws JsonProcessingException {
		Map<String, Object> payloadMap = new HashMap<>();
		payloadMap.put("facts", facts);
		return objectMapper.writeValueAsString(payloadMap);
	}

	private Recommendation processResponse(String responseBody, VirtualMachine vm) {
		// Process the KIE Server response and map it to Recommendation object
		Recommendation recommendation = new Recommendation(responseBody, vm);
		recommendation.setMessage("Processed recommendation from response: " + responseBody);
		recommendation.setVm(vm);
		return recommendation;
	}

	private Recommendation fallbackRecommendation(VirtualMachine vm) {
		// Fallback recommendation logic in case KIE Server is unavailable
		Recommendation recommendation = new Recommendation(null, vm);
		recommendation.setMessage("Default recommendation due to KIE server outage");
		recommendation.setVm(vm);
		return recommendation;
	}
}

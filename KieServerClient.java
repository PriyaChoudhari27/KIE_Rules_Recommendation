package com.example.AWSrecommendation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class KieServerClient {

	 private static final Logger logger = LoggerFactory.getLogger(KieServerClient.class);

	    private final RestTemplate restTemplate;
	    private final String kieServerUrl;
	    private final String containerId;
	    private final String username;
	    private final String password;

	    public KieServerClient(RestTemplate restTemplate,
	                           @Value("${kie.server.url}") String kieServerUrl,
	                           @Value("${kie.container.id}") String containerId,
	                           @Value("${kie.server.username}") String username,
	                           @Value("${kie.server.password}") String password) {
	        this.restTemplate = restTemplate;
	        this.kieServerUrl = kieServerUrl;
	        this.containerId = containerId;
	        this.username = username;
	        this.password = password;
	    }

	    public ResponseEntity<String> executeRules(String payload) throws JSONException {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
	            headers.set("Authorization", "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes()));
	        }

	        // Parse the payload from the database
	        JSONObject vm = new JSONObject(payload).getJSONObject("facts").getJSONObject("vm");

	        // Create commands array to hold insert and fire-all-rules commands
	        JSONArray commands = new JSONArray();

	        // Create insert command for the fact
	        JSONObject insertCommand = new JSONObject();
	        JSONObject insertObject = new JSONObject();
	        insertObject.put("com.myspace." + containerId + ".VirtualMachine", vm);
	        insertCommand.put("insert", insertObject);
	        commands.put(insertCommand);

	        // Create fire-all-rules command
	        JSONObject fireAllRulesCommand = new JSONObject();
	        fireAllRulesCommand.put("fire-all-rules", new JSONObject());
	        commands.put(fireAllRulesCommand);

	        // Prepare payload to send to KIE Server
	        JSONObject executionPayload = new JSONObject();
	        executionPayload.put("lookup", "defaultStatelessKieSession");
	        executionPayload.put("commands", commands);

	        HttpEntity<String> entity = new HttpEntity<>(executionPayload.toString(), headers);
	        String url = kieServerUrl + "/containers/instances/" + containerId ;

	        logger.info("Executing rules with URL: {}", url);
	        logger.debug("Request payload: {}", executionPayload.toString());

	        try {
	            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	            logger.info("Response received: {}", response.getBody());
	            return response;
	        } catch (HttpClientErrorException e) {
	            logger.error("HTTP client error executing rules: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
	            throw new RuntimeException("HTTP client error executing rules: " + e.getMessage(), e);
	        } catch (HttpServerErrorException e) {
	            logger.error("HTTP server error executing rules: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
	            throw new RuntimeException("HTTP server error executing rules: " + e.getMessage(), e);
	        } catch (Exception e) {
	            logger.error("Unexpected error executing rules", e);
	            throw new RuntimeException("Unexpected error executing rules: " + e.getMessage(), e);
	        }
	    }
	}

package com.example.AWSrecommendation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.AWSrecommendation.model.Recommendation;
import com.example.AWSrecommendation.service.RecommendationService;

@RestController
@RequestMapping("/api")
public class RecommendationController {

	@Autowired
	private RecommendationService recommendationService;

	@GetMapping("/recommendation")
	public ResponseEntity<Recommendation> getRecommendation(@RequestParam String accountName) {
		Recommendation recommendation = recommendationService.generateRecommendation(accountName);
		return ResponseEntity.ok(recommendation);
	}

}

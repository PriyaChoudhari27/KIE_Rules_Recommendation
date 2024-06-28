package com.example.AWSrecommendation.service;

import com.example.AWSrecommendation.model.Recommendation;

public interface RecommendationService {

	 Recommendation generateRecommendation(String accountName);
}

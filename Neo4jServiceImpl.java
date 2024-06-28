package com.example.AWSrecommendation.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.AWSrecommendation.Neo4j.repository.AWSRecommendationRepo;
import com.example.AWSrecommendation.model.VirtualMachine;
import com.example.AWSrecommendation.service.Neo4jService;

@Service
public class Neo4jServiceImpl implements Neo4jService {

	@Autowired
	private AWSRecommendationRepo awsRecommendationRepo;

	@Override
	public VirtualMachine findByAccountName(String accountName) {
		return awsRecommendationRepo.findByAccountName(accountName);
	}
}

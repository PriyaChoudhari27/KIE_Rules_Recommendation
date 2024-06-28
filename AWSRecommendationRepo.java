package com.example.AWSrecommendation.Neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import com.example.AWSrecommendation.model.VirtualMachine;

@Repository
public interface AWSRecommendationRepo extends Neo4jRepository<VirtualMachine, Long> {

	VirtualMachine findByAccountName(String accountName);
}

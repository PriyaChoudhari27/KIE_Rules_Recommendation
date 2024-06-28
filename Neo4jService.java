package com.example.AWSrecommendation.service;

import com.example.AWSrecommendation.model.VirtualMachine;

public interface Neo4jService {

	VirtualMachine findByAccountName(String accountName);
}

package com.example.AWSrecommendation.model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;

@Data
@Node("VirtualMachine")
public class VirtualMachine {

	@Id
	@GeneratedValue
	private Long id;

	private String accountName;
	private String accountType;
	private String assetIdentity;
	private String assetName;
	private String assetRegion;
	private String availabilityZone;
	private String configId;
	private String elasticIpAddress;
	private String environmentName;
	private String environmentType;
	private String instanceType;
	private int numberOfCPU;
	private String privateIpAddress;
	private String serverId;
	private String serverState;
	private String storage;
	private String subnetId;
	private Map<String, String> tags;
	private String vpcId;
	

}

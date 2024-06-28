package com.example.AWSrecommendation.model;

import lombok.Data;

@Data
public class Recommendation {

	private String message;
	private VirtualMachine vm;
	
	
	  public Recommendation(String message, VirtualMachine vm) {
	        this.message = message;
	        this.vm = vm;
	    }
	
	 @Override
	    public String toString() {
	        return "Recommendation: " + message;
	    }
	
	}

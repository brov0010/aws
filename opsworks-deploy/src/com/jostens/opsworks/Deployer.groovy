package com.jostens.opsworks

import groovy.util.logging.Log4j

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.opsworks.AWSOpsWorksClient
import com.amazonaws.services.opsworks.model.CreateDeploymentRequest
import com.amazonaws.services.opsworks.model.DeploymentCommand
import com.amazonaws.services.opsworks.model.DescribeInstancesRequest

@Log4j
class Deployer {

	private final def client

	public Deployer(awsAccessKey, awsPrivateKey) {
		this.client = new AWSOpsWorksClient(new BasicAWSCredentials(awsAccessKey, awsPrivateKey))
	}
	
	public def deploy(layerId, appId) {
		// First get the instances
		def instanceReq = new DescribeInstancesRequest(layerId: layerId)
		def instanceResp = client.describeInstances(instanceReq)
		def instanceIds = instanceResp.instances.collect { it.instanceId }
		
		def command = new DeploymentCommand(name: "deploy")
		def deployReq = new CreateDeploymentRequest(appId: appId, instanceIds: instanceIds, command: command)
		client.createDeployment(deployReq)
	}
}

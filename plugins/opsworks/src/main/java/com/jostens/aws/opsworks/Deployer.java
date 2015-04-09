package com.jostens.aws.opsworks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.opsworks.AWSOpsWorksClient;
import com.amazonaws.services.opsworks.model.CreateDeploymentRequest;
import com.amazonaws.services.opsworks.model.DeploymentCommand;
import com.amazonaws.services.opsworks.model.DeploymentCommandName;
import com.amazonaws.services.opsworks.model.DescribeInstancesRequest;
import com.amazonaws.services.opsworks.model.DescribeInstancesResult;
import com.amazonaws.services.opsworks.model.Instance;
import com.jostens.aws.AbstractClient;

/**
 * We probable want to look further into creating a more abstract pattern.
 * This is just an initial pass.
 * 
 * @author brovolc
 *
 */
public class Deployer extends AbstractClient<AWSOpsWorksClient> {

	private final AWSOpsWorksClient client;
	
	@SuppressWarnings("unused")
	private Deployer() { this.client = null; }
	
	public Deployer(String accessKey, String secretKey) {
		
		this.client = new AWSOpsWorksClient(new BasicAWSCredentials(accessKey, secretKey));
	}
	
	public void deploy(String layerId, String applicationId, String customJSON) {
		
		DescribeInstancesRequest instanceRequest = new DescribeInstancesRequest();
		instanceRequest.setLayerId(layerId);
		
		DescribeInstancesResult instanceResult = client.describeInstances(instanceRequest);
		List<String> instanceIds = new ArrayList<String>();
		
		for(Instance instance : instanceResult.getInstances()) {
			instanceIds.add(instance.getInstanceId());
		}
		
		DeploymentCommand command = new DeploymentCommand();
		command.setName(DeploymentCommandName.Deploy);
		
		CreateDeploymentRequest deploymentRequest = new CreateDeploymentRequest();
		deploymentRequest.setAppId(applicationId);
		deploymentRequest.setCommand(command);
		deploymentRequest.setInstanceIds(instanceIds);
		
		if (!StringUtils.isEmpty(customJSON)) {
			deploymentRequest.setCustomJson(customJSON);
		}
		
		getClient().createDeployment(deploymentRequest);
	}

	@Override
	protected final AWSOpsWorksClient getClient() {
		return client;
	}


}

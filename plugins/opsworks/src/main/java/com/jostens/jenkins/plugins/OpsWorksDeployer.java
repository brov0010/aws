package com.jostens.jenkins.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.jostens.aws.opsworks.Deployer;

/**
 * 
 * @author Chad Brovold
 */
public class OpsWorksDeployer extends Notifier {


	private final String layerId;
	private final String applicationId;
	private String customJSON;
	private final String credentialsParameterName;
	private StandardUsernamePasswordCredentials credentials;


	@DataBoundConstructor
	public OpsWorksDeployer(String layerId, String applicationId, String credentialsParameterName, String customJSON) {
		this.layerId = layerId;
		this.applicationId = applicationId;
		this.credentialsParameterName = credentialsParameterName;
		this.customJSON = customJSON;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	@Override
	public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
		if (StringUtils.isEmpty(getCredentialsParameterName())) {
			listener.getLogger().println("You must add the Credentials Parameter Name.");
			return false;
		}
		String id = build.getBuildVariables().get(getCredentialsParameterName());
		if (StringUtils.isEmpty(id)) {
			listener.getLogger().println("Enter the correct Credentials Parameter Name.");
			return false;
		}
		
		credentials = CredentialsProvider.findCredentialById(id, StandardUsernamePasswordCredentials.class, build);
		return super.prebuild(build, listener);
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		if (build.getResult().equals(Result.SUCCESS)) {

			Deployer deployer = new Deployer(getCredentials().getUsername(),
					getCredentials().getPassword().getPlainText());

			deployer.deploy(getLayerId(), getApplicationId(), getCustomJSON());
			
			listener.getLogger().println("Successfully deployed " + getApplicationId());

		}

		return true;
	}

	public String getLayerId() {
		return layerId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getCustomJSON() {
		return customJSON;
	}

	public String getCredentialsParameterName() {
		return credentialsParameterName;
	}
	
	public StandardUsernamePasswordCredentials getCredentials() {
		return credentials;
	}

	@Override
	public OpsWorksDescriptor getDescriptor() {
		return (OpsWorksDescriptor) super.getDescriptor();
	}

	@Extension
	public static final class OpsWorksDescriptor extends
			BuildStepDescriptor<Publisher> {

//		private String awsAccessKey;
//		private String awsSecretKey;

		public OpsWorksDescriptor() {
			load();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Deploy to OpsWorks";
		}

//		public String getAwsAccessKey() {
//			return awsAccessKey;
//		}

//		public String getAwsSecretKey() {
//			return awsSecretKey;
//		}

//		@Override
//		public boolean configure(StaplerRequest req, JSONObject formData)
//				throws FormException {
//			awsAccessKey = formData.getString("awsAccessKey");
//			awsSecretKey = formData.getString("awsSecretKey");
//			save();
//			return super.configure(req, formData);
//		}
	}
}

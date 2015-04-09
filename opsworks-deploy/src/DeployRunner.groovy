import com.jostens.opsworks.Deployer

class DeployRunner {

	static main(args) {
		def d = new Deployer(args[0], args[1])
		d.deploy(args[2], args[3])
	}

}

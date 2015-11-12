package phonestats;

import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava.core.Vertx;

public class PhonestatsMain {
	public static void main(String[] args) {
		new PhonestatsMain().run(Vertx.vertx());
	}

	public void run(Vertx vertx) {
		vertx.deployVerticle("phonestats.PhonestatsRouter", new DeploymentOptions().setInstances(1));
	}
}

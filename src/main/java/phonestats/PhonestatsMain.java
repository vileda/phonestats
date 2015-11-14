package phonestats;

import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.Vertx;

public class PhonestatsMain {
	public static void main(String[] args) {
		VertxOptions options = new VertxOptions();
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				new PhonestatsMain().run(res.result());
			} else {
				System.out.println("Failed: " + res.cause());
			}
		});
	}

	public void run(Vertx vertx) {
		vertx.deployVerticle("phonestats.PhonestatsRouter");
	}
}

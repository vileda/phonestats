package phonestats;

import io.resx.core.EventStore;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSSocket;

public class WebsocketHandler implements Handler<SockJSSocket> {
	private final EventStore eventStore;

	public WebsocketHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void handle(SockJSSocket sockJSSocket) {

	}
}

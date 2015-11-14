package phonestats;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PhoneCallEventHandlerTest {
	@BeforeClass
	public static void beforeClass() throws InterruptedException {
		new PhonestatsMain().run(Vertx.vertx());
		Thread.sleep(1000);
	}

	@Test
	public void testPostCallEvent() throws Exception {
		String callId = "9820948236253789239";
		String userId = "testId";
		HttpResponse execute = postCallEvent(callId, userId);
		assertPostCallEvent(execute, userId);
	}

	private void assertPostCallEvent(HttpResponse execute, String userId) throws IOException {
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		assertThat(IOUtils.toString(execute.getEntity().getContent()),
				containsString(userId));
	}

	private HttpResponse postCallEvent(String callId, String userId) throws IOException {
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("callId", callId));
		return post("/api/event/" + userId, parameters);
	}

	@Test
	public void testGetDashboardAggregate() throws Exception {
		String userId = UUID.randomUUID().toString();

		for (int i = 0; i < 100; i++) {
			String callId = UUID.randomUUID().toString();
			postCallEvent(callId, userId);
		}

		HttpResponse execute = get("/api/aggregate/dashboard/" + userId);
		String actual = IOUtils.toString(execute.getEntity().getContent());
		assertThat(actual, execute.getStatusLine().getStatusCode(), is(200));
		final JsonObject jsonObject = new JsonObject(actual);
		assertThat(actual, jsonObject.getString("id"), is(userId));
		assertThat(actual, jsonObject.getInteger("incomingTotal"), is(100));
	}

	private HttpResponse post(String path, List<NameValuePair> params) throws IOException {
		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost("http://localhost:8080" + path);
		httpPost.setEntity(new UrlEncodedFormEntity(params));
		return httpclient.execute(httpPost);
	}

	private HttpResponse get(String path) throws IOException {
		HttpClient httpclient = HttpClientBuilder.create().build();
		String uri = "http://localhost:8080" + path;
		HttpGet httpGet = new HttpGet(uri);
		return httpclient.execute(httpGet);
	}
}
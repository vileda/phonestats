package phonestats;

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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PhoneCallEventHandlerTest {
	@BeforeClass
	public static void beforeClass() throws InterruptedException {
		new PhonestatsMain().run(Vertx.vertx());
		Thread.sleep(1000);
	}

	@Test
	public void testPostCallEvent() throws Exception {
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("callId", "9820948236253789239"));
		HttpResponse execute = post("/api/event/testId", parameters);
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		assertThat(IOUtils.toString(execute.getEntity().getContent()),
				CoreMatchers.containsString("testId"));
	}

	@Test
	public void testGetDashboardAggregate() throws Exception {
		HttpResponse execute = get("/api/aggregate/dashboard/testId");
		assertThat(execute.getStatusLine().getStatusCode(), is(200));
		String actual = IOUtils.toString(execute.getEntity().getContent());
		assertThat(actual, CoreMatchers.containsString("testId"));
		assertThat(actual, CoreMatchers.containsString("incomingTotal"));
		System.out.println(actual);
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
		System.out.println("HTTP fetching " + uri);
		return httpclient.execute(httpGet);
	}
}
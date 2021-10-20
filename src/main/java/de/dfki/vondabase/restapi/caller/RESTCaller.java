package de.dfki.vondabase.restapi.caller;

import com.google.gson.GsonBuilder;
import de.dfki.vondabase.utils.Listener;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

import static de.dfki.mlt.rudimant.agent.Agent.logger;


public class RESTCaller implements Listener<RESTMessage> {

  protected final GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
  private final ExecutorService pool = Executors.newFixedThreadPool(10);
  private final String koffiUrl;
  private Map<String, Object> _urls;

  public RESTCaller(Map<String, Object> configs) {
    builder.excludeFieldsWithoutExposeAnnotation();
    _urls = configs;
    koffiUrl = (String) configs.get("KoffiURI");
  }

  public boolean listenWR(RESTMessage q){
    Future<String> future = null;
    future = pool.submit(() -> {
      System.err.println("calling " + q);
      CloseableHttpResponse response = publish(q);
      if (response != null)
        return response.getStatusLine().toString();
      else
        return "444";
    });
    try {
      String statusLine =  future.get(60, TimeUnit.SECONDS);
      return statusLine.equals("HTTP/1.0 200 OK");
    } catch (InterruptedException e) {
      logger.warn("task interrupted", q);
    } catch (ExecutionException e) {
      logger.error(q + " execution exception", e);
    } catch (TimeoutException e) {
      logger.debug("future timed out", q);
    }
    return false;
  }

  @Override
  public void listen(RESTMessage q) {
    Future<String> future = null;
    future = pool.submit(() -> {
        CloseableHttpResponse response = publish(q);
       if (response != null)
        return response.getStatusLine().toString();
      else
        return "444";
    });
    try {

      future.get(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.warn("task interrupted", q);
    } catch (ExecutionException e) {
      logger.error(q + " execution exception", e);
    } catch (TimeoutException e) {
      logger.debug("future timed out", q);
    }
  }

  @Override
  public void free() {
    //nothing to do here
  }


  private CloseableHttpResponse publish(RESTMessage message) throws IOException {
    CloseableHttpClient client = HttpClientBuilder.create().build();
    CloseableHttpResponse response = null;
    HttpPost request;
    String endpoint = koffiUrl+_urls.get(message.endpoint);
    if ( message.arg != null)
      request = new HttpPost(endpoint + "/" + message.arg);
    else
      request = new HttpPost(endpoint);

    if (message.body != null){
      StringEntity requestEntity = new StringEntity(
              message.body,
              ContentType.APPLICATION_JSON);
      request.setEntity(requestEntity);
    }
    try {

      response = client.execute(request);
      client.close();
      return response;
    } catch (HttpHostConnectException e) {
      e.printStackTrace();
    }
    return response;
  }

}

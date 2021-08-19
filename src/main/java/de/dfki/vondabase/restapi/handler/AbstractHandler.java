package de.dfki.vondabase.restapi.handler;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.dfki.vondabase.restapi.HTTPMethods;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract public class AbstractHandler implements HttpHandler {

  private final String encoding = "UTF-8";

  protected final GsonBuilder builder = new GsonBuilder();
  protected ExecutorService pool = Executors.newFixedThreadPool(1);

  public int getRandomNumber(int min, int max) {
    return (int) ((Math.random() * (max - min)) + min);
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      if (exchange.getRequestMethod().equals(HTTPMethods.GET.toString()))
        handleGetRequest(exchange);
      else if (exchange.getRequestMethod().equals(HTTPMethods.POST.toString()))
        handlePostRequest(exchange);
      else
        sendResponse(405, exchange, "Method Not Allowed");
    } catch (InterruptedException e){
      sendResponse(500, exchange, "internal server error " + e.getMessage() + "\n"+ Arrays.toString(e.getStackTrace()));
      e.printStackTrace();
    }catch (Exception e){
      sendResponse(500, exchange, "internal server error " + e.getMessage() + "\n"+ Arrays.toString(e.getStackTrace()));
      throw e;
    }
  }

  protected abstract void handlePostRequest(HttpExchange exchange) throws IOException, InterruptedException;

  protected abstract void handleGetRequest(HttpExchange exchange);


  protected String bodyToString(InputStream body) throws IOException {
    StringWriter writer = new StringWriter();
    IOUtils.copy(body, writer);
    return writer.toString();
  }

  protected boolean isValidParam(String paramValue){
    if (paramValue == null)
      return false;
    boolean isValue = !(paramValue.equals("\"\"") || paramValue.equals("''"));
    return !paramValue.isEmpty() && isValue;
  }

  protected boolean isValidId(String paramValue){
    return isValidParam(paramValue) ;
  }

  protected void sendResponse(int code, HttpExchange t, String response) throws IOException {
    byte[] bs = response.getBytes(StandardCharsets.UTF_8);
    if(response.startsWith("http"))
      t.getResponseHeaders().set("Content-Type", "text/html; charset=" + encoding);
    else
      t.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
    t.sendResponseHeaders(code, bs.length);
    OutputStream os = t.getResponseBody();
    try {
      os.write(bs);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    os.flush();
    os.close();
  }

  protected Map<String, String> queryToMap(String query) {
    Map<String, String> result = new HashMap();
    if (query != null) {
      String[] var3 = query.split("&");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
        String param = var3[var5];
        String[] entry = param.split("=");
        if (entry.length > 1) {
          result.put(entry[0], entry[1]);
        } else {
          result.put(entry[0], "");
        }
      }
    }

    return result;
  }

}

package com.cc.servicenow;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class ServiceNowSimpleClient {
  
  
  /**
   * Returns the accessToken 
   * 
   */
  public String uploadAttachment(String url, String clientId, String clientSecret, String path)  {
    
    Encoder encoder = Base64.getEncoder();
    String encodedAuthHeader = encoder.encodeToString((clientId + ":" + clientSecret).getBytes());
    System.out.println(" Invokes uploadAttachment for file " + path);
    //Decoder decoder = Base64.getDecoder();
    //System.out.println(" >> " + new String(decoder.decode(encodedAuthHeader), StandardCharsets.ISO_8859_1));
    
    CloseableHttpClient httpclient = HttpClients.createDefault();
    CloseableHttpResponse response = null;
    
    HttpPost post = new HttpPost(url);
    post.setHeader("Authorization", "Basic " + encodedAuthHeader);
    post.setHeader("Accept", "application/json");
    post.setHeader("Content-Type", "application/x-www-form-urlencoded");
    //post.addHeader("Authorization", "Basic " + clientId + ":" + clientSecret);
    post.setHeader("Cache-Control", "no-cache");
    
    
    
    
    try {
      
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      
      StringEntity tableName = new StringEntity("table_name=sys_data_source");
      StringEntity tableSysId = new StringEntity("table_sys_id=43be0edadb75530029c2d0a1ca9619d5");
      FileEntity uploadFile = new FileEntity(new File(path));
        
      post.setEntity(tableName);
      post.setEntity(tableSysId);
      post.setEntity(uploadFile);
    } catch(UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    
    Header[] headers = post.getAllHeaders();
    
    try {
      
      // Create a custom response handler
      ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        
        @Override
        public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
          int status = response.getStatusLine().getStatusCode();
          System.out.println("  Response retrieved with code " + status);
          HttpEntity entity = response.getEntity();
          
          if (status >= 200 && status < 300) {
            
            Gson gson = new Gson();
            System.out.println(" ** " + gson.toJson(response));
            return EntityUtils.toString(entity);
          } else {
            //throw new ClientProtocolException("Unexpected response status: " + status);
            System.out.println("EIn " + response.getStatusLine().getReasonPhrase());
            Gson gson = new Gson();
            //System.out.println(" ** " + new Gson().toJson(response.getEntity()));
            return entity != null ? EntityUtils.toString(entity) : null;
          }
        }
      };
      
      //.writeRequest(System.out);
//post.getEntity().writeTo(System.out);
     // System.out.println("" + new Gson().toJson(post));
      return httpclient.execute(post, responseHandler);
      
    } catch(ClientProtocolException e) {
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      try {
        httpclient.close();
      } catch(IOException e) {
        
      }
      
    }
    
    return null;
    
  }
}

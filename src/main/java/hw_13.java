import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.json.simple.*;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class hw_13 {

    public static void main(String[] args) {
        try {
            MyHttpResponse  httpResponse;
            // task 1
            System.out.println("Task 1");
            JSONObject userObj = new JSONObject();
            userObj.put("name", "My New User");
            userObj.put("username", "myuser1");
            userObj.put("email", "test@test.biz");
            userObj.put("phone", "79472390472903742");
            userObj.put("website", "test.test.org");

            httpResponse = sendCreateRequest("https://jsonplaceholder.typicode.com/users/", userObj.toJSONString());
            System.out.println("__ Create NEW user ____");
            System.out.println("CREATE RESULT:");
            System.out.println("statusCode = " + httpResponse.getStatusCode());
            System.out.println("content = " + httpResponse.getContent());
            System.out.println("________________________");

            // try to update user id=1
            userObj.put("id", 1);
            httpResponse = sendUpdateRequest("https://jsonplaceholder.typicode.com/users/1", userObj.toJSONString());
            System.out.println("__ UPDATE user ID=1 ____");
            System.out.println("UPDATE RESULT:");
            System.out.println("statusCode = " + httpResponse.getStatusCode());
            System.out.println("content = " + httpResponse.getContent());
            System.out.println("________________________");

            httpResponse = sendDeleteRequest("https://jsonplaceholder.typicode.com/users/1");
            System.out.println("__ DELETE user ID=1 ____");
            System.out.println("DELETE RESULT:");
            System.out.println("statusCode = " + httpResponse.getStatusCode());
            System.out.println("content = " + httpResponse.getContent());
            System.out.println("________________________");

            httpResponse = sendGetRequest("https://jsonplaceholder.typicode.com/users");
            System.out.println("__ getting info about all users ___");
            System.out.println("GET RESULT:");
            System.out.println("statusCode = " + httpResponse.getStatusCode());
            System.out.println("content = " + httpResponse.getContent());
            System.out.println("________________________");

            httpResponse = sendGetRequest("https://jsonplaceholder.typicode.com/users/1");
            System.out.println("__ getting info about user ID=1 ____");
            System.out.println("GET RESULT:");
            System.out.println("statusCode = " + httpResponse.getStatusCode());
            System.out.println("content = " + httpResponse.getContent());
            System.out.println("________________________");

            httpResponse = sendGetRequest("https://jsonplaceholder.typicode.com/users?username="+URLEncoder.encode("Delphine", StandardCharsets.UTF_8.toString()));
            System.out.println("__ getting info about user with username 'Delphine'  ___");
            System.out.println("GET RESULT:");
            System.out.println("statusCode = " + httpResponse.getStatusCode());
            System.out.println("content = " + httpResponse.getContent());
            System.out.println("________________________");

            // task 2
            System.out.println("Task 2");
            String fileDir = System.getProperty("user.dir")+ File.separator;
            getCommentsOnLastPostForUserId (1, fileDir);

            // task 3

            System.out.println("Task 3");
            httpResponse = getUncompletedToDosForUserId(1);
            System.out.println("__ getting uncompleted TODOs for user 1  ___");
            System.out.println("GET RESULT:");
            System.out.println("statusCode = " + httpResponse.getStatusCode());
            System.out.println("content = " + httpResponse.getContent());
            System.out.println("________________________");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected static class MyHttpResponse{
        private String content;
        private int statusCode;

        MyHttpResponse(String content, int statusCode){
            this.content = content;
            this.statusCode = statusCode;
        }

        public String getContent(){
            return this.content;
        }

        public int getStatusCode(){
            return this.statusCode;
        }
    }

    public static void  getCommentsOnLastPostForUserId(int userId, String fileDir) throws IOException, ParseException{
        MyHttpResponse httpResponse = sendGetRequest("https://jsonplaceholder.typicode.com/users/"+userId+"/posts");

        JSONArray  jsonObject;
        JSONParser jsonParser=new  JSONParser();
        jsonObject=(JSONArray) jsonParser.parse(httpResponse.getContent());
        Long maxPostId = jsonObject.parallelStream().mapToLong(x -> (Long)((JSONObject)x).get("id")).max().orElseThrow(NoSuchElementException::new);

        String filePath = fileDir + "user-"+userId+"-post-"+maxPostId+"-comments.json";

        httpResponse = sendGetRequest("https://jsonplaceholder.typicode.com/comments/?postId="+maxPostId);
        writeFile(filePath, httpResponse.getContent());
        System.out.println("Comments saved to '"+filePath+"'");
    }

    public static MyHttpResponse getUncompletedToDosForUserId(int userId) throws IOException, ParseException{
        return sendGetRequest("https://jsonplaceholder.typicode.com/users/"+userId+"/todos?completed=false");

    }


    public static MyHttpResponse sendCreateRequest(String url, String json) throws UnsupportedEncodingException, IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(url);
        postRequest.setEntity(new StringEntity(json));
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        MyHttpResponse result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(postRequest)) {
            String content = EntityUtils.toString(httpResponse.getEntity());
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            result = new MyHttpResponse(content, statusCode);
        } catch (IOException e) {
            System.out.println("ERROR!!!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static MyHttpResponse sendUpdateRequest(String url, String json) throws UnsupportedEncodingException, IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPut putRequest = new HttpPut(url);
        putRequest.setEntity(new StringEntity(json));
        putRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        MyHttpResponse result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(putRequest)) {
            String content = EntityUtils.toString(httpResponse.getEntity());
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            result = new MyHttpResponse(content, statusCode);
        } catch (IOException e) {
            System.out.println("ERROR!!!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static MyHttpResponse sendGetRequest(String url) throws UnsupportedEncodingException, IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet postRequest = new HttpGet(url);
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        MyHttpResponse result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(postRequest)) {
            String content = EntityUtils.toString(httpResponse.getEntity());
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            result = new MyHttpResponse(content, statusCode);
        } catch (IOException e) {
            System.out.println("ERROR!!!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static MyHttpResponse sendDeleteRequest(String url) throws UnsupportedEncodingException, IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpDelete deleteRequest = new HttpDelete(url);
        deleteRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        MyHttpResponse result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(deleteRequest)) {
            String content = EntityUtils.toString(httpResponse.getEntity());
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            result = new MyHttpResponse(content, statusCode);
        } catch (IOException e) {
            System.out.println("ERROR!!!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    private static void writeFile(String path, String str)
            throws IOException {
        try (FileWriter file = new FileWriter(path)) {
            file.write(str);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

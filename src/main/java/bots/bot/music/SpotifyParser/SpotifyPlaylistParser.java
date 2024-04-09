package bots.bot.music.SpotifyParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyPlaylistParser {
    public static String tokenUpdate() {
        try {

            HttpClient httpClient = HttpClients.createDefault();
            HttpGet request = new HttpGet("https://open.spotify.com/?flow_ctx=423047e4-92a1-4f27-ab37-28a84a808ba2%3A1711913213%3A1711913213");

            // Set request headers
            request.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            request.addHeader("accept-language", "uk-UA,uk;q=0.9,en-US;q=0.8,en;q=0.7,ru;q=0.6");
            request.addHeader("cookie", "sp_m=ua-uk; sp_t=46dcbc93-7ae9-4696-b941-997405e94749; _gcl_au=1.1.881161868.1708977898; _scid=7bcd6726-2f23-4711-b120-9acbd771caeb; _cs_c=0; sp_gaid=0088fc5f6216b87b4ed179cba1059a6ea841d9b80b2a85e3eb5902; OptanonAlertBoxClosed=2024-03-05T12:18:12.672Z; sp_last_utm={\"utm_campaign\":\"your_account\",\"utm_medium\":\"menu\",\"utm_source\":\"spotify\"}; _sctr=1|1711144800000; _ga_S35RN5WNT2=GS1.1.1711148767.3.1.1711148776.0.0.0; _ga_S0T2DJJFZM=GS1.1.1711292289.1.0.1711292289.0.0.0; _scid_r=7bcd6726-2f23-4711-b120-9acbd771caeb; _cs_id=5bab0b73-15fd-a766-baf8-b6cf55c4b0b8.1708977898.4.1711292290.1711292290.1.1743141898546.1; LPVID=MwN2M0NzA2Zjg0Yjk0Yzg4; LPSID-2422064=bBocjlpdSHefP7vzB2aA2w; _ga_LJDH9SQRHZ=GS1.1.1711369010.3.1.1711369112.0.0.0; _ga_ZWRF3NLZJZ=GS1.1.1711382229.5.1.1711382231.0.0.0; sp_landing=https://open.spotify.com/playlist/3VKTzsJDypSIavEQS2kOw3?sp_cid=46dcbc93-7ae9-4696-b941-997405e94749&device=desktop&si=1e20d6f8bd304732; _gid=GA1.2.1912289838.1711826374; _gat_UA-5784146-31=1; _ga=GA1.2.105285912.1708977898; OptanonConsent=isGpcEnabled=0&datestamp=Sun+Mar+31+2024+16:26:56+GMT+0300+(Восточная+Европа,+летнее+время)&version=202309.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&landingPath=NotLandingPage&groups=s00:1,f00:1,m00:1,t00:1,i00:1,f11:1,m03:1&AwaitingReconsent=false&geolocation=UA;05; _ga_ZWG1NSHWD8=GS1.1.1711890846.24.1.1711891620.0.0.0; _gat=1; amp_389c1b=45de7701-da8d-45ff-b3bd-3190b9097ca6...1hqaaak6m.1hqab292m.0.0.0; sp_dc=AQDZiAp5IBJJ036VQXsufSgr_R1jKhoEV4hOsdn01iBfRn-nCdRknji8XNMOg4lbMqtJ0BTa_QYwM3AKhGHQQwYhoR9QQQszl9Ns3xXBDdq7cBEce5qP2eekC0CDIol6F_skLpZvE0rF-_AMGcEKRpJaLK17fkg; sp_key=46c13d4f-5eeb-4ca2-b0c0-06fcd0259d57");
            request.addHeader("referer", "https://accounts.spotify.com/");
            request.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"123\",\"Not:A-Brand\";v=\"8\",\"Chromium\";v=\"123\"");
            request.addHeader("sec-ch-ua-mobile", "?0");
            request.addHeader("sec-ch-ua-platform", "\"Windows\"");
            request.addHeader("sec-fetch-dest", "document");
            request.addHeader("sec-fetch-mode", "navigate");
            request.addHeader("sec-fetch-site", "same-site");
            request.addHeader("sec-fetch-user", "?1");
            request.addHeader("upgrade-insecure-requests", "1");
            request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");

            HttpResponse response = httpClient.execute(request);

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String inputLine;
            StringBuilder responseCont = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responseCont.append(inputLine);
            }
            in.close();

            Document doc = Jsoup.parse(String.valueOf(responseCont));

            // Select script elements
            Elements scripts = doc.select("script");
            Element scriptElement = doc.getElementById("session");
            String json = scriptElement.html();
            JSONObject jsonObject = new JSONObject(json);
            String accessToken = jsonObject.getString("accessToken");
            System.out.println(accessToken);
            return accessToken;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*public static String parseSoloTrack(String link) throws IOException {
        String accessToken = tokenUpdate();
        HttpClient httpClient = HttpClients.createDefault();

        // Create HttpClient instance
        HttpGet request = new HttpGet("https://open.spotify.com/track/"+parseTrackId(link));
        request.addHeader("accept", "application/json");
        request.addHeader("accept-language", "uk");
        request.addHeader("app-platform", "WebPlayer");
        request.addHeader("authorization", "Bearer "+accessToken);
        request.addHeader("client-token", "AADa6myIr2hkXDirRoAFDyYQncXkP9H8mIGNqVMJy0Jv5FSyYzWhdNL4an9cCcmtN254wR7dn/fiG7b94PNzHUplxefWSGoU4Z+VUUcgxyGYgghHy65pbZLnxgvs90ZCbVXZiXWAN07Wgv4DX6CgfpnoOAr7QeqA8JzwBzp+UC81DwTYpQF167sXbb/tp5WxvZ8ZN+CAC+pd0xtuMj7QxtG/A5+GO+lhoBmmlp4xmut5LXhiDYIC95stZON5jz5nAesH7RuGc/WcpSfmgshSMR6/wslAbvPBt8EFcgko1rehKTI=");
        request.addHeader("content-type", "application/json;charset=UTF-8");
        request.addHeader("origin", "https://open.spotify.com");
        request.addHeader("referer", "https://open.spotify.com/");
        request.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"123\", \"Not:A-Brand\";v=\"8\", \"Chromium\";v=\"123\"");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("sec-ch-ua-platform", "\"Windows\"");
        request.addHeader("sec-fetch-dest", "empty");
        request.addHeader("sec-fetch-mode", "cors");
        request.addHeader("sec-fetch-site", "same-site");
        request.addHeader("spotify-app-version", "1.2.35.95.ga97faefd");
        request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");

        HttpResponse response = httpClient.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String line;
        StringBuilder responseContent = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        String js = new String(responseContent);
    }*/

    public static ArrayList<String> parsePlaylist(String link, int compositionAmount) throws IOException {

        String accessToken = tokenUpdate();
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api-partner.spotify.com/pathfinder/v1/query?operationName=fetchPlaylist&variables=%7B%22uri%22%3A%22spotify%3Aplaylist%3A" + parsePlaylistId(link) +"%22%2C%22offset%22%3A0%2C%22limit%22%3A"+compositionAmount+"%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%2291d4c2bc3e0cd1bc672281c4f1f59f43ff55ba726ca04a45810d99bd091f3f0e%22%7D%7D");
        request.addHeader("accept", "application/json");
        request.addHeader("accept-language", "uk");
        request.addHeader("app-platform", "WebPlayer");
        request.addHeader("authorization", "Bearer "+accessToken);
        request.addHeader("client-token", "AADa6myIr2hkXDirRoAFDyYQncXkP9H8mIGNqVMJy0Jv5FSyYzWhdNL4an9cCcmtN254wR7dn/fiG7b94PNzHUplxefWSGoU4Z+VUUcgxyGYgghHy65pbZLnxgvs90ZCbVXZiXWAN07Wgv4DX6CgfpnoOAr7QeqA8JzwBzp+UC81DwTYpQF167sXbb/tp5WxvZ8ZN+CAC+pd0xtuMj7QxtG/A5+GO+lhoBmmlp4xmut5LXhiDYIC95stZON5jz5nAesH7RuGc/WcpSfmgshSMR6/wslAbvPBt8EFcgko1rehKTI=");
        request.addHeader("content-type", "application/json;charset=UTF-8");
        request.addHeader("origin", "https://open.spotify.com");
        request.addHeader("referer", "https://open.spotify.com/");
        request.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"123\", \"Not:A-Brand\";v=\"8\", \"Chromium\";v=\"123\"");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("sec-ch-ua-platform", "\"Windows\"");
        request.addHeader("sec-fetch-dest", "empty");
        request.addHeader("sec-fetch-mode", "cors");
        request.addHeader("sec-fetch-site", "same-site");
        request.addHeader("spotify-app-version", "1.2.35.95.ga97faefd");
        request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");

        HttpResponse response = httpClient.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String line;
        StringBuilder responseContent = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        String js = new String(responseContent);

        JSONObject jsonObject = new JSONObject(js);

        // Get the "items" array
        JSONArray itemsArray = jsonObject.getJSONObject("data")
                .getJSONObject("playlistV2")
                .getJSONObject("content")
                .getJSONArray("items");
        ArrayList<String> tracks = new ArrayList<>();
        // Iterate over each item and extract the name
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject item = itemsArray.getJSONObject(i);
            JSONObject trackData = item.getJSONObject("itemV2").getJSONObject("data");
            String itemName = trackData.getString("name");
            tracks.add(itemName);
        }
        return tracks;
    }

    public static String parsePlaylistId(String link){
        // Regular expression pattern to match the playlist ID
        Pattern pattern = Pattern.compile("/playlist/([a-zA-Z0-9]+)");
        Matcher matcher = pattern.matcher(link);

        // Find the first match
        if (matcher.find()) {
            // Return the captured group which corresponds to the playlist ID
            return matcher.group(1);
        } else {
            // If no match found, return null or throw an exception as needed
            return null;
        }
    }
    public static String parseTrackId(String spotifyTrackURL) {
        // https://open.spotify.com/track/0o1BqDDRcXxGmCSjZeHUcc?si=d4ad06cc38714fbe
        // Regular expression pattern to match the playlist ID
        Pattern pattern = Pattern.compile("/track/([a-zA-Z0-9]+)");
        Matcher matcher = pattern.matcher(spotifyTrackURL);

        // Find the first match
        if (matcher.find()) {
            // Return the captured group which corresponds to the playlist ID
            return matcher.group(1);
        } else {
            // If no match found, return null or throw an exception as needed
            return null;
        }
    }
    public static String parseSoloTrack(String link) throws IOException {
        String accessToken = tokenUpdate();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api-partner.spotify.com/pathfinder/v1/query?operationName=getTrack&variables=%7B%22uri%22:%22spotify:track:"+parseTrackId(link)+ "%22%7D&extensions=%7B%22persistedQuery%22:%7B%22version%22:1,%22sha256Hash%22:%22ae85b52abb74d20a4c331d4143d4772c95f34757bfa8c625474b912b9055b5c0%22%7D%7D");

        request.addHeader("accept", "application/json");
        request.addHeader("accept-language", "uk");
        request.addHeader("app-platform", "WebPlayer");
        request.addHeader("authorization", "Bearer " + accessToken);
        request.addHeader("client-token", "AACfXJ9QykyR9Kynrx6ljU97PTfr5Koy81/mazWfD0uTzKcqy+EyG/YqoYB3oMoXXn8lWllU6QMx5x2uVIMsz8UZkt9ETQlnLPwn8kpuFy3Yi6h/fXq/RcdLOWlgjbLBm9jur1Xs1nUk1qcXAfPhDtV7myg91fPdKVarsH+/uFhGo/UesBUIqKoZYSO5k3z2cLcj6PPt6DZsW/xAASG1uGJPjCVwQk21Ev1heXzTwXgWmkuxczvw40NEATkZsoCBgQs2hS/t5EO+yHp1blewadIBt5Tra8erd46WDV5tmkENawRrww==");
        request.addHeader("content-type", "application/json;charset=UTF-8");
        request.addHeader("origin", "https://open.spotify.com");
        request.addHeader("referer", "https://open.spotify.com/");
        request.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"123\", \"Not:A-Brand\";v=\"8\", \"Chromium\";v=\"123\"");
        request.addHeader("sec-ch-ua-mobile", "?0");
        request.addHeader("sec-ch-ua-platform", "\"Windows\"");
        request.addHeader("sec-fetch-dest", "empty");
        request.addHeader("sec-fetch-mode", "cors");
        request.addHeader("sec-fetch-site", "same-site");
        request.addHeader("spotify-app-version", "1.2.36.153.g37163574");
        request.addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");

        HttpResponse response = ((CloseableHttpClient) httpClient).execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        String line;
        StringBuilder responseContent = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        String js = new String(responseContent);
        // Further processing of response content
        JSONObject jsonObject = new JSONObject(js);

        // Get the "items" array

        String songName = jsonObject.getJSONObject("data")
                .getJSONObject("trackUnion")
                .getString("name");
        String author = jsonObject.getJSONObject("data")
                .getJSONObject("trackUnion")
                .getJSONObject("firstArtist")
                .getJSONArray("items")
                .getJSONObject(0)
                .getJSONObject("profile")
                .getString("name");

        System.out.println(songName + " by " + author);
        return songName + " by " + author;
    }

}

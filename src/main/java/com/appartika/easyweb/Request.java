package com.appartika.easyweb;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.appartika.easyweb.Extensions.join;

public class Request {

    public HttpExchange exchange;
    public String body;
    public URI uri;
    public String query;

    public Request(HttpExchange exc) {
        this.exchange = exc;
        try {
            this.body = this.body();
            this.uri = this.path();
            this.query = this.uri.getQuery();
        } catch (IOException e) {
            this.body = e.toString();
        }
    }

    public Headers headers() {
        return this.exchange.getRequestHeaders();
    }

    public String method() {
        return this.exchange.getRequestMethod();
    }

    private URI path() {
        return this.exchange.getRequestURI();
    }

    /** Delete a cookie via key in the browser memory with a time of expiry */
    public void removeCookie(String key) {
        this.exchange.getResponseHeaders().add("Set-Cookie", key + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;");
    }

    /** Writes a cookie in the browser memory with a time of expiry */
    public void cookie(String key, String value, int expires) {
        this.exchange.getResponseHeaders().add("Set-Cookie", key + "=" + value + "; " + "Max-Age=" + expires);
    }

    /** Writes a cookie in the browser memory */
    public void cookie(String key, String value) {
        this.exchange.getResponseHeaders().add("Set-Cookie", key + "=" + value);
    }

    /** Get a cookie via key */
    public String cookie(String key) {
        if (this.exchange.getRequestHeaders().containsKey("Cookie")) {
            List<String> cookies = this.exchange.getRequestHeaders().get("Cookie");
            if(cookies.size() != 0) {
                String cookieString = cookies.get(0);
                String[] cookiesCouples = cookieString.split(";");
                for (String s : cookiesCouples) {
                    if(s.split("=")[0].replaceAll(" ", "").equals(key)) {
                        return s.split("=")[1].replaceAll(" ", "");
                    }
                }
                return "null";
            } else {
                return "null";
            }
        } else {
            return "null";
        }
    }

    /**
     * Parse a cookie in Map
     * @return Map:<String, String>
     */
    public Map<String, String> cookies() {
        Map<String, String> cookies = new HashMap<String, String>();

        if (this.exchange.getRequestHeaders().containsKey("Cookie")) {
            List<String> cookieList = this.exchange.getRequestHeaders().get("Cookie");
            if (cookieList.size() <= 0) {
                cookies.put("null", "null");
                return cookies;
            } else {
                String cookieString = cookieList.get(0);
                String[] cookiesCouples = cookieString.split(";");
                for (String s : cookiesCouples) {
                    cookies.put(s.split("=")[0].replaceAll(" ", ""), s.split("=")[1].replaceAll(" ", ""));
                }
                return cookies;
            }
        } else {
            cookies.put("null", "null");
            return cookies;
        }
    }

    /**
     * Parse a POST body in x-www-form-urlencoded format
     * @return Map
     */
    public Map form() {
        Map<String, String> map = new HashMap<>();
        String[] bodies = this.body.split("&");
        for(int i = 0; i < bodies.length; i++) {
            map.put(bodies[i].split("=")[0], bodies[i].split("=")[1]);
        }
        return map;
    }

    /**
     * Parse a GET body (Contains a Map with all parameters).
     * @return Map
     */
    public Map queryParams() {
        Map<String, String> map = new HashMap<>();
        String[] bodies = this.query.split("&");
        for(int i = 0; i < bodies.length; i++) {
            map.put(bodies[i].split("=")[0], bodies[i].split("=")[1]);
        }
        return map;
    }

    /**
     * Get a query parameter via key
     * @return Map
     */
    public String queryParams(String key) {
        Map<String, String> map = new HashMap<>();
        String[] bodies = this.query.split("&");
        for(int i = 0; i < bodies.length; i++) {
            map.put(bodies[i].split("=")[0], bodies[i].split("=")[1]);
        }
        if(map.containsKey(key)) {
            return map.get(key);
        } else {
            return null;
        }
    }

    /**
     * Parse a POST body in any format to string
     * @return Map
     */
    private String body() throws IOException {
        int lengthOfBody = this.exchange.getRequestBody().available();
        String[] data = new String[lengthOfBody];

        for (int i = 0; i < lengthOfBody; i++) {
            data[i] = ((char) this.exchange.getRequestBody().read()) + "";
        }
        return join(data);
    }
}
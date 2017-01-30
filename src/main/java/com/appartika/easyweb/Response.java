package com.appartika.easyweb;

import static com.appartika.easyweb.Core.getFreemarkerCfg;
import static com.appartika.easyweb.Core.getStaticPath;
import static com.appartika.easyweb.Extensions.IOexcept;
import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.*;
import java.util.Map;

public class Response {

    public HttpExchange exchange;
    public Response(HttpExchange exc) {
        this.exchange = exc;
    }
    public void type(String type) {
        this.exchange.getResponseHeaders().add("Content-type", type);
    }

    /**
     * Send a data string to client.
     * @param data data string (html, json и.т.д)
     * @param status response status (200, 404 и.т.д)
     */
    public boolean send(String data, int status) {
        try {
            this.exchange.sendResponseHeaders(status, 0);
            PrintWriter outStreamObject = new PrintWriter(this.exchange.getResponseBody());
            outStreamObject.println(data);
            outStreamObject.close();
            this.exchange.close();
            return true;
        } catch (IOException ioe) {
            return IOexcept(ioe, this.exchange);
        }
    }

    /**
     * Run a freemarker template engine for rendering
     * @param pathToFile name of template in static file path
     * @param renderData Map with data for templating
     */
    public boolean render(String pathToFile, Map renderData) {
        try {
            this.exchange.sendResponseHeaders(200, 0);
            Writer out = new OutputStreamWriter(this.exchange.getResponseBody());
            Template temp = getFreemarkerCfg().getTemplate(pathToFile);
            try {
                temp.process(renderData, out);
            } catch (TemplateException te) {
                System.out.println(te);
            }
            out.close();
            this.exchange.close();
            return true;
        } catch (IOException ioe) {
            return IOexcept(ioe, this.exchange);
        }
    }

    /**
     * Send a file to client
     * @param fileName filename
     */
    public boolean sendFile(String fileName) { // V
        try {
            this.exchange.sendResponseHeaders(200, 0);
            PrintWriter outStreamObject = new PrintWriter(this.exchange.getResponseBody());
            BufferedReader br = new BufferedReader(new FileReader(getStaticPath() + fileName));
            String s;
            String data = "";
            while ((s = br.readLine()) != null) {
                data += s + "\r\n";
            }
            br.close();
            outStreamObject.println(data);
            outStreamObject.close();
            this.exchange.close();
            return true;

        } catch (IOException ioe) {
            return IOexcept(ioe, this.exchange);
        }
    }
    /**
     * Redirect client to other handler
     * @param URL URL of other handler
     */
    public boolean redirect(String URL) {
        this.exchange.getResponseHeaders().add("Location", URL);
        try {
            this.exchange.sendResponseHeaders(307, 0);
        } catch (IOException e) {
            return this.send("<body><META HTTP-EQUIV=REFRESH CONTENT=\"1; URL=" + URL + "\"></body>", 200);
        }
        return true;
    }
}
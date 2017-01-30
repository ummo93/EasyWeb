package com.appartika.easyweb;

import java.io.File;
import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.net.InetSocketAddress;
import java.util.function.BiPredicate;

/**
 * Main class with all core methods
 */
public class Core {

    /* Instance of request handler **/
    private static HttpHandler handler = new Manager();
    /* Config a freemarker **/
    private static Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
    /* Static files path **/
    private static String pathToStatic = "./";
    /* Public files path **/
    private static String pathToPublic = "./";

    static {
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
    }

    public static Configuration getFreemarkerCfg() {
        return cfg;
    }

    public static String getStaticPath() {
        return pathToStatic;
    }

    public static String getPublicPath() {
        return pathToPublic;
    }

    /**
     * Set path of public files, which accessible via GET
     * @param pathToPublicFolder relative path (ending with /)
     */
    public static void setPublicPath(String pathToPublicFolder) {
        if (pathToPublicFolder.endsWith("/")) {
            pathToPublic = pathToPublicFolder;
        } else {
            System.err.println("End char in path of static files string must ending with a \"/\"");
        }
    }

    /**
     * Set path of static files
     * Templating engine is refers to it.
     * @param pathToStaticFolder relative path (ending with /)
     */
    public static void setStaticPath(String pathToStaticFolder) {
        try {
            if (pathToStaticFolder.endsWith("/")) {
                cfg.setDirectoryForTemplateLoading(new File(pathToStaticFolder));
                pathToStatic = pathToStaticFolder;
            } else {
                System.err.println("End char in path of static files string must ending with a \"/\"");
            }
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        }

    }

    /**
     * Run a server via port
     * @param port the process will be launched via this port
     */
    public static void listen(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
            server.createContext("/", handler);
            server.start();
            System.out.println("Server started on port " + port + "\n");
        } catch(IOException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Run a server via port.
     * @param port the process will be launched via this port
     */
    public static void listen(String port) {
        try {
            if (port != null) {
                HttpServer server = HttpServer.create(new InetSocketAddress(Integer.valueOf(port)), 10);
                server.createContext("/", handler);
                server.start();
                System.out.println("Server started on port " + Integer.valueOf(port) + "\n");
            } else {
                HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10);
                server.createContext("/", handler);
                server.start();
                System.out.println("Server started on port " + 8080 + "\n");
            }
        } catch(IOException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Registers POST request handler
     * @param path request's context
     * @param lambdaExp expression to be executed after server get a request
     */
    public static void post(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "POST" };
        Manager.registrContext(options, lambdaExp);
    }

    /**
     * Registers GET request handler
     * @param path request's context
     * @param lambdaExp expression to be executed after server get a request
     */
    public static void get(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "GET" };
        Manager.registrContext(options, lambdaExp);
    }

    /**
     * Registers PUT request handler
     * @param path request's context
     * @param lambdaExp expression to be executed after server get a request
     */
    public static void put(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "PUT" };
        Manager.registrContext(options, lambdaExp);
    }

    /**
     * Registers PATCH request handler
     * @param path request's context
     * @param lambdaExp expression to be executed after server get a request
     */
    public static void patch(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "PATCH" };
        Manager.registrContext(options, lambdaExp);
    }

    /**
     * Registers DELETE request handler
     * @param path request's context
     * @param lambdaExp expression to be executed after server get a request
     */
    public static void delete(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "DELETE" };
        Manager.registrContext(options, lambdaExp);
    }

    /**
     * Registers request handler for all request types
     * @param path request's context
     * @param lambdaExp expression to be executed after server get a request
     */
    public static void all(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "ALL" };
        Manager.registrContext(options, lambdaExp);
    }
}
package com.appartika.easyweb;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import static com.appartika.easyweb.Core.getPublicPath;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class Manager implements HttpHandler {

    private static Map<String[], BiPredicate<Request, Response>> hierarchy = new HashMap<>();

    /**
     * It checks the public folder, and if the file name in the request is the same with
     * any file from the public directory - gives it to the client
     * @param exc exchange object
     */
    public static void enablePublic(HttpExchange exc) throws IOException {
        String requestFile = exc.getRequestURI().getPath().replace("/", "");
        String[] files = new File(getPublicPath()).list();
        for (String s : files) {
            if (requestFile.equals(s)) {
                File f = new File(getPublicPath() + s);
                if (f.isFile()) {
                    exc.sendResponseHeaders(200, 0);
                    PrintWriter out = new PrintWriter(exc.getResponseBody());
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String line;
                    String data = "";
                    while ((line = br.readLine()) != null) {
                        data += line + "\r\n";
                    }
                    br.close();
                    out.println(data);
                    out.close();
                    exc.close();
                } else {
                    try {
                        exc.sendResponseHeaders(404, 0);
                        PrintWriter outStreamObject = new PrintWriter(exc.getResponseBody());
                        outStreamObject.println("<body><h1>Not Found</h1><p>The requested URL <font color=\"blue\">/"
                                + requestFile + " </font>" + "was not found on this server.</p></body>");
                        outStreamObject.close();
                        exc.close();
                    } catch (IOException ioe) {
                        System.out.println(ioe);
                        PrintWriter outStreamObject = new PrintWriter(exc.getResponseBody());
                        outStreamObject.println(ioe.toString());
                        outStreamObject.close();
                        exc.close();
                    }
                }
                break;
            }
        }
        try {
            exc.sendResponseHeaders(404, 0);
            PrintWriter outStreamObject = new PrintWriter(exc.getResponseBody());
            outStreamObject.println("<body><h1>Not Found</h1><p>The requested URL <font color=\"blue\">/" + requestFile
                    + " </font>" + "was not found on this server.</p></body>");
            outStreamObject.close();
            exc.close();
        } catch (IOException ioe) {
            exc.close();
        }
    }

    public static void registrContext(String[] path, BiPredicate<Request, Response> p) {
        hierarchy.put(path, p);
    }

    @Override
    public void handle(HttpExchange exc) throws IOException {
        String reqPath = exc.getRequestURI().getPath();
        String reqMethod = exc.getRequestMethod();
        System.out.println(reqMethod + ": " + reqPath);
        hierarchy.forEach((String[] k, BiPredicate<Request, Response> v) -> {
            // Check whether the same request came and reported
            if ((k[0].equals(reqPath) && k[1].equals(reqMethod)) || ((k[0] + "/").equals(reqPath) && k[1].equals(reqMethod)) ||
                    (k[0].equals(reqPath) && k[1].equals("ALL")) || ((k[0] + "/").equals(reqPath) && k[1].equals("ALL")) ) {

                v.test(new Request(exc), new Response(exc));
            }
            // If neither method nor the context does not match, the check can be there *
            else if(( reqPath.split("/").length > 1 ) &&
                    (((reqPath.replace(reqPath.split("/")[reqPath.split("/").length - 1], "*").equals(k[0])) && k[1].equals(reqMethod)) ||
                            ((reqPath.replace(reqPath.split("/")[reqPath.split("/").length - 1], "*").equals(k[0] + "/")) && k[1].equals(reqMethod)))) {

                v.test(new Request(exc), new Response(exc));
            }
        });
        // Processes all other cases (files and invalid requests)
        enablePublic(exc);
    }
}
package framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import java.net.URLDecoder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Класс приложения, который включает все необходимые для работы методы.
 * Пример использования: 
 *<pre>
 * // Задаём обработчик
 * App app = new App();
 * // Задаём где у нас будут лежать статические файлы
 * app.setStaticPath("./src/main/resources/");
 * // Задаём где у нас будут лежать публичные файлы
 * app.setPublicPath("./src/main/resources/public/");
 * // Запускаем приложение на указанном порту
 * app.listen(5000);
 *</pre>
 * Требует определения обработчиков событий, например:
 * <pre>
 *  app.post("/post", (req, res) -> {
 *     res.send(out, "ok", 200);
 *     return true;
 *  });
 * </pre>
 */
public class App {
    /* Здесь хранится класс-обработчик запросов **/
    public static HttpHandler handler;
    /* Конфигурация для движка шаблонизатора **/
    public static Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
    /* Путь для статических файлов по умолчанию **/
    public static String pathToStatic = "./";
    /* Путь к публичной директории**/
    public static String pathToPublic = "./";

    public App() throws IOException {
        /** 
         * Создаёт экземпляр приложения
         * @param handler экземпляр обработчика запросов для маршрутизации оных
         */
        handler = new Manager();
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
    }

    public void setStaticPath(String pathToStaticFolder) throws IOException {
        /** 
         * Задаёт путь, где хранятся шаблоны и статические файлы. 
         * На него ссылается движок шаблонизатора.
         * @param pathToStaticFolder относительный путь к папке (заканчивается на /)
         */
        if (pathToStaticFolder.endsWith("/")) {
            cfg.setDirectoryForTemplateLoading(new File(pathToStaticFolder));
            pathToStatic = pathToStaticFolder;
        } else {
            throw new IOException("End char in path of static files string must ending with a \"/\"");
        }

    }

    public void setPublicPath(String pathToPublicFolder) throws IOException {
        /** 
         * Задаёт путь, где хранятся публичные файлы, доступ к которым разрешён с помощью GET. 
         * @param pathToPublicFolder относительный путь к папке (заканчивается на /)
         */
        if (pathToPublicFolder.endsWith("/")) {
            pathToPublic = pathToPublicFolder;
        } else {
            throw new IOException("End char in path of public folder string must ending with a \"/\"");
        }

    }

    public void listen(int port) throws IOException {
        /** 
         * Запускает сервер на определённом порту. 
         * @param port порт на котором будет запущен процесс
         */
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
        server.createContext("/", handler);
        server.start();
        System.out.println("Server started on port " + port + "\nPress any key to stop...");
        System.in.read();
        server.stop(0);
        System.out.println("Server stoped");
    }

    public static String join(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }

    private static void enablePublic(HttpExchange exc) throws IOException {
        /** 
         * Проверяет публичную папку, и если имя файла в запросе совпадает с каким-нибудь файлом из
         * публичной директории - отдаёт его клиенту
         * @param req объект запроса
         */

        String requestFile = exc.getRequestURI().getPath().replace("/", "");
        String[] files = new File(pathToPublic).list();
        for (String s : files) {
            if (requestFile.equals(s)) {
                File f = new File(pathToPublic + s);
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

    public void post(String path, BiPredicate<Request, Response> lambdaExp) {
        /** 
         * Регистрирует обрботчик POST запроса
         * @param path контекст запроса
         * @param lambdaExp выражение, которое будет выполнено при получении запроса
         */
        String[] options = { path, "POST" };
        Manager.registrContext(options, lambdaExp);
    }

    public void get(String path, BiPredicate<Request, Response> lambdaExp) {
        /** 
         * Регистрирует обрботчик GET запроса
         * @param path контекст запроса
         * @param lambdaExp выражение, которое будет выполнено при получении запроса
         */
        String[] options = { path, "GET" };
        Manager.registrContext(options, lambdaExp);
    }
    public static class Response {
    /**
     * Класс ответа от сервера, с помощью которого можно отправлять файлы,
     * строки, рендерить шаблоны и прочее.
     * Пример использования: 
    * <pre>
    *  app.post("/post", (req, <b>res</b>) -> {
    *     <b>res</b>.send(out, "ok", 200);
    *     return true;
    *  });
    * </pre>
    */
        public HttpExchange exchange;
        public Response(HttpExchange exc) {
            this.exchange = exc;
        }
        public boolean send(String data, int status) {
            /** 
             * Отправляет клиенту строку данных. 
             * @param data строка с данными (html, json и.т.д)
             * @param status статус ответа (200, 404 и.т.д)
             */
            try {
                this.exchange.sendResponseHeaders(status, 0);
                PrintWriter outStreamObject = new PrintWriter(this.exchange.getResponseBody());
                outStreamObject.println(data);
                outStreamObject.close();
                this.exchange.close();
                return true;
            } catch (IOException ioe) {
                System.out.println(ioe);
                PrintWriter outStreamObject = new PrintWriter(this.exchange.getResponseBody());
                outStreamObject.println(ioe.toString());
                outStreamObject.close();
                this.exchange.close();
                return false;
            }
        }
        public boolean render(String pathToFile, Map renderData) {
            /** 
             * Запускает движок шаблонизатора freemarker и рендерит заданный шаблон, передавая в него словарь
             * @param pathToFile имя файла шаблона
             * @param renderData словарь с данными для передачи в шаблон
             */
            try {
                this.exchange.sendResponseHeaders(200, 0);
                Writer out = new OutputStreamWriter(this.exchange.getResponseBody());
                Template temp = cfg.getTemplate(pathToFile);
                try {
                    temp.process(renderData, out);
                } catch (TemplateException te) {
                    System.out.println(te);
                }
                out.close();
                this.exchange.close();
                return true;
            } catch (IOException ioe) {
                System.out.println(ioe);
                PrintWriter outStreamObject = new PrintWriter(this.exchange.getResponseBody());
                outStreamObject.println(ioe.toString());
                outStreamObject.close();
                this.exchange.close();
                return false;
            }
        }
        public boolean sendFile(String fileName) { // V
        /** 
         * Отправляет клиенту файл. 
         * @param fileName имя файла (не путь)
         */
            try {
                this.exchange.sendResponseHeaders(200, 0);
                PrintWriter outStreamObject = new PrintWriter(this.exchange.getResponseBody());
                BufferedReader br = new BufferedReader(new FileReader(pathToStatic + fileName));
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
                System.out.println(ioe);
                PrintWriter outStreamObject = new PrintWriter(this.exchange.getResponseBody());
                outStreamObject.println(ioe.toString());
                outStreamObject.close();
                this.exchange.close();
                return false;
            }
        }
        public boolean redirect(String URL) {
        /** 
         * Перенаправляет поток к другому обработчику. 
         * @param URL контекст другого обработчика
         */
            this.send("<body><META HTTP-EQUIV=REFRESH CONTENT=\"1; URL=" + URL + "\"></body>", 200);
            return true;
        }
    }

    public static class Request {
        /**
         * Класс запроса к серверу, с помощью которого можно отправлять обработать запросы,
         * Пример использования: 
        * <pre>
        *  app.post("/post", (<b>req</b>, res) -> {
              String ref = req.headers().get("Referer")
        *     System.out.println(ref);
        *     return true;
        *  });
        * </pre>
        */
        public HttpExchange exchange;
        public Request(HttpExchange exc) {
            this.exchange = exc;
        }
        public Headers headers() {
            return this.exchange.getRequestHeaders();
        }
        public String method() {
            return this.exchange.getRequestMethod();
        }
        public String path() {
            return this.exchange.getRequestURI().toString();
        }
        public void cookies(String key, String value) {
        /** 
         * Записывает куки в память браузера
         */
            this.exchange.getResponseHeaders().set("Set-Cookie", key + "=" + value);
        }

        public Map<String, String> cookies() {
            /** 
             * Парсит куки в словарь, из которого удобно получить значения
             * @return словарь в котором содержаться строковые пары (ключ-значение) с куками
             */
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
        public Map form() throws IOException {
            /** 
             * Парсит в словарь тело POST запроса в формате x-www-form-urlencoded из [] байтов. 
             * @return словарь из которого можно получить передаваемые поля
             */
            int lengthOfBody = this.exchange.getRequestBody().available();
            String[] data = new String[lengthOfBody];
            for (int i = 0; i < lengthOfBody; i++) {
                data[i] = ((char) this.exchange.getRequestBody().read()) + "";
            }
            String dataString = join(data);
            //Парсим в словарь
            Map<String, String> map = new HashMap<String, String>();
            String[] couples = dataString.split("&");
            for (String s : couples) {
                map.put(URLDecoder.decode(s.split("=")[0], "UTF-8"), URLDecoder.decode(s.split("=")[1], "UTF-8"));
            }
            return map;
        }
        public String body() throws IOException {
            /** 
             * Парсит в строку тело POST запроса в любом формате из [] байтов. 
             * @return строка из которой можно извлечь передаваемые поля
             */
            int lengthOfBody = this.exchange.getRequestBody().available();
            String[] data = new String[lengthOfBody];

            for (int i = 0; i < lengthOfBody; i++) {
                data[i] = ((char) this.exchange.getRequestBody().read()) + "";
            }
            return join(data);
        }
    }

    static class Manager implements HttpHandler {
        public static Map<String[], BiPredicate<Request, Response>> hierarchy = new HashMap<String[], BiPredicate<Request, Response>>();

        public static void registrContext(String[] path, BiPredicate<Request, Response> p) {
            hierarchy.put(path, p);
        }

        @Override
        public void handle(HttpExchange exc) throws IOException {
            System.out.println(exc.getRequestMethod() + ": " + exc.getRequestURI());
            hierarchy.forEach((String[] k, BiPredicate<Request, Response> v) -> {
                if (k[0].equals(exc.getRequestURI().toString()) && k[1].equals(exc.getRequestMethod())) {
                    v.test(new Request(exc), new Response(exc));
                }
            });
            App.enablePublic(exc);
        }
    }
}
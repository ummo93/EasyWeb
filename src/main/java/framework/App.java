package framework;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.net.InetSocketAddress;
import java.net.URI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import java.security.MessageDigest;
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
 *     res.send("ok", 200);
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
        /* Экземпляр обработчика запросов для маршрутизации оных **/
        handler = new Manager();
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
    }

    /**
     * Задаёт путь, где хранятся шаблоны и статические файлы.
     * На него ссылается движок шаблонизатора.
     * @param pathToStaticFolder относительный путь к папке (заканчивается на /)
     */
    public void setStaticPath(String pathToStaticFolder) throws IOException {
        if (pathToStaticFolder.endsWith("/")) {
            cfg.setDirectoryForTemplateLoading(new File(pathToStaticFolder));
            pathToStatic = pathToStaticFolder;
        } else {
            throw new IOException("End char in path of static files string must ending with a \"/\"");
        }

    }

    /**
     * Задаёт путь, где хранятся публичные файлы, доступ к которым разрешён с помощью GET.
     * @param pathToPublicFolder относительный путь к папке (заканчивается на /)
     */
    public void setPublicPath(String pathToPublicFolder) throws IOException {
        if (pathToPublicFolder.endsWith("/")) {
            pathToPublic = pathToPublicFolder;
        } else {
            throw new IOException("End char in path of public folder string must ending with a \"/\"");
        }

    }

    /**
     * Запускает сервер на определённом порту.
     * @param port порт на котором будет запущен процесс
     */
    public void listen(int port) throws NumberFormatException, IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 10);
        server.createContext("/", handler);
        server.start();
        System.out.println("Server started on port " + port + "\n");
    }

    /**
     * Запускает сервер на определённом порту.
     * @param port порт на котором будет запущен процесс
     */
    public void listen(String port) throws NumberFormatException, IOException {
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
    }

    private static String join(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Проверяет публичную папку, и если имя файла в запросе совпадает с каким-нибудь файлом из
     * публичной директории - отдаёт его клиенту
     * @param exc объект запроса
     */
    private static void enablePublic(HttpExchange exc) throws IOException {
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

/** ------------------------- Шифрование и безопасность ----------------------------- */
    /**
     * Шифрование строки в md5 хеш, затем - конкатенация кеша с солью и шифрование
     * этого всего в sha1
     * @param str строка, которую будем шифровать
     * @param salt подмешиваемая соль
     * @return зашифрованная строка
     */
    public static String encrypt(String str, String salt) {
        return App.sha1(App.MD5(str) + salt);
    }
    /**
     * Шифрование строки в sha1 хеш
     * @param param строка, которую будем шифровать
     * @return sha1 хеш
     */
    public static String sha1(String param) {
        try {
            MessageDigest SHA = MessageDigest.getInstance("SHA-1");
            SHA.reset();
            SHA.update(param.getBytes("UTF-8"), 0, param.length());
            byte[] sha1hash = SHA.digest();
            return bytesToHexStr(sha1hash);
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            System.out.println(e);
            return "null";
        }
    }
    /**
     * Шифрование строки в md5 хеш
     * @param md5 строка, которую будем шифровать
     * @return md5 хеш
     */
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return "null";
        }
    }
    /** Преобразование байтового массива в hex-строку */
    private static String bytesToHexStr(byte[] raw) {
        char[] kDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        int length = raw.length;
        char[] hex = new char[length * 2];
        for (int i = 0; i < length; i++) {
            int value = (raw[i] + 256) % 256;
            int highIndex = value >> 4;
            int lowIndex = value & 0x0f;
            hex[i * 2 + 0] = kDigits[highIndex];
            hex[i * 2 + 1] = kDigits[lowIndex];
        }
        return new String(hex);
    }
/** --------------------------------------------------------------------------------- */

    /**
     * Регистрирует обрботчик POST запроса
     * @param path контекст запроса
     * @param lambdaExp выражение, которое будет выполнено при получении запроса
     */
    public void post(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "POST" };
        Manager.registrContext(options, lambdaExp);
    }

    /**
     * Регистрирует обрботчик GET запроса
     * @param path контекст запроса
     * @param lambdaExp выражение, которое будет выполнено при получении запроса
     */
    public void get(String path, BiPredicate<Request, Response> lambdaExp) {
        String[] options = { path, "GET" };
        Manager.registrContext(options, lambdaExp);
    }

    private static boolean IOexcept(IOException ioe, HttpExchange exc) {
        System.out.println(ioe);
        PrintWriter outStreamObject = new PrintWriter(exc.getResponseBody());
        outStreamObject.println(ioe.toString());
        outStreamObject.close();
        exc.close();
        return false;
    }

    public static class Response {
    /**
     * Класс ответа от сервера, с помощью которого можно отправлять файлы,
     * строки, рендерить шаблоны и прочее.
     * Пример использования: 
    * <pre>
    *  app.post("/post", (req, <b>res</b>) -> {
    *     <b>res</b>.send("ok", 200);
    *     return true;
    *  });
    * </pre>
    */
        public HttpExchange exchange;
        public Response(HttpExchange exc) {
            this.exchange = exc;
        }
        public void type(String type) {
            this.exchange.getResponseHeaders().add("Content-type", type);
        }
        /**
         * Отправляет клиенту строку данных.
         * @param data строка с данными (html, json и.т.д)
         * @param status статус ответа (200, 404 и.т.д)
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
         * Запускает движок шаблонизатора freemarker и рендерит заданный шаблон, передавая в него словарь
         * @param pathToFile имя файла шаблона
         * @param renderData словарь с данными для передачи в шаблон
         */
        public boolean render(String pathToFile, Map renderData) {
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
                return IOexcept(ioe, this.exchange);
            }
        }

        /**
         * Отправляет клиенту файл.
         * @param fileName имя файла (не путь)
         */
        public boolean sendFile(String fileName) { // V
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
                return IOexcept(ioe, this.exchange);
            }
        }
        /**
         * Перенаправляет поток к другому обработчику.
         * @param URL контекст другого обработчика
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
        /** Записывает куки в память браузера с временем истечения */
        public void removeCookie(String key) {
            this.exchange.getResponseHeaders().add("Set-Cookie", key + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;");
        }
        /** Записывает куки в память браузера с временем истечения */
        public void cookie(String key, String value, int expires) {
            this.exchange.getResponseHeaders().add("Set-Cookie", key + "=" + value + "; " + "Max-Age=" + expires);
        }
        /** Записывает куки в память браузера */
        public void cookie(String key, String value) {
            this.exchange.getResponseHeaders().add("Set-Cookie", key + "=" + value);
        }
        /** Полуает доступ к куки по имени */
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
         * Парсит куки в словарь, из которого удобно получить значения
         * @return словарь в котором содержаться строковые пары (ключ-значение) с куками
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
         * Парсит в словарь тело POST запроса в формате x-www-form-urlencoded из [] байтов.
         * @return словарь из которого можно получить передаваемые поля
         */
        public Map form() throws IOException {
            Map<String, String> map = new HashMap<String, String>();
            String[] bodies = this.body.split("&");
            for(int i = 0; i < bodies.length; i++) {
                map.put(bodies[i].split("=")[0], bodies[i].split("=")[1]);
            }
            return map;
        }
        /**
         * Парсит в строку тело POST запроса в любом формате из [] байтов.
         * @return строка из которой можно извлечь передаваемые поля
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

    static class Manager implements HttpHandler {
        public static Map<String[], BiPredicate<Request, Response>> hierarchy = new HashMap<String[], BiPredicate<Request, Response>>();

        public static void registrContext(String[] path, BiPredicate<Request, Response> p) {
            hierarchy.put(path, p);
        }

        @Override
        public void handle(HttpExchange exc) throws IOException {
            System.out.println(exc.getRequestMethod() + ": " + exc.getRequestURI());
            hierarchy.forEach((String[] k, BiPredicate<Request, Response> v) -> {
                if (k[0].equals(exc.getRequestURI().getPath()) && k[1].equals(exc.getRequestMethod())) {
                    v.test(new Request(exc), new Response(exc));
                }
            });
            App.enablePublic(exc);
        }
    }
}
import java.io.*;
import java.util.*;
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
import db.Database;

/**
 * Класс приложения, который включает все необходимые для работы методы.
 * Пример использования: 
 *<pre>
 * // Задаём обработчик
 * App app = new App(new Router(), new Database(Environment.getEnv()));
 * // Задаём где у нас будут лежать статические файлы
 * app.setStaticPath("./src/main/resources/");
 * // Задаём где у нас будут лежать публичные файлы
 * app.setPublicPath("./src/main/resources/public/");
 * // Запускаем приложение на указанном порту
 * app.listen(5000);
 *</pre>
 * Требует определения статического класса, реализующего интерфейс HttpHandler, например:
 * <pre>
 * static class Router implements HttpHandler {
 *       <p>@Override</p>
 *       public void handle(HttpExchange exc) throws IOException {
 *           // Обработка GET
 *           if(exc.getRequestMethod().equals("GET")) {
 *              // ... ваш код
 *           }
 *       }
 *   }
 * </pre>
 */
public class App {
    /* Здесь хранится класс-обработчик запросов **/
    public HttpHandler handler;
    /* Конфигурация для движка шаблонизатора **/
    public static Configuration cfg = new Configuration(Configuration.VERSION_2_3_25);
    /* Путь для статических файлов по умолчанию **/
    public static String pathToStatic = "./";
    /* Путь к публичной директории**/
    public static String pathToPublic = "./";

    public App(HttpHandler handler) throws IOException {
        /** 
         * Создаёт экземпляр приложения без БД 
         * @param handler экземпляр обработчика запросов для маршрутизации оных
         */
        this.handler = handler;
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
        if(pathToStaticFolder.endsWith("/")) {
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
        if(pathToPublicFolder.endsWith("/")) {
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
    public static void sendFile(HttpExchange req, String fileName) throws IOException {
        /** 
         * Отправляет клиенту файл. 
         * @param req объект запроса
         * @param fileName имя файла (не путь)
         */
        req.sendResponseHeaders(200, 0);
        PrintWriter outStreamObject = new PrintWriter(req.getResponseBody());
        BufferedReader br = new BufferedReader(new FileReader(pathToStatic + fileName));
        String s;
        String data = "";
        while((s=br.readLine()) != null) {
            data += s + "\r\n";
        }
        br.close();
        outStreamObject.println(data);
        outStreamObject.close();
        req.close();
    }
    public static void send(HttpExchange req, String data, int status) throws IOException {
        /** 
         * Отправляет клиенту строку данных. 
         * @param req объект запроса
         * @param data строка с данными (html, json и.т.д)
         * @param status статус ответа (200, 404 и.т.д)
         */
        req.sendResponseHeaders(status, 0);
        PrintWriter outStreamObject = new PrintWriter(req.getResponseBody());
        outStreamObject.println(data);
        outStreamObject.close();
        req.close();
    }
    public static Map parseUrlEncoded(HttpExchange req) throws IOException {
        /** 
         * Парсит в словарь тело POST запроса в формате x-www-form-urlencoded из [] байтов. 
         * @param req объект запроса
         * @return словарь из которого можно получить передаваемые поля
         */
        int lengthOfBody = req.getRequestBody().available();
        String[] data = new String[lengthOfBody];
        for(int i = 0; i < lengthOfBody; i++) {
            data[i] = ((char) req.getRequestBody().read()) + "";
        }
        String dataString = join(data);
        //Парсим в словарь
        Map<String, String> map = new HashMap<String, String>();
        String[] couples = dataString.split("&");
        for(String s : couples) {
            map.put(URLDecoder.decode(s.split("=")[0], "UTF-8"), URLDecoder.decode(s.split("=")[1], "UTF-8"));
        }
        return map;
    }
    public static String join(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }
    public static String parseBodyToString(HttpExchange req) throws IOException {
        /** 
         * Парсит в строку тело POST запроса в любом формате из [] байтов. 
         * @param req объект запроса
         * @return строка из которой можно извлечь передаваемые поля
         */
        int lengthOfBody = req.getRequestBody().available();
        String[] data = new String[lengthOfBody];
        
        for(int i = 0; i < lengthOfBody; i++) {
             data[i] = ((char) req.getRequestBody().read()) + "";
        }
        return join(data);
    }
    public static void render(HttpExchange req, String pathToFile, Map renderData) throws IOException {
        /** 
         * Запускает движок шаблонизатора freemarker и рендерит заданный шаблон, передавая в него словарь
         * @param req объект запроса
         * @param pathToFile имя файла шаблона
         * @param renderData словарь с данными для передачи в шаблон
         */
        req.sendResponseHeaders(200, 0);
        Writer out = new OutputStreamWriter(req.getResponseBody());
        Template temp = cfg.getTemplate(pathToFile);
        try {
            temp.process(renderData, out);
        } catch (TemplateException te) {
            System.out.println(te);
        }
        out.close();
        req.close();
    }
    public static void setCookie(HttpExchange req, String key, String value) {
        /** 
         * Записывает куки в память браузера
         */
        req.getResponseHeaders().set("Set-Cookie", key + "=" + value);
    }
    public static Map<String, String> getCookies(HttpExchange req) throws IOException {
        /** 
         * Парсит куки в словарь, из которого удобно получить значения
         * @param req объект запроса
         * @return словарь в котором содержаться строковые пары (ключ-значение) с куками
         */
        Map<String, String> cookies = new HashMap<String, String>();

        if(req.getRequestHeaders().containsKey("Cookie")) {
            List<String> cookieList = req.getRequestHeaders().get("Cookie");
            if(cookieList.size() <= 0) {
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
    public static void enablePublic(HttpExchange exc) throws IOException {
        /** 
         * Проверяет публичную папку, и если имя файла в запросе совпадает с каким-нибудь файлом из
         * публичной директории - отдаёт его клиенту
         * @param req объект запроса
         */
        
        String requestFile = exc.getRequestURI().getPath().replace("/", "");
        String[] files = new File(pathToPublic).list();
        for(String s : files) {
            if(requestFile.equals(s)) {
                File f = new File(pathToPublic + s);
                if(f.isFile()) {
                    exc.sendResponseHeaders(200, 0);
                    PrintWriter out = new PrintWriter(exc.getResponseBody());
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String line;
                    String data = "";
                    while((line=br.readLine()) != null) {
                        data += line + "\r\n";
                    }
                    br.close();
                    out.println(data);
                    out.close();
                    exc.close();
                } else {
                    send(exc, "<body><h1>Not Found</h1><p>The requested URL <font color=\"blue\">/"+ requestFile +" </font>" + 
        "was not found on this server.</p></body>", 404);
                }
                break;
            }
        }
        send(exc, "<body><h1>Not Found</h1><p>The requested URL <font color=\"blue\">/"+ requestFile +" </font>" + 
        "was not found on this server.</p></body>", 404);
    }
    public static void redirect(HttpExchange exc, String URL) throws IOException {
        send(exc, "<body><META HTTP-EQUIV=REFRESH CONTENT=\"1; URL="+URL+"\"></body>", 200);
    }
}
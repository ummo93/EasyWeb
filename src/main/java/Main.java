import java.io.*;
import java.sql.SQLException;
import com.sun.net.httpserver.*;
import java.util.*;
import db.Database;

public class Main {

    public static Database db = new Database(Environment.getEnv());

    public static void main(String[] args) throws Exception {
        // Задаём обработчик
        App app = new App(new Router());
        // Задаём где у нас будут лежать статические файлы
        app.setStaticPath("./src/main/resources/");
        // Задаём где у нас будут лежать публичные файлы
        app.setPublicPath("./src/main/resources/public/");
        // Запускаем приложение на указанном порту
        app.listen(5000);
    }

    static class Router implements HttpHandler {
        @Override
        public void handle(HttpExchange exc) throws IOException {
            System.out.println(exc.getRequestMethod() + ": " + exc.getRequestURI());
            Headers headers = exc.getResponseHeaders();
            // Обработка GET
            if(exc.getRequestMethod().equals("GET")) {
               switch(exc.getRequestURI().toString()) {
                    // Отправка html
                    case "/":
                        Map<String, String> cookies = App.getCookies(exc); // Здесь забираем куки в словарь
                        if(cookies.containsKey("login") && cookies.get("login").equals("true")) {
                            headers.set("Content-type", "text/html");
                            Map templatingData = new HashMap();
                            templatingData.put("user", "Dima");
                            App.render(exc, "index.ftl", templatingData); //Здесь используем шаблонизатор
                        } else {
                            App.redirect(exc, "/login");
                        }
                        break;
                    case "/login":
                        headers.set("Content-type", "text/html");
                        App.sendFile(exc, "login.html");
                        break;    
                    case "/void":
                        try {
                            // Пример работы с базой
                            db.addRecord("title", "description", "imageURL", "shortDescription", "autor");
                        } catch (SQLException se) {
                            System.out.println(se);
                        }
                        
                        headers.set("Content-type", "text/html");
                        App.sendFile(exc, "login.html"); //Здесь отправляем статический html
                        break;
                    default:
                        //Обязательно должна быть функция, открывающая доступ к файлам public
                        App.enablePublic(exc);
                        break;
                }
            }
            // Обработка POST
            if(exc.getRequestMethod().equals("POST")) {
                
                switch(exc.getRequestURI().toString()) {
                    case "/":
                        headers.set("Content-type", "text/html");
                        Map body = App.parseUrlEncoded(exc);
                        App.send(exc, body.toString(), 200); //Здесь парсим ответ как словарь
                        break;
                    case "/json":
                        headers.set("Content-type", "text/html");
                        App.send(exc, App.parseBodyToString(exc), 200); //Здесь парсим ответ как строку
                        break;
                    case "/login":
                        Map credentials = App.parseUrlEncoded(exc);
                        if(credentials.get("pass").equals("12345") && credentials.get("login").equals("user")) {
                            App.setCookie(exc, "login", "true"); // Здесь запись куков
                            App.redirect(exc, "/");
                        } else {
                            App.send(exc, "<body><h1>Not Found</h1></body>", 404);
                        }
                        break;    
                    default:
                        App.send(exc, "<body><h1>Not Found</h1></body>", 404);
                        break;    
                }
                
            }

            if(!exc.getRequestMethod().equals("POST") && !exc.getRequestMethod().equals("GET")) {
                // Обработка остальных типов запросов
                App.send(exc, "404, not found handlers for " + exc.getRequestMethod() + " request", 404);
            }
        }
    }
}

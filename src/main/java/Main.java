import java.io.*;
import com.sun.net.httpserver.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Задаём обработчик
        App app = new App(new Router());
        // Задаём где у нас будут лежать статические файлы
        app.setStaticPath("./src/main/resources/");
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
                        headers.set("Content-type", "text/html");
                        App.setCookie(exc, "name", "test"); // Здесь запись куков
                        Map templatingData = new HashMap();
                        templatingData.put("user", "Dima");
                        App.render(exc, "index.ftl", templatingData); //Здесь используем шаблонизатор
                        break;
                    case "/void":
                        Map<String, String> cookies = App.getCookies(exc); // Здесь забираем куки в словарь
                        headers.set("Content-type", "text/html");
                        App.sendFile(exc, "index2.html"); //Здесь отправляем статический html
                        break;
                    // Отправка json    
                    case "/users": 
                        headers.set("Content-type", "application/json");
                        App.sendFile(exc, "users.json"); //Здесь отправляем json файл
                        break;
                    default:
                        App.send(exc, "404, not found", 404);
                        break;    
                }
            }
            // Обработка POST
            if(exc.getRequestMethod().equals("POST")) {
                
                switch(exc.getRequestURI().toString()) {
                    // Отправка html
                    case "/":
                        headers.set("Content-type", "text/html");
                        Map body = App.parseUrlEncoded(exc);
                        App.send(exc, body.toString(), 200); //Здесь парсим ответ как словарь
                        break;
                    case "/json":
                        headers.set("Content-type", "text/html");
                        App.send(exc, App.parseBodyToString(exc), 200); //Здесь парсим ответ как строку
                        break;
                    default:
                        App.send(exc, "404, not found", 404);
                        break;    
                }
                
            } else {
                // Обработка остальных типов запросов
                App.send(exc, "404, not found handlers for " + exc.getRequestMethod() + " request", 404);
            }
        }
    }
}

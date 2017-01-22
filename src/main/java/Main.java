import com.sun.net.httpserver.*;
import java.util.*;
import framework.App;

public class Main {

    public static void main(String[] args) throws Exception {
        // Задаём обработчик
        App app = new App();
        // Задаём где у нас будут лежать статические файлы
        app.setStaticPath("./src/main/resources/");
        // Задаём где у нас будут лежать публичные файлы
        app.setPublicPath("./src/main/resources/public/");

        app.get("/", out -> {
            out.getResponseHeaders().set("Content-type", "text/html");
            Map templatingData = new HashMap();
            templatingData.put("user", "Dima");
            app.render(out, "index.ftl", templatingData); //Рендерим шаблон
            return true;
        });

        app.post("/post", out -> {
            app.send(out, "ok", 200);
            return true;
        });

        // Запускаем приложение на указанном порту
        app.listen(5000);
    }
}

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

        app.get("/", (req, res) -> {
            Map<String, String> templatingData = new HashMap<String, String>();
            templatingData.put("user", "Dima");
            res.render("index.ftl", templatingData); //Рендерим шаблон
            return true;
        });

        app.post("/post", (req, res) -> {
            res.send("ok", 200);
            return true;
        });

        // Запускаем приложение на указанном порту
        app.listen(5000);
    }
}

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import framework.App;

public class Main {

    public static void main(String[] args) throws IOException {
        // Задаём обработчик
        App app = new App();
        // Задаём где у нас будут лежать статические файлы
        app.setStaticPath("./src/main/resources/");
        // Задаём где у нас будут лежать публичные файлы
        app.setPublicPath("./src/main/resources/public/");

        app.get("/", (req, res) -> {
            //Пример рендеринга шаблона;
            Map<String, String> data = new HashMap<String, String>();
            data.put("user", "GitHub user");
            return res.render("index.ftl", data);
        });

        // Запускаем приложение на указанном порту
        app.listen(System.getenv("PORT"));
    }
}
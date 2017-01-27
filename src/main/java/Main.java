import java.io.IOException;
import framework.App;

public class Main {

    public static void main(String[] args) throws IOException {
        // Создаём экземпляр приложения
        App app = new App();
        // Задаём где у нас будут лежать статические файлы
        app.setStaticPath("./src/main/resources/");
        // Задаём где у нас будут лежать публичные файлы
        app.setPublicPath("./src/main/resources/public/");

        // Регистрируем обработчик главной стрнаицы на метод mainPage()
        app.get("/", Controller::mainPage);

        app.get("/rest", (req, res) -> {
            Greetings hello = new Greetings("hello!");
            res.type("application/json");
            return res.send(hello.toJson(), 200);
        });

        // Запускаем приложение на указанном порту
        app.listen(System.getenv("PORT"));
    }
}
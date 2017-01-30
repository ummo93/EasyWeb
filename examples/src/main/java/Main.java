import com.appartika.easyweb.App;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // Создаём экземпляр приложения
        App app = new App();
        // Задаём где у нас будут лежать статические файлы
        app.setStaticPath("./examples/src/main/resources/");
        // Задаём где у нас будут лежать публичные файлы
        app.setPublicPath("./examples/src/main/resources/public/");

        // Регистрируем обработчик главной стрнаицы на метод mainPage()
        app.get("/", Controller::mainPage);

        // Демонстрация некоторых других возможностей микрофреймворка
        app.post("/rest/*", (req, res) -> res.send("Hello world!", 200));

        app.put("/put", (req, res) -> res.send("This is a put request!", 200));

        app.patch("/patch", (req, res) -> res.send("This is a patch request!", 200));

        app.delete("/delete", (req, res) -> res.send("This is a delete request!", 200));

        app.all("/all", (req, res) -> res.sendFile("examples/src/main/resources/login.html"));

        app.get("/rest", (req, res) -> {
            Greetings hello = new Greetings("hello!");
            res.type("application/json");
            return res.send(hello.toJson(), 200);
        });

        // Запускаем приложение на указанном порту
        app.listen(System.getenv("PORT"));
    }
}
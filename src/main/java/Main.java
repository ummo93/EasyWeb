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
        app.get("/", (req, res) -> Controller.mainPage(req, res));

        // Запускаем приложение на указанном порту
        app.listen(System.getenv("PORT"));
    }
}
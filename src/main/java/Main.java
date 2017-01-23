import java.io.IOException;
import framework.App;
import framework.Storage;

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

        // Регистрируем обработчик страницы длогина
        app.get("/login", (req, res) -> Authentication.loginPage(req, res));

        // Регистрируем обработчик формы входа
        app.get("/logon", (req, res) -> Authentication.logIn(req, res));

        // Выход
        app.get("/logout", (req, res) -> Authentication.logOut(req, res));

        // Создание дефолтной конфигурации хранилища
        Storage.config();

        // Запускаем приложение на указанном порту
        app.listen(System.getenv("PORT"));
    }
}
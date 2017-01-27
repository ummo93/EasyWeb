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
        app.get("/", Controller::mainPage);

        // Регистрируем обработчик страницы длогина
        app.get("/login", Authentication::loginPage);

        // Регистрируем обработчик формы входа
        app.get("/logon", Authentication::logIn);

        // Выход
        app.get("/logout", Authentication::logOut);

        // Создание дефолтной конфигурации хранилища
        Storage.config();

        // Запускаем приложение на указанном порту
        app.listen(System.getenv("PORT"));
    }
}
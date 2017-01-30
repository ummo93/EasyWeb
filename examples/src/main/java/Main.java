import static com.appartika.easyweb.Core.*;

public class Main {

    public static void main(String[] args) {

        // Set path for static files (.ftl, .html, etc...)
        setStaticPath("./examples/src/main/resources/");
        // Set path for public files
        setPublicPath("./examples/src/main/resources/public/");

        // Main page handler with controller mainPage()
        get("/", Controller::mainPage);

        post("/rest/*", (req, res) -> res.send("Hello world!", 200));

        put("/put", (req, res) -> res.send("This is a put request!", 200));

        patch("/patch", (req, res) -> res.send("This is a patch request!", 200));

        delete("/delete", (req, res) -> res.send("This is a delete request!", 200));

        all("/all", (req, res) -> {
            res.type("text/html");
            return res.sendFile("login.html");
        });

        // Run application on port from .env
        listen(System.getenv("PORT"));
    }
}
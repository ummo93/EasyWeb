# EasyWeb

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/ummo93/EasyWeb)

EasyWeb is a easiest web framework with a minimal number of dependencies inspired ExpressJS.
#### Requirements and dependencies
  - Maven
  - Freemarker (http://freemarker.org/)
#### Getting started
```sh
$ mvn install
$ java -jar target/easy_web.jar
```
#### Minimal configuration
```java
import java.io.IOException;
import framework.App;

public class Main {
    public static void main(String[] args) throws IOException {
        App app = new App();
        
        app.get("/", (req, res) -> {
            return res.send("ok", 200);
        });

        app.listen(5000);
    }
}
```

### Todos

 - Documentation for using

License
----

MIT

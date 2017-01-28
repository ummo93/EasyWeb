# EasyWeb (modern wrapper for com.sun.net.httpserver)

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/ummo93/EasyWeb)

EasyWeb is easiest web framework with a minimal number of dependencies inspired by ExpressJS. Founded on com.sun.net.httpserver package.
#### Requirements and dependencies
  - Java 8
  - Maven
  - Freemarker (http://freemarker.org/)
  
#### Installation

```sh
git clone https://github.com/ummo93/EasyWeb.git
```
Then, go to EasyWeb/target folder. Copy easyweb.jar file to libs folder in your project or JRE. Mark this file as lib in your IDE.

#### Getting started
```java
import com.appartika.easyweb.App;
import java.io.IOException;

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

# EasyWeb (modern wrapper for com.sun.net.httpserver)

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/ummo93/EasyWeb)

EasyWeb is a easiest web framework with a minimal number of dependencies inspired ExpressJS. Founded on com.sun.net.httpserver package.
#### Requirements and dependencies
  - Java 8
  - Maven
  - Freemarker (http://freemarker.org/)
  
#### Add depependency from maven
On the title page of the library the should be an instruction containing the repository address like:
```xml
  <repositories>
    <repository>
      <id>EasyWeb</id>
      <url>https://github.com/ummo93/EasyWeb/</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
      </snapshots>
    </repository>
  </repositories>
```

And a dependency name:
```xml
  <dependencies>
      <dependency>
        <groupId>com.appartika</groupId>
        <artifactId>easyweb</artifactId>
        <version>1.0</version>
      </dependency>
  </dependencies>
```

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

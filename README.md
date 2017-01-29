# EasyWeb (modern wrapper for com.sun.net.httpserver)
[![Repo](https://jitpack.io/v/ummo93/EasyWeb.svg)](https://jitpack.io/#ummo93/EasyWeb)
[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/ummo93/EasyWeb)

EasyWeb is easiest web framework with a minimal number of dependencies inspired by ExpressJS. Founded on com.sun.net.httpserver package.
#### Requirements and dependencies
  - Java 8
  - Maven
  - Freemarker (http://freemarker.org/)
  
#### Installation as maven dependency

In pom.xml file there should be an instruction containing the repository address like:
 ```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
 ```
 
 And a dependency name in dependencies section:
 ```xml
   <dependency>
       <groupId>com.github.ummo93</groupId>
       <artifactId>EasyWeb</artifactId>
       <version>dfe6fc7e8e</version>
   </dependency>
 ```

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

License
----

MIT

# EasyWeb (modern wrapper for com.sun.net.httpserver)
[![Repo](https://jitpack.io/v/ummo93/EasyWeb.svg)](https://jitpack.io/#ummo93/EasyWeb)

EasyWeb is easiest web framework with a minimal number of dependencies inspired by ExpressJS. Founded on com.sun.net.httpserver package.
#### Requirements and dependencies
  - Java 8
  - Maven
  - Freemarker (http://freemarker.org/)
  - GSON for json parsing (https://github.com/google/gson)
  
#### Installation as maven dependency

In pom.xml file there should be an instruction containing the repository address:
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
	    <version>1.0.2</version>
	</dependency>
 ```

#### Getting started
```java
import static com.appartika.easyweb.App.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        
        get("/", (req, res) -> res.send("ok", 200));
        
        listen(5000);
    }
}
```

For details, use the wiki:
* [Create an application](https://github.com/ummo93/EasyWeb/wiki/Getting-started)
* [Routers](https://github.com/ummo93/EasyWeb/wiki/Routers)
* [Response and Request objects](https://github.com/ummo93/EasyWeb/wiki/Request-and-Response-objects)
* [Cookies](https://github.com/ummo93/EasyWeb/wiki/Work-with-cookies)

License
----

MIT

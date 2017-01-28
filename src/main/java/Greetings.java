public class Greetings {

    private static long id = 0;
    private String message;

    public Greetings(String message) {
        id += 1;
        this.message = message;
    }

    public String toJson() {
        return "{\"id\": " + id + ", \"message\": \"" + this.message + "\"}";
    }
}

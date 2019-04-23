package sample.Message;

import java.io.Serializable;

public class Packet implements Serializable {
    private String message;
    private String from;
    private String to;

    public Packet(String message, String from, String to) {
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {return to;}
}
package org.proiect.clientManipulation;

import java.io.Serializable;

public class SerializableMessage implements Serializable {
    public String message;
    public SerializableMessage(String inputMessage) {
        message = inputMessage;
    }
    @Override
    public String toString() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof String) {
            return message.equals((String) obj);
        }
        if (obj instanceof SerializableMessage) {
            return message.equals(((SerializableMessage) obj).message);
        }
        return false;
    }
    public boolean equals(String inputMessage) {
        return message.equals(inputMessage);
    }
    @Override
    public int hashCode() {
        return message.hashCode();
    }
}


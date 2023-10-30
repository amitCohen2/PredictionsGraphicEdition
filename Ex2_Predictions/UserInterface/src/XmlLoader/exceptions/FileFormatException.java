package XmlLoader.exceptions;


public class FileFormatException extends Exception {
    public FileFormatException() {
        super("This file does not supported in this format!");
    }

    public FileFormatException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "FileFormatException: " + getMessage();
    }
}


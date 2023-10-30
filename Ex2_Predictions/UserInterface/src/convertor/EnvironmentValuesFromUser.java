package convertor;

public class EnvironmentValuesFromUser {
    private boolean isValueFromUser;
    private String name;
    private String type;
    private Object value;


    public boolean isValueFromUser() {
        return isValueFromUser;
    }

    public void setValueFromUser(boolean valueFromUser) {
        isValueFromUser = valueFromUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

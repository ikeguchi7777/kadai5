package GUI;

/**
 * GraphicShape
 */
public class GraphicShape {

    String name;
    String color;
    String shape;
    String on;

    public GraphicShape(String name, String color, String shape, String on) {
        this.name = name;
        this.color = color;
        this.shape = shape;
        this.on = on;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
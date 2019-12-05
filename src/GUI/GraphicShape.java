package GUI;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.Color;

/**
 * GraphicShape
 */
public class GraphicShape {

    String name;
    String color;
    String shape;
    String on;
    public static HashMap<String,Color> colorMap = new HashMap<>();
    public static HashMap<String,String> nameMap = new HashMap<>();

    static{
        colorMap.put("black",Color.BLACK);
        colorMap.put("blue",Color.BLUE);
        colorMap.put("cyan",Color.CYAN);
        colorMap.put("darkgray",Color.DARK_GRAY);
        colorMap.put("gray",Color.GRAY);
        colorMap.put("green",Color.GREEN);
        colorMap.put("lightgray",Color.LIGHT_GRAY);
        colorMap.put("magenta",Color.MAGENTA);
        colorMap.put("orange",Color.ORANGE);
        colorMap.put("pink",Color.PINK);
        colorMap.put("red",Color.RED);
        colorMap.put("white",Color.WHITE);
        colorMap.put("yellow",Color.YELLOW);

        nameMap.put("No.0","A");
        nameMap.put("No.1","B");
        nameMap.put("No.2","C");
        nameMap.put("No.3","D");
    }
    public GraphicShape(String name, String color, String shape, String on) {
        this.name = name;
        this.color = color;
        this.shape = shape;
        this.on = on;
    }

    public String getOn() {
        return on;
    }

    public Color getColor(){
        return colorMap.get(color);
    }

    public String getShape() {
        return shape;
    }

    public String getName() {
        return nameMap.get(name);
    }

    public GraphicShape[] parseState(Vector<String> theCurrentState) {
        return null;
    }

    public void parseBinding(Hashtable<String, String> theBinding){
    }
}
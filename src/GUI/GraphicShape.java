package GUI;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import kadai5.Operator;
import kadai5.Unifier;

import java.awt.Color;

/**
 * GraphicShape
 */
public class GraphicShape {

    private String name;
    private String color;
    private String shape;
    private String on;
    private int row;
    private int col;
    public static HashMap<String,Color> colorMap = new HashMap<>();
    public static HashMap<String,String> nameMap = new HashMap<>();
    public static HashMap<String,GraphicShape> shapeMap = new HashMap<>();

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
        setPoint(-1, -1);
    }

    /**
     * initialize by default value
     */
    public GraphicShape(String name){
        this.name = name;
        color = "black";
        shape = "rect";
        on = "table";
        setPoint(-1, -1);
    }

    public void setPoint(int row,int col){
        this.row = row;
        this.col = col;
    }

    public int getCol(){
        return col;
    }

    public int getRow(){
        return row;
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

    /**
     * parse initial states
     * <p>1. ?name is name
     * -> new GraphicShape(?name)</p>
     * <p>2. ?x has a characteristic of ?color
     * -> setColor(?color)</p>
     * <p>3. ?x has shape of ?shape
     * -> setShape(?shape)</p>
     * @param initInitialState
     * @return vector contain GraphicShapes from initial states
     */
    public static Vector<GraphicShape> parseState(Vector<String> initInitialState){
        Vector<GraphicShape> shapes = new Vector<>();
        Hashtable<String,String> var = new Hashtable<>();
        for (String string : initInitialState) {
            if ((new Unifier()).unify("?name is name", string, var)) {
                GraphicShape gs = new GraphicShape(var.get("?name"));
                shapes.add(gs);
                shapeMap.put(var.get("?name"),gs);
                var.clear();
                continue;
            }
            if((new Unifier()).unify("?name has a characteristec og ?color",string,var)){
                shapeMap.get(var.get("?name")).setColor(var.get("?color"));
                var.clear();
                continue;
            }
            if((new Unifier()).unify("?name has shape of ?shape",string,var)){
                shapeMap.get(var.get("?name")).setShape(var.get("?shape"));
                var.clear();
                continue;
            }
        }
        return shapes;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
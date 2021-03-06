package GUI;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import kadai5.Unifier;

import java.awt.Color;

/**
 * GraphicShape
 */
public class GraphicShape {

    private String name;
    private String color;
    private String shape;
    private int row;
    private int col;
    public static HashMap<String, Color> colorMap = new HashMap<>();

    static {
        colorMap.put("black", Color.BLACK);
        colorMap.put("blue", Color.BLUE);
        colorMap.put("cyan", Color.CYAN);
        colorMap.put("darkgray", Color.DARK_GRAY);
        colorMap.put("gray", Color.GRAY);
        colorMap.put("green", Color.GREEN);
        colorMap.put("lightgray", Color.LIGHT_GRAY);
        colorMap.put("magenta", Color.MAGENTA);
        colorMap.put("orange", Color.ORANGE);
        colorMap.put("pink", Color.PINK);
        colorMap.put("red", Color.RED);
        colorMap.put("white", Color.WHITE);
        colorMap.put("yellow", Color.YELLOW);
    }

    /**
     * initialize by input value
     * 
     * @param name
     * @param color
     * @param shape
     */
    public GraphicShape(String name, String color, String shape) {
        this.name = name;
        this.color = color;
        this.shape = shape;
        setPoint(-1, -1);
    }

    /**
     * initialize by default value
     */
    public GraphicShape(String name) {
        this.name = name;
        color = "black";
        shape = "rect";
        setPoint(-1, -1);
    }

    // top of setters and getters
    public void setPoint(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public Color getColor() {
        return colorMap.get(color);
    }

    public String getShape() {
        return shape;
    }

    public String getName() {
        return name;
    }

    public boolean setColor(String color) {
        if (colorMap.get(color) == null)
            return false;
        this.color = color;
        return true;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
    // bottom of setters and getters

    /**
     * parse initial states
     * <p>
     * 1. ?name is name -> new GraphicShape(?name)
     * </p>
     * <p>
     * 2. ?x has a characteristic of ?color -> setColor(?color)
     * </p>
     * <p>
     * 3. ?x has shape of ?shape -> setShape(?shape)
     * </p>
     * 
     * @param initInitialState
     * @param shapeMap
     * @return vector contain GraphicShapes from initial states
     */
    public static Vector<GraphicShape> parseState(Vector<String> initInitialState,
            HashMap<String, GraphicShape> shapeMap) {
        Vector<GraphicShape> shapes = new Vector<>();
        for (String string : initInitialState) {
            Hashtable<String, String> var = new Hashtable<>();
            if ((new Unifier()).unify("?name is name", string, var)) {
                GraphicShape gs = new GraphicShape(var.get("?name"));
                shapes.add(gs);
                shapeMap.put(var.get("?name"), gs);
            } else if ((new Unifier()).unify("?name has a characteristic of ?chara", string, var)) {
                GraphicShape gs = shapeMap.get(var.get("?name"));
                if (gs.setColor(var.get("?chara")))
                    continue;
                gs.setShape(var.get("?chara"));
            }
        }
        return shapes;
    }
}
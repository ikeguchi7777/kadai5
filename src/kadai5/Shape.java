//package kadai5;

import java.util.Vector;

public class Shape {
	public static Vector<String> make(String name) {
		Vector<String> result = new Vector<>();
		result.add(name + " is name");
		result.add("clear " + name);
		result.add("ontable " + name);
		return result;
	}

	public static Vector<String> make(String name, String... characteristics) {
		Vector<String> result = make(name);
		for (String chara : characteristics) {
			switch (chara) {
			case "triangle":
				result.add("#Place ?* on " + name);
			case "rect":
				result.add(name + " has shape of " + chara);
				break;
			default:
				result.add(name + " has a characteristic of " + chara);
				break;
			}
		}
		return result;
	}
}

/*class Triangle extends Shape {
	public static Vector<String> make(String name) {
		Vector<String> result = Shape.make(name);
		result.add(name + " has shape of triangle");
		result.add("#Place ?* on " + name);
		return result;
	}

	public static Vector<String> make(String name, String... characteristics) {
		Vector<String> result = Shape.make(name, characteristics);
		result.add(name + " has shape of triangle");
		result.add("#Place ?* on " + name);
		return result;
	}
}*/

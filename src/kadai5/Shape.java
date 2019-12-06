package kadai5;

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
			result.add(name + " has a characteristic of " + chara);
		}
		return result;
	}
}
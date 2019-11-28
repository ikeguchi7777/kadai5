package kadai5;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class Operator {
	String name;
	Vector<String> ifList;
	Vector<String> addList;
	Vector<String> deleteList;

	Operator(String theName,
			Vector<String> theIfList, Vector<String> theAddList, Vector<String> theDeleteList) {
		name = theName;
		ifList = theIfList;
		addList = theAddList;
		deleteList = theDeleteList;
	}

	public Vector<String> getAddList() {
		return addList;
	}

	public Vector<String> getDeleteList() {
		return deleteList;
	}

	public Vector<String> getIfList() {
		return ifList;
	}

	public String toString() {
		String result = "NAME: " + name + "\n" +
				"IF :" + ifList + "\n" +
				"ADD:" + addList + "\n" +
				"DELETE:" + deleteList;
		return result;
	}

	public Vector<String> applyState(Vector<String> theState) {
		for (int i = 0; i < addList.size(); i++) {
			theState.addElement(addList.elementAt(i));
		}
		for (int i = 0; i < deleteList.size(); i++) {
			theState.removeElement(deleteList.elementAt(i));
		}
		return theState;
	}

	public Operator getRenamedOperator(int uniqueNum) {
		Vector<String> vars = new Vector<String>();
		// IfListの変数を集める
		for (int i = 0; i < ifList.size(); i++) {
			String anIf = ifList.elementAt(i);
			vars = getVars(anIf, vars);
		}
		// addListの変数を集める
		for (int i = 0; i < addList.size(); i++) {
			String anAdd = addList.elementAt(i);
			vars = getVars(anAdd, vars);
		}
		// deleteListの変数を集める
		for (int i = 0; i < deleteList.size(); i++) {
			String aDelete = deleteList.elementAt(i);
			vars = getVars(aDelete, vars);
		}
		Hashtable<String, String> renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

		// 新しいIfListを作る
		Vector<String> newIfList = new Vector<String>();
		for (int i = 0; i < ifList.size(); i++) {
			String newAnIf = renameVars(ifList.elementAt(i),
					renamedVarsTable);
			newIfList.addElement(newAnIf);
		}
		// 新しいaddListを作る
		Vector<String> newAddList = new Vector<String>();
		for (int i = 0; i < addList.size(); i++) {
			String newAnAdd = renameVars(addList.elementAt(i),
					renamedVarsTable);
			newAddList.addElement(newAnAdd);
		}
		// 新しいdeleteListを作る
		Vector<String> newDeleteList = new Vector<String>();
		for (int i = 0; i < deleteList.size(); i++) {
			String newADelete = renameVars(deleteList.elementAt(i),
					renamedVarsTable);
			newDeleteList.addElement(newADelete);
		}
		// 新しいnameを作る
		String newName = renameVars(name, renamedVarsTable);

		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private Vector<String> getVars(String thePattern, Vector<String> vars) {
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				vars.addElement(tmp);
			}
		}
		return vars;
	}

	private Hashtable<String, String> makeRenamedVarsTable(Vector<String> vars, int uniqueNum) {
		Hashtable<String, String> result = new Hashtable<String, String>();
		for (int i = 0; i < vars.size(); i++) {
			String newVar = vars.elementAt(i) + uniqueNum;
			result.put(vars.elementAt(i), newVar);
		}
		return result;
	}

	private String renameVars(String thePattern,
			Hashtable<String, String> renamedVarsTable) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				result = result + " " +
						renamedVarsTable.get(tmp);
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	public Operator instantiate(Hashtable<String, String> theBinding) {
		// name を具体化
		String newName = instantiateString(name, theBinding);
		// ifList    を具体化
		Vector<String> newIfList = new Vector<String>();
		for (int i = 0; i < ifList.size(); i++) {
			String newIf = instantiateString(ifList.elementAt(i), theBinding);
			newIfList.addElement(newIf);
		}
		// addList   を具体化
		Vector<String> newAddList = new Vector<String>();
		for (int i = 0; i < addList.size(); i++) {
			String newAdd = instantiateString(addList.elementAt(i), theBinding);
			newAddList.addElement(newAdd);
		}
		// deleteListを具体化
		Vector<String> newDeleteList = new Vector<String>();
		for (int i = 0; i < deleteList.size(); i++) {
			String newDelete = instantiateString(deleteList.elementAt(i), theBinding);
			newDeleteList.addElement(newDelete);
		}
		return new Operator(newName, newIfList, newAddList, newDeleteList);
	}

	private String instantiateString(String thePattern, Hashtable<String,String> theBinding) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				String newString = theBinding.get(tmp);
				if (newString == null) {
					result = result + " " + tmp;
				} else {
					result = result + " " + newString;
				}
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	private boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}
}

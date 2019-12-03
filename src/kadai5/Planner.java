package kadai5;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 *
 * startを呼ぶと動作が開始しする。
 *
 */
public class Planner {
	Vector<Operator> operators;
	Random rand;
	Vector<Operator> plan;

	public static void main(String argv[]) {
		(new Planner()).start();
	}

	Planner() {
		rand = new Random();
	}

	/**
	 *
	 */
	public void start() {
		initOperators();
		Vector<String> goalList = initGoalList();
		Vector<String> initialState = initInitialState();

		Hashtable<String, String> theBinding = new Hashtable<String, String>();
		plan = new Vector<Operator>();
		planning(goalList, initialState, theBinding);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	}

	private boolean planning(Vector<String> theGoalList,
			Vector<String> theCurrentState,
			Hashtable<String, String> theBinding) {
		System.out.println("*** GOALS ***" + theGoalList);
		if (theGoalList.size() == 1) {
			String aGoal = theGoalList.elementAt(0);
			if (planningAGoal(aGoal, theCurrentState, theBinding, 0) != -1) {
				return true;
			} else {
				return false;
			}
		} else {
			String aGoal = theGoalList.elementAt(0);
			int cPoint = 0;
			while (cPoint < operators.size()) {
				//System.out.println("cPoint:"+cPoint);
				// Store original binding
				Hashtable<String, String> orgBinding = new Hashtable<String, String>();
				for (Enumeration<String> e = theBinding.keys(); e.hasMoreElements();) {
					String key = e.nextElement();
					String value = theBinding.get(key);
					orgBinding.put(key, value);
				}
				Vector<String> orgState = new Vector<String>();
				for (int i = 0; i < theCurrentState.size(); i++) {
					orgState.addElement(theCurrentState.elementAt(i));
				}
				
				//System.out.println(cPoint);
				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint);
				System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != -1) {
					theGoalList.removeElementAt(0);
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding)) {
						//System.out.println("Success !");
						return true;
					} else {
						cPoint = tmpPoint+1;
						//System.out.println("Fail::"+cPoint);
						theGoalList.insertElementAt(aGoal, 0);

						theBinding.clear();
						for (Enumeration<String> e = orgBinding.keys(); e.hasMoreElements();) {
							String key = e.nextElement();
							String value = orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.removeAllElements();
						for (int i = 0; i < orgState.size(); i++) {
							theCurrentState.addElement(orgState.elementAt(i));
						}
					}
				} else {
					cPoint++;
					theBinding.clear();
					for (Enumeration<String> e = orgBinding.keys(); e.hasMoreElements();) {
						String key = e.nextElement();
						String value = orgBinding.get(key);
						theBinding.put(key, value);
					}
					theCurrentState.removeAllElements();
					for (int i = 0; i < orgState.size(); i++) {
						theCurrentState.addElement(orgState.elementAt(i));
					}
					return false;
				}
			}
			return false;
		}
	}

	private int planningAGoal(String theGoal, Vector<String> theCurrentState,
			Hashtable<String, String> theBinding, int cPoint) {
		System.out.println("**" + theGoal);
		int size = theCurrentState.size();
		for (int i = 0; i < size; i++) {
			String aState = theCurrentState.elementAt(i);
			if ((new Unifier()).unify(theGoal, aState, theBinding)) {
				return 0;
			}
		}

		int randInt = Math.abs(rand.nextInt()) % operators.size();
		//Operator op = operators.elementAt(randInt);
		//operators.removeElementAt(randInt);
		//operators.addElement(op);
		System.out.println(cPoint);
		for (int i = cPoint; i < operators.size(); i++) {
			Operator anOperator = rename(operators.elementAt(i));
			// 現在のCurrent state, Binding, planをbackup
			Hashtable<String, String> orgBinding = new Hashtable<String, String>();
			for (Enumeration<String> e = theBinding.keys(); e.hasMoreElements();) {
				String key = e.nextElement();
				String value = theBinding.get(key);
				orgBinding.put(key, value);
			}
			Vector<String> orgState = new Vector<String>();
			for (int j = 0; j < theCurrentState.size(); j++) {
				orgState.addElement(theCurrentState.elementAt(j));
			}
			Vector<Operator> orgPlan = new Vector<Operator>();
			for (int j = 0; j < plan.size(); j++) {
				orgPlan.addElement(plan.elementAt(j));
			}

			Vector<String> addList = anOperator.getAddList();
			for (int j = 0; j < addList.size(); j++) {
				if ((new Unifier()).unify(theGoal,
						addList.elementAt(j),
						theBinding)) {
					Operator newOperator = anOperator.instantiate(theBinding);
					Vector<String> newGoals = newOperator.getIfList();
					System.out.println(newOperator.name);
					if (planning(newGoals, theCurrentState, theBinding)) {
						System.out.println(newOperator.name);
						plan.addElement(newOperator);
						theCurrentState = newOperator.applyState(theCurrentState);
						return i + 1;
					} else {
						// 失敗したら元に戻す．
						theBinding.clear();
						for (Enumeration<String> e = orgBinding.keys(); e.hasMoreElements();) {
							String key = e.nextElement();
							String value = orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.removeAllElements();
						for (int k = 0; k < orgState.size(); k++) {
							theCurrentState.addElement(orgState.elementAt(k));
						}
						plan.removeAllElements();
						for (int k = 0; k < orgPlan.size(); k++) {
							plan.addElement(orgPlan.elementAt(k));
						}
					}
				}
			}
		}
		System.out.println("失敗");
		return -1;
	}

	int uniqueNum = 0;

	private Operator rename(Operator theOperator) {
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	private Vector<String> initGoalList() {
		Vector<String> goalList = new Vector<String>();
		//goalList.addElement("A on B");
		//goalList.addElement("B on C");
		goalList.addElement("red on blue");
		goalList.addElement("blue on green");
		return goalList;
	}

	private Vector<String> initInitialState() {
		Vector<String> initialState = new Vector<String>();
		/*initialState.addElement("clear A");
		initialState.addElement("clear B");
		initialState.addElement("clear C");

		initialState.addElement("ontable A");
		initialState.addElement("ontable B");
		initialState.addElement("ontable C");*/
		initialState.addAll(Shape.make("A", "red"));
		initialState.addAll(Shape.make("B", "blue"));
		initialState.addAll(Shape.make("C", "green"));
		initialState.addElement("handEmpty");
		return initialState;
	}

	private void initOperators() {
		operators = new Vector<Operator>();

		// OPERATOR 5
		/// NAME:?yの名前は?x
		String name5 = "?x is name of ?y";
		/// IF
		Vector<String> ifList5 = new Vector<>();
		ifList5.addElement("?x has a characteristic of ?y");
		ifList5.addElement("?x is name");
		//ifList5.addElement("?x has a characteristic of ?y");
		ifList5.addElement("?x on ?z");
		/// ADD-LIST
		Vector<String> addList5 = new Vector<>();
		addList5.addElement("?y on ?z");
		/// DELETE-LIST
		Vector<String> deleteList5 = new Vector<>();
		deleteList5.addElement("?x on ?z");
		operators.addElement(new Operator(name5, ifList5, addList5, deleteList5));

		// OPERATOR 5
		/// NAME:?yの名前は?x
		String name6 = "?x is name of ?y";
		/// IF
		Vector<String> ifList6 = new Vector<>();
		ifList6.addElement("?x has a characteristic of ?y");
		ifList6.addElement("?x is name");
		//ifList6.addElement("?x has a characteristic of ?y");
		ifList6.addElement("?z on ?x");
		/// ADD-LIST
		Vector<String> addList6 = new Vector<>();
		addList6.addElement("?z on ?y");
		/// DELETE-LIST
		Vector<String> deleteList6 = new Vector<>();
		deleteList6.addElement("?z on ?x");
		operators.addElement(new Operator(name6, ifList6, addList6, deleteList6));

		// OPERATOR 1
		/// NAME:?yの上に?xを乗せる
		String name1 = "Place ?x on ?y";
		/// IF
		Vector<String> ifList1 = new Vector<String>();
		ifList1.addElement("?x is name");
		ifList1.addElement("?y is name");
		ifList1.addElement("clear ?y");
		ifList1.addElement("holding ?x");
		/// ADD-LIST
		Vector<String> addList1 = new Vector<String>();
		addList1.addElement("?x on ?y");
		addList1.addElement("clear ?x");
		addList1.addElement("handEmpty");
		/// DELETE-LIST
		Vector<String> deleteList1 = new Vector<String>();
		deleteList1.addElement("clear ?y");
		deleteList1.addElement("holding ?x");
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.addElement(operator1);

		// OPERATOR 2
		/// NAME:?yの上から?xを除く
		String name2 = "remove ?x from on top ?y";
		/// IF
		Vector<String> ifList2 = new Vector<String>();
		ifList2.addElement("?x is name");
		ifList2.addElement("?y is name");
		ifList2.addElement("?x on ?y");
		ifList2.addElement("clear ?x");
		ifList2.addElement("handEmpty");
		/// ADD-LIST
		Vector<String> addList2 = new Vector<String>();
		addList2.addElement("clear ?y");
		addList2.addElement("holding ?x");
		/// DELETE-LIST
		Vector<String> deleteList2 = new Vector<String>();
		deleteList2.addElement("?x on ?y");
		deleteList2.addElement("clear ?x");
		deleteList2.addElement("handEmpty");
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.addElement(operator2);

		// OPERATOR 3
		/// NAME:テーブルから?xを持ち上げる
		String name3 = "pick up ?x from the table";
		/// IF
		Vector<String> ifList3 = new Vector<String>();
		ifList3.addElement("?x is name");
		ifList3.addElement("?y is name");
		ifList3.addElement("ontable ?x");
		ifList3.addElement("clear ?x");
		ifList3.addElement("handEmpty");
		/// ADD-LIST
		Vector<String> addList3 = new Vector<String>();
		addList3.addElement("holding ?x");
		/// DELETE-LIST
		Vector<String> deleteList3 = new Vector<String>();
		deleteList3.addElement("ontable ?x");
		deleteList3.addElement("clear ?x");
		deleteList3.addElement("handEmpty");
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.addElement(operator3);

		// OPERATOR 4
		/// NAME:?xをテーブルの上に戻す
		String name4 = "put ?x down on the table";
		/// IF
		Vector<String> ifList4 = new Vector<String>();
		ifList4.addElement("?x is name");
		ifList4.addElement("holding ?x");
		/// ADD-LIST
		Vector<String> addList4 = new Vector<String>();
		addList4.addElement("ontable ?x");
		addList4.addElement("clear ?x");
		addList4.addElement("handEmpty");
		/// DELETE-LIST
		Vector<String> deleteList4 = new Vector<String>();
		deleteList4.addElement("holding ?x");
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.addElement(operator4);

	}
}

package kadai5;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

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
		
		System.out.println("state: " + initialState);
		System.out.println("plan: " + plan);
		System.out.println("binding: " + theBinding);
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

				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint);
				//System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != -1) {
					theGoalList.removeElementAt(0);
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding)) {
						//System.out.println("Success !");
						return true;
					} else {
						cPoint = tmpPoint;
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
		Operator op = operators.elementAt(randInt);
		operators.removeElementAt(randInt);
		operators.addElement(op);

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
		goalList.addElement("A on B");
		goalList.addElement("B on C");
		return goalList;
	}

	private Vector<String> initInitialState() {
		Vector<String> initialState = new Vector<String>();
		initialState.addElement("clear A");
		initialState.addElement("clear B");
		initialState.addElement("clear C");

		initialState.addElement("ontable A");
		initialState.addElement("ontable B");
		initialState.addElement("ontable C");
		initialState.addElement("handEmpty");
		return initialState;
	}

	private void initOperators() {
		operators = new Vector<Operator>();

		// OPERATOR 1
		/// NAME
		String name1 = new String("Place ?x on ?y");
		/// IF
		Vector<String> ifList1 = new Vector<String>();
		ifList1.addElement(new String("clear ?y"));
		ifList1.addElement(new String("holding ?x"));
		/// ADD-LIST
		Vector<String> addList1 = new Vector<String>();
		addList1.addElement(new String("?x on ?y"));
		addList1.addElement(new String("clear ?x"));
		addList1.addElement(new String("handEmpty"));
		/// DELETE-LIST
		Vector<String> deleteList1 = new Vector<String>();
		deleteList1.addElement(new String("clear ?y"));
		deleteList1.addElement(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.addElement(operator1);

		// OPERATOR 2
		/// NAME
		String name2 = new String("remove ?x from on top ?y");
		/// IF
		Vector<String> ifList2 = new Vector<String>();
		ifList2.addElement(new String("?x on ?y"));
		ifList2.addElement(new String("clear ?x"));
		ifList2.addElement(new String("handEmpty"));
		/// ADD-LIST
		Vector<String> addList2 = new Vector<String>();
		addList2.addElement(new String("clear ?y"));
		addList2.addElement(new String("holding ?x"));
		/// DELETE-LIST
		Vector<String> deleteList2 = new Vector<String>();
		deleteList2.addElement(new String("?x on ?y"));
		deleteList2.addElement(new String("clear ?x"));
		deleteList2.addElement(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.addElement(operator2);

		// OPERATOR 3
		/// NAME
		String name3 = new String("pick up ?x from the table");
		/// IF
		Vector<String> ifList3 = new Vector<String>();
		ifList3.addElement(new String("ontable ?x"));
		ifList3.addElement(new String("clear ?x"));
		ifList3.addElement(new String("handEmpty"));
		/// ADD-LIST
		Vector<String> addList3 = new Vector<String>();
		addList3.addElement(new String("holding ?x"));
		/// DELETE-LIST
		Vector<String> deleteList3 = new Vector<String>();
		deleteList3.addElement(new String("ontable ?x"));
		deleteList3.addElement(new String("clear ?x"));
		deleteList3.addElement(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.addElement(operator3);

		// OPERATOR 4
		/// NAME
		String name4 = new String("put ?x down on the table");
		/// IF
		Vector<String> ifList4 = new Vector<String>();
		ifList4.addElement(new String("holding ?x"));
		/// ADD-LIST
		Vector<String> addList4 = new Vector<String>();
		addList4.addElement(new String("ontable ?x"));
		addList4.addElement(new String("clear ?x"));
		addList4.addElement(new String("handEmpty"));
		/// DELETE-LIST
		Vector<String> deleteList4 = new Vector<String>();
		deleteList4.addElement(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.addElement(operator4);
	}
}




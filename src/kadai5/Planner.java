package kadai5;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

public class Planner {
	Vector<Operator> operators;
	Random rand;
	Vector<Operator> plan;
	Vector<String> blockNext = new Vector<>();
	Vector<String> GoalList;
	int[] priority = {2,3,1,4,5,5,6,6,6,6,6,6};


	public static void main(String argv[]) {
		long st = System.currentTimeMillis();
		(new Planner()).start();
		long et = System.currentTimeMillis();
		long totalTime = et - st;
		System.out.println("time:" + totalTime);
	}

	public Planner() {
		rand = new Random();
	}

	Vector<String> checkRoute(Vector<String> goalList, Vector<String> initState) {
		Vector<String> newGoal = new Vector<>();
		Vector<String> list = new Vector<>();
		for (String goal : goalList) {
			StringTokenizer tokenizer = new StringTokenizer(goal);
			String x = tokenizer.nextToken();
			tokenizer.nextToken();
			String y = tokenizer.nextToken();
			for (String state : initState) {
				if ((new Unifier()).unify("?x has a characteristic of " + x, state)) {
					StringTokenizer t = new StringTokenizer(state);
					x = t.nextToken();
				}

				if ((new Unifier()).unify("?x has a characteristic of " + y, state)) {
					StringTokenizer t = new StringTokenizer(state);
					y = t.nextToken();
				}
			}
			boolean isadd = false;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).equals(x)) {
					list.add(i, y);
					newGoal.add(i, goal);
					isadd = true;
					break;
				}
			}
			if (!isadd) {
				list.add(y);
				newGoal.add(goal);
			}
		}
		return newGoal;
	}

	private Vector<String> testPlan(Vector<Operator> thePlan, Hashtable<String, String> theBinding) {
		Vector<String> theState = initInitialState();
		Vector<Operator> newPlan = new Vector<>();
		for (Operator operator : thePlan) {
			Operator instOp = operator.instantiate(theBinding);
			if (theState.containsAll(instOp.ifList)) {
				newPlan.add(operator);
				instOp.applyState(theState);
			} else {
				break;
			}
		}
		thePlan = newPlan;
		return theState;
	}

	public void start() {
		blockNext.removeAllElements();
		initOperators();
		Vector<String> goalList = initGoalList();
		Vector<String> initialState = initInitialState();

		sortGoalList(goalList, initialState);

		Vector<String> blockList = initBlockList(initialState);
		GoalList = new Vector<>(goalList);
		Hashtable<String, String> theBinding = new Hashtable<String, String>();
		plan = new Vector<Operator>();
		goalList = checkRoute(goalList, initialState);
		//do {
		do {
			if (!planning(goalList, initialState, theBinding, blockList)) {
				System.out.println("探索失敗");
				return;
			}
			goalList.removeAllElements();
			for (String goal : GoalList) {
				if (!initialState.contains(goal))
					goalList.addElement(goal);
			}
		} while (!goalList.isEmpty());

		//	goalList = initGoalList();
		//	initialState = testPlan(plan, theBinding);
		//} while (!initialState.containsAll(goalList));

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}

		//System.out.println("state: " + initialState);
		//System.out.println("plan: " + plan);
		//System.out.println("binding: " + theBinding);
	}

	/**
	* GUIで探索を行うためのメソッド
	* ゴールリストはすべて?x on ?yの形で
	* initialStateはShape.makeで作ったものを入れれば良い
	* 探索が失敗するとnullを返す
	* @param goalList
	* @param initialState
	* @return plan
	*/
	public Vector<Operator> GUIStart(Vector<String> goalList, Vector<String> initialState) {
		blockNext.removeAllElements();
		initOperators();
		Vector<String> blockList = initBlockList(initialState);
		GoalList = new Vector<>(goalList);
		Hashtable<String, String> theBinding = new Hashtable<String, String>();
		plan = new Vector<Operator>();
		goalList = checkRoute(goalList, initialState);
		int count = 0;
		//do {
		do {
			if (!planning(goalList, initialState, theBinding, blockList)) {
				System.out.println("探索失敗");
				return null;
			}
			goalList.removeAllElements();
			for (String goal : GoalList) {
				if (!initialState.contains(goal))
					goalList.addElement(goal);
			}
			if(count > 5)
				return null;
			count++;
		} while (!goalList.isEmpty());

		//	goalList = initGoalList();
		//	initialState = testPlan(plan, theBinding);
		//} while (!initialState.containsAll(goalList));

		/*
		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
		
		System.out.println("state: " + initialState);
		System.out.println("plan: " + plan);
		System.out.println("binding: " + theBinding);
		*/

		return plan;
	}

	private Vector<String> initBlockList(Vector<String> initState) {
		Vector<String> result = new Vector<>();
		for (String state : initState) {
			if (state.startsWith("#"))
				result.add(state.substring(1));
		}
		initState.removeIf(s -> s.startsWith("#"));
		return result;
	}

	private boolean planning(Vector<String> theGoalList,
			Vector<String> theCurrentState,
			Hashtable<String, String> theBinding,
			Vector<String> theBlockList) {
		System.out.println("*** GOALS ***" + theGoalList);
		//System.out.println(theCurrentState);
		if (theGoalList.size() == 1) {
			String aGoal = theGoalList.elementAt(0);
			if (planningAGoal(aGoal, theCurrentState, theBinding, 0, theBlockList) != Integer.MIN_VALUE) {
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

				int tmpPoint = planningAGoal(aGoal, theCurrentState, theBinding, cPoint, theBlockList);
				//System.out.println("tmpPoint: "+tmpPoint);
				if (tmpPoint != Integer.MIN_VALUE) {
					theGoalList.removeElementAt(0);
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding, theBlockList)) {
						//System.out.println("Success !");
						return true;
					} else {
						cPoint = tmpPoint;
						//cPoint=tmpPoint;
						//System.out.println("Fail::"+cPoint);
						theGoalList.add(0, aGoal);

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
						if (tmpPoint == 0)
							return false;
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
			Hashtable<String, String> theBinding, int cPoint, Vector<String> theBlockList) {
		System.out.println("**" + theGoal);
		if (cPoint <= 0) {
			int size = theCurrentState.size();
			for (int i = -cPoint; i < size; i++) {
				String aState = theCurrentState.elementAt(i);
				if ((new Unifier()).unify(theGoal, aState, theBinding)) {
					return -i - 1;
				}
			}
		}
		if (cPoint < 0)
			return Integer.MIN_VALUE;
		//変えなきゃいけないところ！

		//初期状態(ランダムで決定)
		//int randInt = Math.abs(rand.nextInt()) % operators.size();
		//Operator op = operators.elementAt(randInt);
		//operators.removeElementAt(randInt);
		//operators.addElement(op);
		Operator op = operators.elementAt(0);
		operators.removeElementAt(0);
		operators.addElement(op);
		int judgeOperator = judgeName(op);
		resetPriority(judgeOperator);
		sortOp();
		Vector<String> theBlockNext = blockNext;

		loop: for (int i = cPoint; i < operators.size(); i++) {
			for (String dontnext : blockNext) {
				if (operators.elementAt(i).name.equals(dontnext))
					continue loop;
			}
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
					for (String block : theBlockList) {
						/*if (operators.elementAt(i).name.equals(block))
							continue loop;*/
						if ((new Unifier()).unify(newOperator.name, block, theBinding))
							continue loop;
					}
					blockNext = anOperator.getBlockList();
					Vector<String> newGoals = newOperator.getIfList();
					Vector<String> addblocks = getBlockList(newGoals);
					theBlockList.addAll(addblocks);
					//theBlockList.addAll(newOperator.getBlockList());
					System.out.println(newOperator.name);
					//operators.get(i).setPriority(anOperator.getPriority() + 1);
					if (planning(newGoals, theCurrentState, theBinding, theBlockList)) {
						newOperator = newOperator.instantiate(theBinding);
						System.out.println(newOperator.name);
						plan.addElement(newOperator);
						theCurrentState = newOperator.applyState(theCurrentState);
						//operators.get(i).setPriority(anOperator.getPriority() + 1);
						for (int k = 0; k < addblocks.size(); k++) {
							theBlockList.removeElementAt(theBlockList.size() - 1);
						}

						//theBlockList.removeAll(addblocks);
						blockNext.removeAllElements();
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
						for (int k = 0; k < addblocks.size(); k++) {
							theBlockList.removeElementAt(theBlockList.size() - 1);
						}
						//theBlockList.removeAll(addblocks);
						blockNext = theBlockNext;
					}
				}
			}
			System.out.println("次のOPへ:" + theGoal);
		}
		System.out.println("失敗:" + theGoal);
		return Integer.MIN_VALUE;
	}

	Vector<String> getBlockList(Vector<String> ifList) {
		Vector<String> result = new Vector<>();
		for (String string : ifList) {
			if (string.startsWith("#"))
				result.add(string.substring(1));
		}
		ifList.removeIf(s -> s.startsWith("#"));
		return result;
	}

	int uniqueNum = 0;

	private Operator rename(Operator theOperator) {
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	private Vector<String> initGoalList() {
		Vector<String> goalList = new Vector<String>();
		//goalList.addElement("B on C");
		//goalList.addElement("A on B");
		goalList.addElement("triangle on rect");
		goalList.addElement("blue on green");

		return goalList;
	}

	/**
	 * goalListを効率のいいようにソート
	 * @param goalList ソートすべきgoalList
	 * @param states 特徴と名前を関連付ける状態
	 */
	private void sortGoalList(Vector<String> goalList, final Vector<String> states) {
		Hashtable<String, Integer> sort = new Hashtable<>();
		Hashtable<String, Integer> directPriority = new Hashtable<>();
		Vector<String[]> indirectPriority = new Vector<>();

		for (String string : goalList) {
			StringTokenizer parsedGoal = new StringTokenizer(string);
			String token;
			if ((token = parsedGoal.nextToken()).equals("ontable")) {
				for (String name : getNamesByCharacter(token, states)) {
					directPriority.put(name, 0);
				}
				sort.put(string, 0);
			} else {
				parsedGoal.nextToken();
				String token2 = parsedGoal.nextToken();
				for (String name : getNamesByCharacter(token, states))
					for (String name2 : getNamesByCharacter(token2, states))
						indirectPriority.add(new String[]{name, name2});
			}
		}

		for (String[] strings : indirectPriority) {
			if (directPriority.containsKey(strings[1])) {
				try {
					directPriority.put(strings[0],
							Math.min(directPriority.get(strings[0]), directPriority.get(strings[1]) + 1));
				} catch (NullPointerException e) {
					directPriority.put(strings[0], directPriority.get(strings[1]) + 1);
				}
			} else {
				try {
					directPriority.put(strings[1], directPriority.get(strings[0]) - 1);
				} catch (NullPointerException e) {
					directPriority.put(strings[1], goalList.size());
					directPriority.put(strings[0], goalList.size() + 1);
				}
			}
		}

		for (String goal : goalList) {
			if (!sort.contains(goal)) {
				sort.put(goal, goalList.size() * 2);
				if (!goal.startsWith("ontable ")) {
					for (String string : getNamesByCharacter(new StringTokenizer(goal).nextToken(), states)) {
						sort.put(goal, directPriority.get(string));
					}
				}
			}
		}
		goalList.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return sort.get(o1) - sort.get(o2);
			}
		});
	}

	/**
	 * 特徴を名前にする
	 * @param character 特徴あるいは名前
	 * @param states 特徴と名前を関連付ける状態集合
	 * @return characterが名前ならそれのみを含むVector, 特徴ならその特徴を含む名前の集合
	 */
	private Vector<String> getNamesByCharacter(final String character, final Vector<String> states) {
		Vector<String> ret = new Vector<>();
		if (states.contains(character + " is name")) {
			ret.add(character);
			return ret;
		}
		for (String state : states) {
			if (state.endsWith(" has a characteristic of " + character) || state.endsWith(" has shape of " + character)) {
				String name = new StringTokenizer(state).nextToken();
				if (!ret.contains(name))
					ret.add(name);
			}
		}

		return ret;
	}


	private Vector<String> initInitialState() {
		Vector<String> initialState = new Vector<String>();
		//initialState.addElement("clear A");
		//initialState.addElement("clear B");
		//initialState.addElement("clear C");
		initialState.addAll(Shape.make("A", "red", "triangle"));
		//initialState.addAll(Shape.make("B", "blue"));
		initialState.addAll(Shape.make("B", "blue", "rect"));
		initialState.addAll(Shape.make("C", "green", "rect"));

		//initialState.addElement("ontable A");
		//initialState.addElement("ontable B");
		//initialState.addElement("ontable C");
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
		ifList1.addElement("#Place ?x on ?y");
		ifList1.addElement("?x is name");
		ifList1.addElement("?y is name");
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
		/// BLOCK-LIST
		Vector<String> blockList1 = new Vector<>();
		blockList1.addElement("remove ?x from on top ?y");
		blockList1.addElement("Place ?x on ?y");
		// PRIORITY
		int priority1 = priority[0];
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1, priority1, blockList1);
		operators.addElement(operator1);

		// OPERATOR 2
		/// NAME
		String name2 = new String("remove ?x from on top ?y");
		/// IF
		Vector<String> ifList2 = new Vector<String>();
		ifList2.addElement("#remove ?x from on top ?y");
		ifList2.addElement("?x is name");
		ifList2.addElement("?y is name");
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
		/// BLOCK-LIST
		Vector<String> blockList2 = new Vector<>();
		blockList2.addElement("Place ?x on ?y");
		blockList2.addElement("remove ?x from on top ?y");
		// PRIORITY
		int priority2 = priority[1];
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2, priority2, blockList2);
		operators.addElement(operator2);

		// OPERATOR 3
		/// NAME
		String name3 = new String("pick up ?x from the table");
		/// IF
		Vector<String> ifList3 = new Vector<String>();
		//ifList3.addElement("?x is name");
		ifList3.addElement("#pick up ?x from the table");
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
		/// BLOCK-LIST
		Vector<String> blockList3 = new Vector<>();
		blockList3.addElement("put ?x down on the table");
		blockList3.addElement("pick up ?x from the table");
		// PRIORITY
		int priority3 = priority[2];
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3, priority3, blockList3);
		operators.addElement(operator3);

		// OPERATOR 4
		/// NAME
		String name4 = new String("put ?x down on the table");
		/// IF
		Vector<String> ifList4 = new Vector<String>();
		//ifList4.addElement("?x is name");
		ifList4.addElement("#put ?x down on the table");
		ifList4.addElement(new String("holding ?x"));
		/// ADD-LIST
		Vector<String> addList4 = new Vector<String>();
		addList4.addElement(new String("ontable ?x"));
		addList4.addElement(new String("clear ?x"));
		addList4.addElement(new String("handEmpty"));
		/// DELETE-LIST
		Vector<String> deleteList4 = new Vector<String>();
		deleteList4.addElement(new String("holding ?x"));
		/// BLOCK-LIST
		Vector<String> blockList4 = new Vector<>();
		blockList4.addElement("put ?x down on the table");
		blockList4.addElement("pick up ?x from the table");
		// PRIORITY
		int priority4 = priority[3];
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4, priority4, blockList4);
		operators.addElement(operator4);

		// OPERATOR 5
		/// NAME:?yの名前は?x
		String name5 = "?x is name of ?y";
		/// IF
		Vector<String> ifList5 = new Vector<>();
		ifList5.addElement("#?x is name of ?y");
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
		/// BLOCK-LIST
		Vector<String> blockList5 = new Vector<>();
		blockList5.addElement("?x is name of ?y");
		blockList5.addElement("?z is a characteristic of ?x");
		/// PRIORITY
		int priority5 = priority[4];
		operators.addElement(new Operator(name5, ifList5, addList5, deleteList5, priority5, blockList5));

		// OPERATOR 6
		/// NAME:?yの名前は?x
		String name6 = "?x is name of ?z";
		/// IF
		Vector<String> ifList6 = new Vector<>();
		ifList6.addElement("#?x is name of ?z");
		ifList6.addElement("?x has a characteristic of ?z");
		ifList6.addElement("?x is name");
		//ifList6.addElement("?x has a characteristic of ?y");
		ifList6.addElement("?y on ?x");
		/// ADD-LIST
		Vector<String> addList6 = new Vector<>();
		addList6.addElement("?y on ?z");
		/// DELETE-LIST
		Vector<String> deleteList6 = new Vector<>();
		deleteList6.addElement("?y on ?x");
		/// BLOCK-LIST
		Vector<String> blockList6 = new Vector<>();
		blockList6.addElement("?x is name of ?z");
		blockList6.addElement("?y is a characteristic of ?x");
		/// PRIORITY
		int priority6 = priority[5];
		operators.addElement(new Operator(name6, ifList6, addList6, deleteList6, priority6, blockList6));

		// OPERATOR 7
		/// NAME:?yの特徴は?x
		String name7 = "?y is a characteristic of ?x";
		/// IF
		Vector<String> ifList7 = new Vector<>();
		ifList7.addElement("#?y is a characteristic of ?x");
		ifList7.addElement("?x has a characteristic of ?y");
		ifList7.addElement("?x is name");
		//ifList7.addElement("?x has a characteristic of ?y");
		ifList7.addElement("?z on ?y");
		/// ADD-LIST
		Vector<String> addList7 = new Vector<>();
		addList7.addElement("?z on ?x");
		/// DELETE-LIST
		Vector<String> deleteList7 = new Vector<>();
		deleteList7.addElement("?z on ?y");
		/// BLOCK-LIST
		Vector<String> blockList7 = new Vector<>();
		blockList7.addElement("?y is a characteristic of ?x");
		blockList7.addElement("?x is name of ?z");
		/// PRIORITY
		int priority7 = priority[6];
		operators.addElement(new Operator(name7, ifList7, addList7, deleteList7, priority7, blockList7));

		// OPERATOR 8
		/// NAME:?yの特徴は?x
		String name8 = "?z is a characteristic of ?x";
		/// IF
		Vector<String> ifList8 = new Vector<>();
		ifList8.addElement("#?z is a characteristic of ?x");
		ifList8.addElement("?x has a characteristic of ?z");
		ifList8.addElement("?x is name");
		//ifList7.addElement("?x has a characteristic of ?y");
		ifList8.addElement("?z on ?y");
		/// ADD-LIST
		Vector<String> addList8 = new Vector<>();
		addList8.addElement("?x on ?y");
		/// DELETE-LIST
		Vector<String> deleteList8 = new Vector<>();
		deleteList8.addElement("?z on ?y");
		/// BLOCK-LIST
		Vector<String> blockList8 = new Vector<>();
		blockList8.addElement("?z is a characteristic of ?x");
		blockList8.addElement("?x is name of ?y");
		/// PRIORITY
		int priority8 = priority[7];
		operators.addElement(new Operator(name8, ifList8, addList8, deleteList8, priority8, blockList8));

		/*// OPERATOR 9
		/// NAME:?yの名前は?x
		String name9 = "?x is shape of ?y";
		/// IF
		Vector<String> ifList9 = new Vector<>();
		ifList9.addElement("#?x is shape of ?y");
		ifList9.addElement("?x has shape of ?y");
		ifList9.addElement("?x is name");
		//ifList9.addElement("?x has shape of ?y");
		ifList9.addElement("?x on ?z");
		/// ADD-LIST
		Vector<String> addList9 = new Vector<>();
		addList9.addElement("?y on ?z");
		/// DELETE-LIST
		Vector<String> deleteList9 = new Vector<>();
		deleteList9.addElement("?x on ?z");
		/// BLOCK-LIST
		Vector<String> blockList9 = new Vector<>();
		blockList9.addElement("?x is shape of ?y");
		blockList9.addElement("?z is a shape of ?x");
		/// PRIORITY
		int priority9 = priority[8];
		operators.addElement(new Operator(name9, ifList9, addList9, deleteList9, priority9, blockList9));

		// OPERATOR 10
		/// NAME:?yの名前は?x
		String name10 = "?x is shape of ?z";
		/// IF
		Vector<String> ifList10 = new Vector<>();
		ifList10.addElement("#?x is shape of ?z");
		ifList10.addElement("?x has shape of ?z");
		ifList10.addElement("?x is name");
		//ifList10.addElement("?x has shape of ?y");
		ifList10.addElement("?y on ?x");
		/// ADD-LIST
		Vector<String> addList10 = new Vector<>();
		addList10.addElement("?y on ?z");
		/// DELETE-LIST
		Vector<String> deleteList10 = new Vector<>();
		deleteList10.addElement("?y on ?x");
		/// BLOCK-LIST
		Vector<String> blockList10 = new Vector<>();
		blockList10.addElement("?x is shape of ?z");
		blockList10.addElement("?y is a shape of ?x");
		/// PRIORITY
		int priority10 = priority[9];
		operators.addElement(new Operator(name10, ifList10, addList10, deleteList10, priority10, blockList10));

		// OPERATOR 11
		/// NAME:?yの特徴は?x
		String name11 = "?y is a shape of ?x";
		/// IF
		Vector<String> ifList11 = new Vector<>();
		ifList11.addElement("#?y is a shape of ?x");
		ifList11.addElement("?x has shape of ?y");
		ifList11.addElement("?x is name");
		//ifList11.addElement("?x has shape of ?y");
		ifList11.addElement("?z on ?y");
		/// ADD-LIST
		Vector<String> addList11 = new Vector<>();
		addList11.addElement("?z on ?x");
		/// DELETE-LIST
		Vector<String> deleteList11 = new Vector<>();
		deleteList11.addElement("?z on ?y");
		/// BLOCK-LIST
		Vector<String> blockList11 = new Vector<>();
		blockList11.addElement("?y is a shape of ?x");
		blockList11.addElement("?x is shape of ?z");
		/// PRIORITY
		int priority11 = priority[10];
		operators.addElement(new Operator(name11, ifList11, addList11, deleteList11, priority11, blockList11));

		// OPERATOR 12
		/// NAME:?yの特徴は?x
		String name12 = "?z is a shape of ?x";
		/// IF
		Vector<String> ifList12 = new Vector<>();
		ifList12.addElement("#?z is a shape of ?x");
		ifList12.addElement("?x has shape of ?z");
		ifList12.addElement("?x is name");
		//ifList12.addElement("?x has shape of ?y");
		ifList12.addElement("?z on ?y");
		/// ADD-LIST
		Vector<String> addList12 = new Vector<>();
		addList12.addElement("?x on ?y");
		/// DELETE-LIST
		Vector<String> deleteList12 = new Vector<>();
		deleteList12.addElement("?z on ?y");
		/// BLOCK-LIST
		Vector<String> blockList12 = new Vector<>();
		blockList12.addElement("?z is a shape of ?x");
		blockList12.addElement("?x is shape of ?y");
		/// PRIORITY
		int priority12 = priority[11];
		operators.addElement(new Operator(name12, ifList12, addList12, deleteList12, priority12, blockList12));*/

		sortOp();

		//Collections.sort(operators, new Comparator() {
		//	public int compare(Object o1, Object o2) {
		//		Operator r1 = (Operator) o1;
		//		Operator r2 = (Operator) o2;
		//		return ((Integer) (r1.getPriority())).compareTo(r2.getPriority());
		//	}
		//});
		//System.out.println(operators.toString());
	}

	void sortOp() {
		operators.sort(new Comparator<Operator>() {

			@Override
			public int compare(Operator o1, Operator o2) {
				// TODO 自動生成されたメソッド・スタブ
				return ((Integer) (o1.getPriority())).compareTo(o2.getPriority());
			}

		});
		//System.out.println(operators);
	}


	public int judgeName(Operator op){
		if((op.name).contains("Place")){
			return 1;
		}else if((op.name).contains("remove")){
			return 2;
		}else if((op.name).contains("pick")){
			return 3;
		}else if((op.name).contains("put")){
			return 4;
		}else{
			return 0;
		}
	}

	void resetPriority(int judgeOperator){
		if(judgeOperator == 1){
			priority[0] = 7;
			priority[1] = 7;
			priority[2] = 2;
			priority[3] = 1;
		}else if(judgeOperator == 2){
			priority[0] = 7;
			priority[1] = 7;
			priority[2] = 1;
			priority[3] = 2;
		}else if(judgeOperator == 3){
			priority[0] = 2;
			priority[1] = 2;
			priority[2] = 7;
			priority[3] = 1;
		}else if(judgeOperator == 4){
			priority[0] = 2;
			priority[1] = 3;
			priority[2] = 1;
			priority[3] = 7;
		}
	}
}

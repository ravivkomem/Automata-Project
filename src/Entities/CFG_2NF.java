package Entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;


public class CFG_2NF extends ContextFreeGrammar {

	// I_g - The inverse graph of the
	private Graph<String> inverseGraph;
	private Set<String> nullable;
	
	public CFG_2NF(Set<String> variableSet, Set<Character> terminalsSet, Multimap<String, String> rewriteRules,
			String s0) {
		super(variableSet, terminalsSet, rewriteRules, s0);
		applyBinTransofrmation();
		calculateNullable();
		calculateInverseGraph();
	}
	
	@Override
	public void findParseOfWord (String word)
	{
		int n = word.length();
		ArrayList<String> wordAsArray = breakToStringArray(word);
		
		ArrayList<ArrayList<Set<String>>> tableMatrix = new ArrayList<ArrayList<Set<String>>>();
		for (int i = 0; i < n; i++)
		{
			ArrayList<Set<String>> tableRow = new ArrayList<Set<String>>();
			tableMatrix.add(tableRow);
			for (int j = 0; j < n ; j++)
			{
				Set<String> tableCell = new HashSet<String>();
				if (j < i)
				{
					tableCell.add("Empty");
				}
				tableMatrix.get(i).add(tableCell);
			}
		}
		
		/* Step 1 - Add all the elements that can derive word_i,i */
		for (int i = 0; i < n ; i++)
		{
			Set<String> set = new HashSet<String>();
			set.add(wordAsArray.get(i));
			tableMatrix.get(i).get(i).addAll(getUnitSet(set));
		}
		
		/* Step 2 - Advance and break at all lengths */
		for (int j = 1; j < n; j++)
		{
			for (int i = j-1; i>=0; i--)
			{
				Set<String> cellSet = new HashSet<String>();
				for (int k = i; k <= j-1; k++)
				{
					for (String y : tableMatrix.get(i).get(k))
					{
						for (String z : tableMatrix.get(k+1).get(j))
						{
							String requiredRule = y+z;
							for (String nonTerminal : variableSet)
							{
								for (String rule: rewriteRules.get(nonTerminal))
								{
									if (rule.equals(requiredRule))
									{
										cellSet.add(nonTerminal);
									}
								}
							}
						}
					}
				}
				tableMatrix.get(i).get(j).addAll(getUnitSet(cellSet));
			}
		}
		
		/* Step 3 - Print the table matrix for programmar reffernce */
		System.out.println("The parsing table is:\n");
		printTable(tableMatrix);
		System.out.println();
		
		/* Step 4 - Print proper message if word can be derived or not */
		if (tableMatrix.get(0).get(n-1).contains(S0))
		{
			System.out.println("The word: '" + word +"' can be derived from the grammar");
		}
		else
		{
			System.out.println("The word: '" + word +"' CAN'T be derived from the grammar");
		}
	}
	
	private void printTable (ArrayList<ArrayList<Set<String>>> tableMatrix)
	{
		for (int i = 0; i < tableMatrix.size(); i++)
		{
			for (int j = 0; j < tableMatrix.get(i).size() ; j++ )
			{
				System.out.print(tableMatrix.get(i).get(j));
			}
			System.out.println("");
		}
	}
	
	private void applyBinTransofrmation()
	{
		boolean newRulesBroken = false;
		
		HashSet<String> newVariables = new HashSet<String>();
		for (String var : variableSet)
		{
			HashSet<String> newRules = new HashSet<String>();
			HashSet<String> removeRules = new HashSet<String>();
			
			for (String rule : rewriteRules.get(var))
			{
				ArrayList<String> varList = breakToStringArray(rule);
				int varCount = varList.size();
				if (varCount > 2)
				{
					removeRules.add(rule);
					String newVar = "";
					String newRule = "";
					/* Construct the new rule */
					for (int i = 1; i < varCount ; i++)
					{
						newRule += varList.get(i);
					}
					
					newVar = CFG_2NF.REPLACEMENT_VARS_ARRAY[this.BREAK_VARS_COUNT/9]+Integer.toString((this.BREAK_VARS_COUNT)%9 + 1);
					BREAK_VARS_COUNT++;
					newVariables.add(newVar);
					newRewriteRules.put(newVar, newRule);
					newRulesBroken = true;
		
					String updatedOriginalRule = varList.get(0)+newVar;
					newRules.add(updatedOriginalRule);
				}
			}
			
			rewriteRules.get(var).removeAll(removeRules);
			rewriteRules.get(var).addAll(newRules);
		}
		
		variableSet.addAll(newVariables);
		for (String newVar : newVariables)
		{
			System.out.println("#BIN Transformation - Added new rule: " + newVar + " ---> " + newRewriteRules.get(newVar));
			rewriteRules.put(newVar, newRewriteRules.get(newVar));
		}
		
		if (newRulesBroken == true)
		{
			applyBinTransofrmation();
		}
	}

	private void calculateNullable()
	{
		nullable = new HashSet<String>();
		Stack<String> todoStack = new Stack<String>();
		HashMultimap<String, ArrayList<String>> occurs = HashMultimap.create();
		
		String A;
		String B;
		String C;
		String alpha;
		String beta;
		
		for (String var : variableSet)
		{
			ArrayList<String> occursArrayList = new ArrayList<String>();
			for (String rule : rewriteRules.get(var))
			{
				occursArrayList.clear();
				ArrayList<String> ruleArray = breakToStringArray(rule);
				/* Removing for the check any possible epsilon vars */
				ruleArray.remove("e");
				ruleArray.remove("e");
				
				switch (ruleArray.size())
				{
					case 0: /* A-->e */
						todoStack.push(var);
						nullable.add(var);
						break;
					case 1: /* A-->alpha */
						alpha = ruleArray.get(0);
						if (variableSet.contains(alpha))
						{
							occursArrayList.add(var);
							occurs.put(alpha, occursArrayList);
						}
						break;
					case 2: /* A-->alpha beta */
						alpha = ruleArray.get(0);
						beta = ruleArray.get(1);
						if (variableSet.contains(alpha) && variableSet.contains(beta))
						{
							/* Handling alpha */
							occursArrayList.add(var);
							occursArrayList.add(beta);
							occurs.put(alpha, occursArrayList);
							
							occursArrayList.clear();
							/* Handling beta */
							occursArrayList.add(var);
							occursArrayList.add(alpha);
							occurs.put(beta, occursArrayList);
						}	
						break;
					default:
						System.out.println("ERROR: Check nullable method");	
				}
			}
		}
		
		
		while (!todoStack.isEmpty())
		{
			B = todoStack.pop();
			for (ArrayList<String> occursArrayList : occurs.get(B))
			{
				int occursSize = occursArrayList.size();
				switch (occursSize)
				{
					case 1: /* A ---> B ---*> e */
						A = occursArrayList.get(0);
						if (!nullable.contains(A))
						{
							nullable.add(A);
							todoStack.push(A);
						}
						break;
					case 2: /* A--->BC or CB */
						A = occursArrayList.get(0);
						C = occursArrayList.get(1);
						
						if (nullable.contains(C))
						{
							if (!nullable.contains(A))
							{
								nullable.add(A);
								todoStack.push(A);
							}
						}
						break;
					default:
						System.out.println("ERROR: Check nullable method");
				}
			}
		}
		
		
	}
	
	private void calculateInverseGraph()
	{
		inverseGraph = new Graph<String>();
		for (String var: variableSet)
		{
			inverseGraph.addVertex(var);
		}
		
		for (String var : variableSet)
		{
			for (String rule : rewriteRules.get(var))
			{
				ArrayList<String> ruleArray = breakToStringArray(rule);
				/* Ignore epsilon letters */
				ruleArray.remove(String.valueOf(CFG_2NF.NULL_LETTER));
				ruleArray.remove(String.valueOf(CFG_2NF.NULL_LETTER));
				
				int ruleSize = ruleArray.size();
				
				switch(ruleSize)
				{
					case 0:
						break;
					case 1:
						inverseGraph.addEdge(ruleArray.get(0), var);
						break;
					case 2:
						String x = ruleArray.get(0);
						String y = ruleArray.get(1);
						if (nullable.contains(x))
						{
							inverseGraph.addEdge(y, var);
						}
						if (nullable.contains(y))
						{
							inverseGraph.addEdge(x, var);
						}
						break;
					default:
						System.out.println("ERROR: Check method to create inverse graph");
				}
			}
		}
	}
	
	/*
	 * Calculates U_g*(M) -
	 * The set of all variables that can be derived to any of the variables
	 * in set M, simply implemented as BFS from all the nodes in set M
	 */
	private Set<String> getUnitSet(Set<String> M)
	{
		Set<String> unitSet = new HashSet<String>();
		
		Queue<String> nodesToVist = new LinkedList<String>();
		nodesToVist.addAll(M);
		unitSet.addAll(M);
		
		while(!nodesToVist.isEmpty())
		{
			String node = nodesToVist.poll();
			for (String adjcentNode : inverseGraph.getAdjacentCollection(node))
			{
				if (!unitSet.contains(adjcentNode))
				{
					nodesToVist.add(adjcentNode);
					unitSet.add(adjcentNode);
				}
			}
		}
		
		return unitSet;
	}
	
	/*
	 * Converts a rule into an array of terminals and non terminals
	 */
	private ArrayList<String> breakToStringArray(String rule) {
		
		ArrayList<String> stringArr = new ArrayList<String>();
		int beginSubstring=0;
		for(int endSubstring = 1; endSubstring <= rule.length(); endSubstring++)
		{
			String subRule = rule.substring(beginSubstring, endSubstring);
			// $ not part of the SIGMA union Epsilon
			Character c = '$';
			if (subRule.length() == 1)
			{
				c = subRule.charAt(0);
			}
			if (variableSet.contains(subRule) || terminalsSet.contains(c) || c.equals(CFG_2NF.NULL_LETTER))
			{
				stringArr.add(subRule);
				beginSubstring = endSubstring;
			}
		}
		return stringArr;
	}

}

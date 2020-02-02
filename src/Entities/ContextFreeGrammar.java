package Entities;

import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ContextFreeGrammar {

	/* **************************************
	 * *********** Static Variables *********
	 * **************************************/
	public static final char NULL_LETTER = 'e';
	public static final String REPLACE_NULL_REGEX = "["+NULL_LETTER+"]";
	public static final String[] REPLACEMENT_VARS_ARRAY = {"P", "R", "K", "W", "Z", "X"};
	
	private static Map<String, String> newRewriteRules = new HashMap<String,String>();
	private static int BREAK_VARS_COUNT = 0;
	
	/* **************************************
	 * ********** Private Variables *********
	 * **************************************/
	private Set<String> variableSet; // V
	private Set<Character> terminalsSet; // Sigma
	private Multimap<String, String> rewriteRules; // P
	private String S0; // Start variable
	private int newVariableNumber = 1;
	
	/* **************************************
	 * ********** Constructors **************
	 * **************************************/
	public ContextFreeGrammar(Set<String> variableSet, Set<Character> terminalsSet,
			Multimap<String, String> rewriteRules, String s0) {
		super();
		this.variableSet = variableSet;
		this.terminalsSet = terminalsSet;
		this.rewriteRules = rewriteRules;
		S0 = s0;
	}
	
	/* **************************************
	 * ********** Public Methods ************
	 * **************************************/
	
	/**
	 * Few simple checks if the grammar is valid or not
	 * @return true if valid, false otherwise
	 */
	public boolean isValidGrammar()
	{	
		/* CFG must have some variables */
		if (variableSet.isEmpty())
		{
			return false;
		}
		
		/* S0 must be a valid String */
		if (S0.isEmpty())
		{
			return false;
		}
		
		/* The variable Set must include the start variable */
		if (!variableSet.contains(S0))
		{
			return false;
		}
		
		/* the terminalsSet must include some letters */
		if (terminalsSet.isEmpty())
		{
			return false;
		}
		
		/* the terimnalsSet must exclude the null letter */
		if (terminalsSet.contains(NULL_LETTER))
		{
			return false;
		}
		
		/* For every variable, must be valid, rewrite rules should include it, and it should have rules */
		for (String v : variableSet)
		{
			if (v.isEmpty())
			{
				return false;
			}
			
			if (!rewriteRules.keySet().contains(v))
			{
				return false;
			}
			
			if (rewriteRules.get(v).isEmpty())
			{
				return false;
			}
		}
	
		return true;
	}
	
	public void covertToChomanskyNormalForm()
	{
		/* This algorithm consists of several steps */
		this.handleInitialNonTerminal();
		this.simplifyGrammar();
		this.removedTerminalNonTerminalMix();
		this.breakLongRules();
	}

	private Map<String, String> getAllBreakRules(String rule)
	{
		Set<String> breakSet = new HashSet<String>();
		ArrayList<String> varList = this.breakToVarArray(rule);
		if (varList.size() > 2)
		{
			//String newVar = 
			
			
		}
		
		
		
		return null;
	}
	
	
	private void breakLongRules() {
		
		boolean newRulesBroken = false;
		for (String var : variableSet)
		{
			HashSet<String> newRules = new HashSet<String>();
			HashSet<String> removeRules = new HashSet<String>();
			
			for (String rule : rewriteRules.get(var))
			{
				ArrayList<String> varList = this.breakToVarArray(rule);
				int varCount = varList.size();
				if (varCount > 2)
				{
					removeRules.add(rule);
					String newVar = "";
					String newNonTerminalRule = "";
					/* Construct the new rule */
					for (int i = 1; i < varCount ; i++)
					{
						newNonTerminalRule += varList.get(i);
					}
					
					/* Check if it is already exists */
					if (newRewriteRules.values().contains(newNonTerminalRule))
					{
						for (String s : newRewriteRules.keySet())
						{
							if (newRewriteRules.get(s).equals(newNonTerminalRule))
							{
								newVar = s;
								break;
							}
						}
					}
					else
					{
						newVar = REPLACEMENT_VARS_ARRAY[BREAK_VARS_COUNT/9]+Integer.toString((BREAK_VARS_COUNT)%9 + 1);
						BREAK_VARS_COUNT++;
						newRewriteRules.put(newVar, newNonTerminalRule);
						newRulesBroken = true;
					}
					String newRule = varList.get(0)+newVar;
					newRules.add(newRule);
				}
			}
			
			rewriteRules.get(var).removeAll(removeRules);
			rewriteRules.get(var).addAll(newRules);
		}
		
		variableSet.addAll(newRewriteRules.keySet());
		for (String newVar : newRewriteRules.keySet())
		{
			System.out.println("Added new rule: " + newVar + " ---> " + newRewriteRules.get(newVar) + " , derived from broken long rule");
			rewriteRules.put(newVar, newRewriteRules.get(newVar));
		}
		
		if (newRulesBroken == true)
		{
			breakLongRules();
		}
	}

	private void removedTerminalNonTerminalMix() {
		
		int terminalCounter = 1;
		Map<Character, String> terminalRules = new HashMap<Character, String>();
		for (Character terminal : terminalsSet)
		{
			String newVar = "T"+terminalCounter;
			terminalRules.put(terminal, newVar);
			terminalCounter++;
		}
		
		HashSet<String> varsToAdd = new HashSet<String>();
		for (String var : variableSet)
		{
			HashSet<String> rulesToRemove = new HashSet<String>();
			HashSet<String> rulesToAdd = new HashSet<String>();
			
			for (String rule : rewriteRules.get(var))
			{
				if (isTerminal(rule) == false && isVariable(rule) == false)
				{
					for (Character terminal : terminalsSet)
					{
						if (rule.contains(String.valueOf(terminal)))
						{
							rulesToRemove.add(rule);
							String terminalVar = terminalRules.get(terminal);
							rulesToAdd.add(rule.replaceAll("["+terminal+"]", terminalVar));
							varsToAdd.add(terminalVar);
						}
					}
				}
			}
			
			rewriteRules.get(var).addAll(rulesToAdd);
			rewriteRules.get(var).removeAll(rulesToRemove);
		}
		
		variableSet.addAll(varsToAdd);
		
		for (String varToAdd : varsToAdd)
		{
			for (Character c : terminalRules.keySet())
			{
				if (terminalRules.get(c).equals(varToAdd))
				{
					rewriteRules.put(varToAdd, String.valueOf(c));
				}
			}
		}
	}

	private void handleInitialNonTerminal() {
		
		boolean startNonTerminalAppeardRHS = false; 
		for (String var : variableSet)
		{
			for (String rule : rewriteRules.get(var))
			{
				if (rule.contains(S0))
				{
					startNonTerminalAppeardRHS = true;
				}
			}
			
		}
		
		/* Need to crate new start non terminal */
		if (startNonTerminalAppeardRHS == true)
		{
			String newStartNonTerminal = S0+"_new";
			rewriteRules.put(newStartNonTerminal, S0);
			S0 = newStartNonTerminal;
			variableSet.add(newStartNonTerminal);
		}
	}

	public void simplifyGrammar()
	{
		this.removeNullableVariables();
		this.removeUnitProductions();
		this.removeUnderiableVariables();
		this.removeUselessProductionVariables();
	}
	
	private void cleanNullCharcters()
	{
		for (String var : variableSet)
		{
			HashSet<String> rulesToRemove = new HashSet<String>();
			HashSet<String> rulesToAdd = new HashSet<String>();
			for (String currentRule : rewriteRules.get(var))
			{
				if (!currentRule.equals(String.valueOf(NULL_LETTER)) && currentRule.contains(String.valueOf(NULL_LETTER)))
				{
					rulesToRemove.add(currentRule);
					String ruleToAdd = currentRule.replaceAll(REPLACE_NULL_REGEX, "");
					rulesToAdd.add(ruleToAdd);
				}
			}
			
			rewriteRules.get(var).removeAll(rulesToRemove);
			rewriteRules.get(var).addAll(rulesToAdd);
		}
	}
	
	
	private void removeUnitProductions() {
		
		/* Mapping all the unit productions and the replacement rules */
		int newRulesAdded = 0;
		HashMultimap<String, String> rulesToRemove = HashMultimap.create();
		HashMultimap<String, String> rulesToUse = HashMultimap.create();
		
		for (String var : variableSet)
		{
			for (String varRule : rewriteRules.get(var))
			{
				/* This is unit production */
				if (this.isVariable(varRule))
				{
					System.out.println("# Removed rule: " + var + " ---> " + varRule + " , Becuase this is unit production");
					rulesToRemove.put(var, varRule);
				}
				/* This is replacement for unit production */
				else
				{
					rulesToUse.put(var, varRule);
				}
			}
		}
		
		/* Adding all the replacement rules */
		for (String var : rulesToRemove.keys())
		{
			for (String unitProduction : rulesToRemove.get(var))
			{
				for (String newRule : rulesToUse.get(unitProduction))
				{
					System.out.println("# Added rule: " + var + " ---> " + newRule + " , To replace unit production");
					rewriteRules.put(var, newRule);
					newRulesAdded++;
				}
			}
		}
		
		/* Remove all the unit production rules */
		for (String var : rulesToRemove.keys())
		{
			rewriteRules.get(var).removeAll(rulesToRemove.get(var));
		}
		
		/* Check if this needed another iteration */
		if (newRulesAdded > 0)
		{
			this.removeUnitProductions();
		}
	}

	private void removeNullableVariables() {
		
		this.cleanNullCharcters();
		
		HashSet<String> nullableVariablesSet = new HashSet<String>();
		HashSet<String> removeVariablesSet = new HashSet<String>();
		
		/* Get all the nullable variables */
		for (String var : variableSet)
		{
			if (!var.equals(S0))
			{
				boolean containsMoreThanNull = false;
				for (String currentRule : rewriteRules.get(var))
				{
					
					if (currentRule.equals(String.valueOf(NULL_LETTER)))
					{
						nullableVariablesSet.add(var);
						System.out.println("# Removed rule: " + var + " ---> " + NULL_LETTER + " , because it is null rule, updating all the rules dpending on " + var);
					}
					else
					{
						containsMoreThanNull = true;
					}
				}
				if (nullableVariablesSet.contains(var))
				{
					rewriteRules.get(var).remove(String.valueOf(NULL_LETTER));
					/* It was only a null rule */
					if (containsMoreThanNull == false)
					{
						removeVariablesSet.add(var);
					}
				}
			}
				
		}
		
		variableSet.removeAll(removeVariablesSet);
		
		for (String variable : variableSet)
		{
			HashSet<String> newRules = new HashSet<String>();
			for (String rule : rewriteRules.get(variable))
			{
				for (String nullableVar : nullableVariablesSet)
				{
					if(rule.contains(nullableVar))
					{
						/* Get all occurrences of null var */
						Matcher m = Pattern.compile("(?=("+nullableVar+"))").matcher(rule);
						List<Integer> positionSet = new ArrayList<Integer>();
						while (m.find())
						{
							positionSet.add(m.start());
						}
						
						ArrayList<ArrayList<Integer>> allSubSets = new ArrayList<ArrayList<Integer>>();
						for (int i = 0; i < (1<<positionSet.size()); i++) 
				        { 
							ArrayList<Integer> arrayList = new ArrayList<Integer>();
				            for (int j = 0; j < positionSet.size(); j++)
				            { 	
				            	if ((i & (1 << j)) > 0) 
				            		arrayList.add(positionSet.get(j));
				            }
				            if (!arrayList.isEmpty())
				            {
				            	allSubSets.add(arrayList);
				            }
				        } 
						
						for (ArrayList<Integer> positionList : allSubSets)
						{
							String newRule = "";
							int startIndex = 0;
							for (Integer pos : positionList)
							{
								newRule += rule.substring(startIndex, pos);
								newRule += NULL_LETTER;
								startIndex = pos+nullableVar.length();
							}
							newRule += rule.substring(startIndex);
							if (!newRule.equals(""))
							{
								newRules.add(newRule);
								System.out.println("# Adding rule: " + variable + " ---> " + newRule);
							}
							else if (variable.equals(S0))
							{
								newRules.add(String.valueOf(NULL_LETTER));
								System.out.println("# Adding rule: " + variable + " ---> " + newRule);
							}
						}
					}
				}
			}
			rewriteRules.get(variable).addAll(newRules);
		}
		
		if (!nullableVariablesSet.isEmpty())
		{
			this.removeNullableVariables();
		}
	}

	private void removeUselessProductionVariables() {
		
		HashSet<String> varsToRemoveSet = new HashSet<String>();
		HashSet<String> rulesToRemoveSet = new HashSet<String>();
		
		for (String var :variableSet)
		{
			if (!var.equals(S0))
			{
				boolean isUsefull = false;
				for(String varRule: rewriteRules.get(var))
				{
					if (!varRule.contains(var))
					{
						isUsefull = true;
						break;
					}
				}
				
				if (isUsefull == false)
				{
					System.out.println("# Removed Variable: " + var + " --->" + rewriteRules.get(var).toString() + ", Could not generate any word from this rule");
					varsToRemoveSet.add(var);
					rewriteRules.removeAll(var);
				}
			}
		}
		
		variableSet.removeAll(varsToRemoveSet);
		for (String removedVar : varsToRemoveSet)
		{
			for (String currentVar : variableSet)
			{
				for (String currentVarRule : rewriteRules.get(currentVar))
				{
					if (currentVarRule.contains(removedVar))
					{
						System.out.println("# Removed the following rule: " + currentVar + " ---> " + currentVarRule + ", Becuase the variable " + removedVar + " was removed");
						rulesToRemoveSet.add(currentVarRule);
					}
				}
			}
		}
	
		rewriteRules.values().removeAll(rulesToRemoveSet);
		
	}

	private void removeUnderiableVariables() {

		HashSet<String> varsToRemoveSet = new HashSet<String>();
		
		for (String var : variableSet)
		{
			if (!var.equals(S0))
			{
				boolean isDerived = false;
				for(String rule: rewriteRules.values())
				{
					if(rule.contains(var))
					{
						isDerived = true;
						break;
					}
				}
				if (isDerived == false)
				{
					System.out.println("# Removed Variable: " + var + " --->" + rewriteRules.get(var).toString() + ", Could not be derived from " + S0);
					varsToRemoveSet.add(var);
					rewriteRules.removeAll(var);
				}
			}
		}
		
		variableSet.removeAll(varsToRemoveSet);
		/* Repeat check because maybe another variable is now underivable */
		if (!varsToRemoveSet.isEmpty())
		{
			removeUnderiableVariables();
		}
	}

	public String toString()
	{
		String contextFreeGrammerString = "";
		contextFreeGrammerString += S0 + " ---> " + rewriteRules.get(S0).toString() + "\n";
		for (String v : variableSet)
		{
			if (!v.equals(S0))
			{
				contextFreeGrammerString+= v + " ---> " + rewriteRules.get(v).toString() + "\n";
			}
		}
		return contextFreeGrammerString;
	}
	
	private boolean isTerminal(String s)
	{
		for (Character c : terminalsSet)
		{
			if (s.equals(String.valueOf(c)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isVariable(String s)
	{
		for (String var : variableSet)
		{
			if(s.equals(var))
			{
				return true;
			}
		}
		return false;
	}
	
	private ArrayList<String> breakToVarArray(String varOnlyRule)
	{
		ArrayList<String> varArray = new ArrayList<String>();
		
		for (Character c : terminalsSet)
		{
			if (varOnlyRule.contains(String.valueOf(c)))
			{
				return varArray;
			}
		}
		
		if (varOnlyRule.contains(String.valueOf(NULL_LETTER)))
		{
			return varArray;
		}
		
		int startIndex = 0;
		for (int endIndex = 1; endIndex <= varOnlyRule.length(); endIndex++)
		{
			String currentSubString = varOnlyRule.substring(startIndex, endIndex);
			if (isVariable(currentSubString))
			{
				varArray.add(currentSubString);
				startIndex = endIndex;
			}	
		}		
		return varArray;
	}
	
	public void findParseOfWord (String w)
	{
		for (String nonTerminal : variableSet )
		{
			if (w.contains(nonTerminal))
			{
				System.out.println("The word is invalid, because it contains non terminal: " + nonTerminal);
				return;
			}
		}
		
		/* Creating the arrayList matrix */
		ArrayList<ArrayList<ArrayList<NormlizedRule>>> arrayListMatrix = new ArrayList<ArrayList<ArrayList<NormlizedRule>>>();
		int n = w.length();
		for (int i = 0; i < n; i ++)
		{
			ArrayList<ArrayList<NormlizedRule>>  matrixRow = new ArrayList<ArrayList<NormlizedRule>>();
			for (int j = 0; j < n; j++)
			{
				ArrayList<NormlizedRule> element = new ArrayList<NormlizedRule>();
				matrixRow.add(element);
			}
			arrayListMatrix.add(matrixRow);
		}
		
		for (int i = 0; i < n ; i++)
		{
			char terminal = w.charAt(i);
			String terminalStr = String.valueOf(terminal);
			for (String nonTerminal : variableSet)
			{
				for (String rule : rewriteRules.get(nonTerminal))
				{
					if (rule.equals(terminalStr))
					{
						arrayListMatrix.get(i).get(i).add(new NormlizedRule(nonTerminal, terminalStr));
					}
				}
			}
		}
		
		for (int i = 1; i < n ; i++)
		{
			for (int j = i-1; j >= 0 ; j--)
			{
				for (int k = j+1; k <= i; k++)
				{
					for (NormlizedRule b : arrayListMatrix.get(j).get(k-1))
					{
						for (NormlizedRule c: arrayListMatrix.get(k).get(i))
						{
							String requiredRule = b.getStartNonTerminal()+c.getStartNonTerminal();
							for (String nonTerminal : variableSet)
							{
								for (String rule: rewriteRules.get(nonTerminal))
								{
									if (rule.equals(requiredRule))
									{
										arrayListMatrix.get(j).get(i).add(new NormlizedRule(nonTerminal, b, c));
									}
								}
							}
						}	
					}
				}
			}
		}
		
		//if (arrayListMatrix.get(0).get(n-1).contains(S0))
		boolean deriveAble = false;
		
		for (NormlizedRule nRule : arrayListMatrix.get(0).get(n-1))
		{
			if (nRule.getStartNonTerminal().equals(S0))
			{
				System.out.println("The Word: '" + w +"' can be derived from the grammar");
				deriveAble = true;
				System.out.println("\nThe parse tree is:");
				Stack<NormlizedRule> normalizedRulesStack = new Stack<NormlizedRule>();
				normalizedRulesStack.push(nRule);
				ArrayList<String> currWord = new ArrayList<String>();
				currWord.add(nRule.getStartNonTerminal());
				System.out.print(nRule.getStartNonTerminal());
				
				while (!normalizedRulesStack.isEmpty())
				{
					System.out.print(" ---> ");
					NormlizedRule currRule = normalizedRulesStack.pop();
					
					int locationInWord;
					boolean found = false;
					
					for (locationInWord = 0; locationInWord < currWord.size() && (found == false) ; locationInWord ++)
					{
						if (currWord.get(locationInWord).equals(currRule.getStartNonTerminal()))
						{
							found = true;
						}
					}
					
					locationInWord -= 1;
					
					
					if (currRule.isToTerminal() == true)
					{
						currWord.add(locationInWord, currRule.getTerminal());
						currWord.remove(locationInWord+1);
					}
					else
					{
						currWord.add(locationInWord, currRule.getNonTerminalC().getStartNonTerminal());
						currWord.add(locationInWord, currRule.getNonTerminalB().getStartNonTerminal());
						normalizedRulesStack.push(currRule.getNonTerminalC());
						normalizedRulesStack.push(currRule.getNonTerminalB());
						currWord.remove(locationInWord+2);
					}
					
					for (String str : currWord)
					{
						System.out.print(str);
					}
					System.out.println("");
					
				}	
				break;
			}
		}
		
		if (deriveAble == true)
		{
			
			
		}
		else
		{
			System.out.println("The word can not be derived from the lanaguge");
		}
	}
	
	protected class NonTerminalWithIndexs 
	{
		private String nonTerminal;
		private int i;
		private int j;
		
		public NonTerminalWithIndexs(String nonTerminal, int i, int j)
		{
			this.nonTerminal = nonTerminal;
			this.i = i;
			this.j = j;
		}
		
		private String getNonTerminal()
		{
			return this.nonTerminal;
		}
		
		private int getIndexI()
		{
			return i;
		}
		
		private int getIndexJ()
		{
			return j;
		}
	}
	
	/* A --> BC 
	 * OR
	 * A --> a*/
	protected class NormlizedRule 
	{
		private String startNonTerminal;
		private NormlizedRule b;
		private NormlizedRule c;
		private String terminal;
		private boolean isToTerminal;
		
		public NormlizedRule (String startNonTerminal, NormlizedRule b, NormlizedRule c)
		{
			this.startNonTerminal = startNonTerminal;
			this.b = b;
			this.c = c;
			isToTerminal = false;
		}
		
		public NormlizedRule (String startNonTerminal, String terminal)
		{
			this.startNonTerminal = startNonTerminal;
			this.terminal = terminal;
			isToTerminal = true;
		}
		
		public NormlizedRule getNonTerminalB()
		{
			if (isToTerminal == true)
			{
				return null;
			}
			else
			{
				return b;
			}
		}
		
		public NormlizedRule getNonTerminalC()
		{
			if (isToTerminal == true)
			{
				return null;
			}
			else
			{
				return c;
			}
		}
		
		public String getTerminal()
		{
			return terminal;
		}
		
		public boolean isToTerminal()
		{
			return isToTerminal;
		}
		
		public String getStartNonTerminal()
		{
			return startNonTerminal;
		}
		
		public String toString()
		{
			String str = startNonTerminal+"--->";
			if (isToTerminal == true)
			{
				str +=terminal;
			}
			else
			{
				str += b.toString();
				str += c.toString();
			}
			return str;
		}
	}

	public Set<String> generateAllWordWithLength(int length)
	{
		Set<String> grammarWords = new HashSet<String>();
		if (length < 0)
		{
			return grammarWords;
		}
		
		if (rewriteRules.get(S0).contains(String.valueOf(NULL_LETTER)))
		{
			grammarWords.add(String.valueOf(NULL_LETTER));
		}
		
		
		Set<String> incompleteRules = new HashSet<String>();
		incompleteRules.add(S0);
		
		/*
		 * For every iteration we will advance
		 * All the incomplete rules by 1 derive rule
		 * Following Left TO Right opening
		 * 
		 * The number of steps required in CNF Grammar to create a word with length n
		 * is 2*n + 1
		 */
		for (int i = 0; i < length*2+1 ; i++)
		{
			Set<String> nextIncompleteRules = new HashSet<String>();
			for (String incompleteRule : incompleteRules)
			{
				int j = 0;
				/*
				 * All terminals should be characters with length 1
				 * This step is done in order to find the first nonTerminal in the rule
				 */
				while (isTerminal(incompleteRule.substring(j, j+1)) == true)
				{
					j++;
					if (j == incompleteRule.length())
					{
						break;
					}
				}
				
				/* It contains only terminals, therfore it is a valid word
				 * derived from the grammar
				 */
				if (j == incompleteRule.length())
				{
					grammarWords.add(incompleteRule);
				}
				else
				{
					int start = j;
					while (isVariable(incompleteRule.substring(start, j+1)) == false)
					{
						j++;
					}
					
					String firstVariable = incompleteRule.substring(start, j+1);
					
					for (String rule : rewriteRules.get(firstVariable))
					{
						String incompleteRuleStart = incompleteRule.substring(0,start);
						String incompleteRuleEnd = incompleteRule.substring(j+1, incompleteRule.length());
						String newIncompleteRule = incompleteRuleStart+rule+incompleteRuleEnd;
						nextIncompleteRules.add(newIncompleteRule);
					}
				}
				
			}
			incompleteRules = nextIncompleteRules;
		}
		return grammarWords;
	}
}



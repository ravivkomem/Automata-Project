package Entities;

import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

import com.google.common.collect.HashMultimap;


public class MainClass {

	public static void main(String[] args) {
		HashSet<String> variableSet = new HashSet<String>();
		variableSet.add("S");
		variableSet.add("A");
		variableSet.add("B");
		variableSet.add("C");
//		variableSet.add("D");;
//		variableSet.add("E");
		HashSet<Character> terminalsSet = new HashSet<Character>();
		terminalsSet.add('a');
		terminalsSet.add('b');
		terminalsSet.add('+');
		terminalsSet.add('*');
		terminalsSet.add('(');
		terminalsSet.add(')');
		terminalsSet.add('0');
		terminalsSet.add('1');
		
		HashMultimap<String, String> rewriteRules = HashMultimap.create();
		
		Collection<String> S_Rules =  new HashSet<String>();
		S_Rules.add("A");
		S_Rules.add("S+A");
		S_Rules.add("S+A");
		
		Collection<String> A_Rules =  new HashSet<String>();
		A_Rules.add("B");
		A_Rules.add("A*B");
		
		Collection<String> B_Rules =  new HashSet<String>();
		B_Rules.add("aC");
		B_Rules.add("bC");
		B_Rules.add("(S)");
		
		Collection<String> C_Rules =  new HashSet<String>();
		C_Rules.add("0C");
		C_Rules.add("1C");
		C_Rules.add("e");
		
		Collection<String> D_Rules =  new HashSet<String>();
		
		Collection<String> E_Rules =  new HashSet<String>();
		
		rewriteRules.get("S").addAll(S_Rules);
		rewriteRules.get("A").addAll(A_Rules);
		rewriteRules.get("B").addAll(B_Rules);
		rewriteRules.get("C").addAll(C_Rules);
//		rewriteRules.get("D").addAll(D_Rules);
//		rewriteRules.get("E").addAll(E_Rules);
		
		ContextFreeGrammar cfg = new ContextFreeGrammar(variableSet, terminalsSet, rewriteRules, "S");
		CFG_2NF cfg2NF = new CFG_2NF(variableSet, terminalsSet, rewriteRules, "S");
		System.out.println(cfg2NF);
		cfg2NF.findParseOfWord("(a0+b)*a");
		
		
		//System.out.println(cfg);
		//cfg.covertToChomanskyNormalForm();
		//System.out.println(cfg);
		//cfg.findParseOfWord("bbaa");
		Set<String> grammarSubset = cfg.generateLengthSteps(8);
		if (grammarSubset.contains("(a0+b)*a"))
		{
			System.out.println("According to the exponential method,\n"
					+ "The word can be derived from the grammar");
		}
		else
		{
			System.out.println("Check please");
		}
	}

}

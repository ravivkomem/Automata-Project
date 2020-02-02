package Entities;

import java.util.HashSet;
import java.util.Collection;

import com.google.common.collect.HashMultimap;


public class MainClass {

	public static void main(String[] args) {
		HashSet<String> variableSet = new HashSet<String>();
		variableSet.add("S");
		variableSet.add("A");
		variableSet.add("B");
		variableSet.add("C");
		variableSet.add("D");
		variableSet.add("E");
		HashSet<Character> terminalsSet = new HashSet<Character>();
		terminalsSet.add('a');
		terminalsSet.add('b');
		terminalsSet.add('c');
		terminalsSet.add('d');
		
		HashMultimap<String, String> rewriteRules = HashMultimap.create();
		
		Collection<String> S_Rules =  new HashSet<String>();
//		S_Rules.add("A");
//		S_Rules.add("B");
//		S_Rules.add("BB");
//		S_Rules.add("BdBsB");
//		S_Rules.add("E");
//		S_Rules.add("AeSeAeSeAeA");
//		S_Rules.add("aeB");
		S_Rules.add("AB");
		S_Rules.add("a");
		
		
		Collection<String> A_Rules =  new HashSet<String>();
//		A_Rules.add("a");
//		A_Rules.add("AA");
//		A_Rules.add("eB");
//		A_Rules.add("Se");
		A_Rules.add("BC");
		A_Rules.add("a");
		
		Collection<String> B_Rules =  new HashSet<String>();
//		B_Rules.add("b");
//		B_Rules.add("BB");
//		B_Rules.add("e");
//		B_Rules.add("ec");
//		B_Rules.add("ebeee");
//		B_Rules.add("e");
		B_Rules.add("AB");
		B_Rules.add("b");
		
		Collection<String> C_Rules =  new HashSet<String>();
//		C_Rules.add("df");
//		C_Rules.add("Ddd");
//		C_Rules.add("dc");
		C_Rules.add("c");

		Collection<String> D_Rules =  new HashSet<String>();
//		D_Rules.add("sdsa");
		
		Collection<String> E_Rules =  new HashSet<String>();
//		E_Rules.add("aE");
//		E_Rules.add("Eb");
		
		rewriteRules.get("S").addAll(S_Rules);
		rewriteRules.get("A").addAll(A_Rules);
		rewriteRules.get("B").addAll(B_Rules);
		rewriteRules.get("C").addAll(C_Rules);
		rewriteRules.get("D").addAll(D_Rules);
		rewriteRules.get("E").addAll(E_Rules);
		
		ContextFreeGrammar cfg = new ContextFreeGrammar(variableSet, terminalsSet, rewriteRules, "S");
		System.out.println(cfg);
		//cfg.simplifyGrammar();
		cfg.covertToChomanskyNormalForm();
		System.out.println(cfg);
		
		cfg.findParseOfWord("abcab");
		System.out.println(cfg.generateAllWordWithLength(5));
	}

}

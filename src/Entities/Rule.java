package Entities;

public class Rule {
	private String startNonTerminal;
	private String result;
	private boolean isTerminal;
	
	public Rule (String startNonTerminal, String result)
	{
		this.startNonTerminal = startNonTerminal;
		this.result = result;
		isTerminal = false;
	}
	
	public Rule (String result)
	{
		this.result = result;
		isTerminal = true;
	}
	
	public boolean isTerminal()
	{
		return isTerminal;
	}
	
	public String getRuleResult()
	{
		return result;
	}
	
	public String getStartNonTerminal()
	{
		return startNonTerminal;
	}
	
	public String toString()
	{
		if (isTerminal == true)
		{
			return result;
		}
		return ""+startNonTerminal+" --->"+result;
	}
}

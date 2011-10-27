package ptrl.util;

import java.util.ArrayList;

public class CityNameGenerator
{
	public CityNameGenerator()
	{
		suffixes=new ArrayList<String>();
		suffixes.add("sville");
		suffixes.add("wood");
		suffixes.add("town");
		suffixes.add("grad");
		suffixes.add("stone");
		suffixes.add("field");
		suffixes.add("-o-rama");
		suffixes.add("bourgh");
		suffixes.add("s");
		
		prefixes=new ArrayList<String>();
		prefixes.add("New");
		prefixes.add("Old");
		prefixes.add("North");
		prefixes.add("South");
		prefixes.add("East");
		prefixes.add("West");
		prefixes.add("Shady");
		
		nouns=new ArrayList<String>();
		nouns.add("Spring");
		nouns.add("Junk");
		nouns.add("Bang");
		nouns.add("York");
		nouns.add("Park");
		nouns.add("Dork");
		nouns.add("Sand");
		
		unsuffixedNouns=new ArrayList<String>();
		unsuffixedNouns.add("York");
		unsuffixedNouns.add("Park");
		unsuffixedNouns.add("Orleans");
		unsuffixedNouns.add("Paris");
		unsuffixedNouns.add("Moscow");
		unsuffixedNouns.add("Berlin");
		unsuffixedNouns.add("London");

	}
	
	public String getName()
	{
		String result=""; 
		if (Math.random()>0.3) //use prefix
		{
			int i=(int)Math.floor(Math.random()*(prefixes.size()));
			if (i>=prefixes.size())
				i=prefixes.size()-1;
			result+=prefixes.get(i)+" ";
		}
		if (Math.random()>0.3) //use suffix
		{
			int i=(int)Math.floor(Math.random()*(nouns.size()));
			if (i>=nouns.size())
				i=nouns.size()-1;
			result+=nouns.get(i);
			i=(int)Math.floor(Math.random()*(suffixes.size()));
			if (i>=suffixes.size())
				i=suffixes.size();
			result+=suffixes.get(i);
		}
		else //don't use suffix
		{
			int i=(int)Math.floor(Math.random()*(unsuffixedNouns.size()));
			if (i>=unsuffixedNouns.size())
				i=unsuffixedNouns.size()-1;
			result+=unsuffixedNouns.get(i);
		}
		System.out.println(result);
		return result;
	}

	private ArrayList<String> nouns;
	private ArrayList<String> unsuffixedNouns;
//	private ArrayList<String> adjectives;
	private ArrayList<String> suffixes;
	private ArrayList<String> prefixes;
	
	private ArrayList<String> usedNames;
}

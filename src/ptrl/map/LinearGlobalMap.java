package ptrl.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import ptrl.Game;

public class LinearGlobalMap
{
	private List<MapDescriptor> myLevels;

	public Map getMapByNumber(int number)
	{
		if (myLevels==null)
		{
			throw new IllegalStateException("Levels list is null");
		}
		return myLevels.get(number).getMap();
	}

	
}

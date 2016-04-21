//Mark Penny 111477906
package cmsc433.p2;

import java.util.ArrayList;
import java.util.List;

public class SeatingArea {
	private static List<Table> Tables;

	public static void Initialize(){
		Tables = new ArrayList<Table>();		
	}
	
	public static void addTable(){
		Tables.add(new Table());
	}
	
	public static int size(){
		return Tables.size();
	}

	public static Table get(int itt){
		return Tables.get(itt);
	}
}


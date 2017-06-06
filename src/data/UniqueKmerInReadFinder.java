package data;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import data.Read.Valid30merMethod;
import entry.Constants;

public class UniqueKmerInReadFinder implements Valid30merMethod {

  private boolean toAddToList;
  private ConcurrentHashMap<Long, ArrayList<Read>> u30mers;

  public UniqueKmerInReadFinder(boolean toAddTou30mers,
  		ConcurrentHashMap<Long, ArrayList<Read>> existing30mers) {
  	toAddToList = toAddTou30mers;
  	u30mers = existing30mers;
  }

	public void RunFunction(int pos, long hash, Read thisRead) {
     //TODO: what about rc. Consider later.
     ArrayList<Read> nodesToCompare = u30mers.get(hash);
     if (nodesToCompare != null) {
     	thisRead.addIdentifier(hash & Constants.THIRTY_MASK);
     	if (toAddToList) {
     		if (!nodesToCompare.contains(thisRead)) {
     			nodesToCompare.add(thisRead);
     		}
     	}
     }
	}
}

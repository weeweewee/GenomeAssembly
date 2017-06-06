package data;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import data.Contig.Valid30merContigMethod;

public class UniqueKmerInContigFinder implements Valid30merContigMethod {
	
	private ConcurrentHashMap<Long, ArrayList<Contig>> u30mers;
  private boolean toAddToList;
	
	public UniqueKmerInContigFinder(boolean toAddTou30mers,
			ConcurrentHashMap<Long, ArrayList<Contig>> f30mers) {
		u30mers = f30mers;
		toAddToList = toAddTou30mers;
	}

	public void RunFunction(int pos, long hash, Contig thisContig) {
		ArrayList<Contig> list = u30mers.get(hash);
		if (list == null) {
			return;
		}
    if (toAddToList) {
    	list.add(thisContig);
    }
		thisContig.addUniqueKmer(hash);
	}
}

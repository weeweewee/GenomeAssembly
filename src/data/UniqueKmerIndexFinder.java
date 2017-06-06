package data;

import java.util.ArrayList;

import data.Contig.Valid30merContigMethod;
import data.Read.Valid30merMethod;

public class UniqueKmerIndexFinder implements Valid30merMethod,
		Valid30merContigMethod {

  	private ArrayList<Integer> indexes;
  	private long hashed;

  	public UniqueKmerIndexFinder(ArrayList<Integer> ind, long hashToCheck) {
  		indexes = ind;
  		hashed = hashToCheck;
  	}

		public void RunFunction(int pos, long hash, Read thisRead) {
      //TODO: what about rc. Consider later.
			if (hash != hashed) {
				return;
			}
			indexes.add(pos);
		}

		public void RunFunction(int pos, long hash, Contig thisContig) {
			if (hash != hashed) {
				return;
			}
			indexes.add(pos);
		}

}

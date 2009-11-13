package tgdh.tree;

/**
 * This class implements the coordinate of the binary tree that used for TGDH in specification  
 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public final class Coordinate {
	private int level;
	private int ordinal;
	
	/**
	 * Constructor with no paramter. It setzts level and ordinal to an invalid value -1.
	 */
	public Coordinate(){
		level = -1;
		ordinal = -1;
	}
	
	/**
	 * @param level level, a non-negativ integer
	 * @param ordinal who satisfies 0 &le; ordinal &le; 2<sup>level</sup>  - 1
	 */
	public Coordinate(int level, int ordinal){
		if(level >= 0 && ordinal >= 0 && ordinal < (1 << level)){
			this.level = level;
			this.ordinal = ordinal;
		}
		else{
			this.level = -1;
			this.ordinal = -1;
		}
			
	}
	public int getLevel(){
		return this.level;
	}
	
	public int getOrdinal(){
		return this.ordinal;
	}
	
	/**
	 * @return &lt; level, ordinal &gt;
	 */
	public String toString(){
		return "<" + this.level + "," + this.ordinal +">";
	}
	
}

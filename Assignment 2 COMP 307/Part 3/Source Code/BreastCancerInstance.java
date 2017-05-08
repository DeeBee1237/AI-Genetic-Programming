package Part3;

import java.util.ArrayList;

/**
 * This class will represent a cancer patient with each of the 9 features (clump thickness etc ..)
 *
 * @author berceadrag
 *
 */
public class BreastCancerInstance {

	private ArrayList<Integer>  features = new ArrayList<Integer> ();

	private int type;

	/**
	 * The cancer instance will conatin a list of all the 9 features, as well as a class (2 or 4)
	 * @param features
	 * @param type
	 */
	public BreastCancerInstance(ArrayList<Integer> features, int type) {
		this.features = features;
		this.type = type;
	}

	/**
	 * Obtain a feature of this instance at a certain index. For Feature Fi use get(i-1)
	 * @param index
	 * @return
	 */
	public int getFeatureAt (int index) {
		return this.features.get(index);
	}

	/**
	 * Obtain the class value for this instance (either a 2 or a 4)
	 * @return
	 */
	public int getType() {
		return type;
	}



}

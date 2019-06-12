
package prc.navprot.utils;
import java.io.Serializable;

/**
 * A class representing the abstract entity "n dimensional vector". 
 * Data format is double.
 * @author Ziv Yaniv
 */
public class AlgebraicVector extends Tuple implements Serializable {

    /**
     * Create a vector of the given size, with all entries initialized to zero.
     * @param n The dimensionality of the point.
     */
    public AlgebraicVector(int n) {
	super(n);
    }

    /**
     * Create a vector of the given size, with all entries initialized to the
     * given value.
     * @param n The dimensionality of the vector.
     * @param val The value all point entries.
     */
    public AlgebraicVector(int n, double val) {
	super(n,val);
    }

    /**
     * Create a vector according to the given double array.
     * @param data The vector will contain entries as specified by the given
     *             array.
     */
    public AlgebraicVector(double[] data) {
	super(data);
    }

    /**
     * Copy constructor, create a vector according to the given vector.
     * @param v Create a copy of this vector.
     */
    public AlgebraicVector(AlgebraicVector v) {
	super(v);
    }

    /**
     * Create a vector according to the given tuple.
     * @param t Create the vector according to this tuple.
     * @see Tuple
     */
    public AlgebraicVector(Tuple t) {
	super(t);
    }

    /**
     * Compute the angle between this vector and the given vector.
     * The result is in the range [0,pi].
     * @param v Compute the angle to this vector.
     * @return Returns the angle between the vectors, in the range [0,pi].
     */ 
    public double angle(AlgebraicVector v) {
	if(data.length != v.data.length)
	    throw new IllegalArgumentException("Vectors must have same " +
					       "dimenssion.");
	return Math.acos(this.dot(v)/(this.normL2()*v.normL2()));
    }

    /**
     * Computes the dot product between this vector and the given vector.
     * @param v Compute the dot product with this vector.
     * @return Returns the dot product.
     */
    public double dot(AlgebraicVector v) {
	if(data.length != v.data.length)
	    throw new IllegalArgumentException("Vectors must have same " +
					       "dimenssion.");
	double sum = 0.0;
	for(int i=0; i<data.length; i++)
	    sum+= data[i]*v.data[i];
	return sum;
    }

    /**
     * Compute the squared L2 norm of this vector.
     * @return Returns the squared L2 norm of the vector.
     */ 
    public double normL2Squared() {
	double sum = 0.0;
	for(int i=0; i<data.length; i++)
	    sum+= data[i]*data[i];
	return sum;
    }

    /**
     * Compute the L2 norm of this vector.
     * @return Returns the L2 norm of the vector.
     */ 
    public double normL2() {
	double sum = 0.0;
	for(int i=0; i<data.length; i++)
	    sum+= data[i]*data[i];
	return Math.sqrt(sum);
    }

    /**
     * Compute the L1 norm of this vector.
     * @return Returns the L1 norm of the vector.
     */ 
    public double normL1() {
	double sum = 0.0;
	for(int i=0; i<data.length; i++)
	    sum+= Math.abs(data[i]);
	return sum;
    }

    /**
     * Compute the Linfinity norm of this vector.
     * @return Returns the Linfinity norm of the vector.
     */     
    public double normLinf() {
	double maxAbs = Math.abs(data[0]);
	for(int i=1; i<data.length; i++) {
	    double abs = Math.abs(data[i]);
	    if(abs > maxAbs)
		maxAbs = abs;
	}
	return maxAbs;
    }

    /**
     * Normalize this vector, make this vector a unit vector.
     */
    public void normalize() {
	int i;
	double sum = 0.0;

	for(i=0; i<data.length; i++)
	    sum+= data[i]*data[i];
	sum = Math.sqrt(sum);
	for(i=0; i<data.length; i++)
	    data[i]/=sum;
    }

    /**
     * Normalize the given vector and place the result in this vector.
     * @param v Set the vector to the normalized form of the given vector, v.
     */
    public void normalize(AlgebraicVector v) {
	int i;
	double sum = 0.0;

	if(data.length != v.data.length)
	    throw new IllegalArgumentException("Vectors must have same " +
					       "dimenssion.");
	for(i=0; i<data.length; i++)
	    sum+= v.data[i]*v.data[i];
	sum = Math.sqrt(sum);
	for(i=0; i<data.length; i++)
	    data[i]=v.data[i]/sum;
    }
}
 


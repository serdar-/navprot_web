/**
 * This class is used iterate eular iteration for minimizing artificial potential function
 * for linked type of simulation.
 * The direction of step is taken as the negative gradient of APF.
 * @author Baris Ulutas - Bogazici University, Electrical & Electronics Engineering Dept.
 * @version 1.1 - 05 March 2008
 */

package prc.navprot;

import java.math.*;
import java.util.Vector;
import prc.navprot.utils.AlgebraicVector;
import prc.navprot.utils.Point3D;
import prc.navprot.utils.Robot3D;
import prc.navprot.utils.RobotList;
import prc.navprot.utils.TestForInverseSqrt;
public class NewOdeSolverForTorsion {

    /** Creates a new instance of NewOdeSolverForLinked */
    public NewOdeSolverForTorsion() {
    }

    /**
     *This method removes all initialized terms.
     */
    public static void removeAll(){/*removed*/}

    public static boolean odeSolverCombo( RobotList f, int k, double linkLength, boolean isNoise ){/*removed*/}

    public static BigDecimal log10(BigDecimal b, int dp){/*removed*/}

    public static void calculateAdaptiveStepSize(int size){/*removed*/}

    public static void setAdaptiveStepSize(int size){/*removed*/}

    public static double getDistance(double []data1, double []data2) {/*removed*/}

    public static double[] normalize( BigDecimal[] vector ){/*removed*/}

    private static double[] normalizeSequential(BigDecimal[] vector) {/*removed*/}

    public static boolean checkForCollision( RobotList f){/*removed*/}

    public static void setXYZCoordinates( RobotList f ){/*removed*/}

    public static void setGoalXYZCoordinates( RobotList f ){/*removed*/}

    public static BigDecimal calculateU( RobotList robotList , int k ){/*removed*/}

    public static BigDecimal calculateUCombo( RobotList robotList, int k , double linkLength){/*removed*/}

    public static double[][] matrixProduct( double[][] m1, double[][] m2 ){/*removed*/}
}



/*
 * TestForInverseSqrt.java
 *
 * Created on 05 �ubat 2007 Pazartesi, 19:27
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package prc.navprot.utils;

/**
 *
 * @author Bar��
 */
import java.math.*;


public class TestForInverseSqrt {
    public static int DEFAULT_PRECISION = 100;
    public static MathContext mc = new MathContext(DEFAULT_PRECISION);
    private static BigDecimal ONE = new BigDecimal("1");
    
    /** Creates a new instance of TestForInverseSqrt */
    public TestForInverseSqrt() {
    }
    
    
    
    private static BigDecimal getInitialApproximation(BigDecimal n) {
        BigInteger integerPart = n.toBigInteger();
        
        int length = integerPart.toString().length();
        if ((length % 2) == 0) {
            length--;
        }
        length /= 2;
        BigDecimal guess = ONE.movePointRight(length);
        guess = new BigDecimal(1.0, mc).divide(guess, mc);
        return guess;
    }
    
    public static BigDecimal inverseSqrt( BigDecimal test ){
        BigInteger dummy = test.unscaledValue();
        BigDecimal unscaledValue = new BigDecimal(dummy.toString(), mc);
        int scale = test.scale();
        if( Math.abs(scale) % 2 != 0 ){
            scale++;
            unscaledValue = unscaledValue.scaleByPowerOfTen(1);
        }
        //Cok guzel initial guess icin unutma guess is for 1/sqrt(x)
        BigDecimal dummyForGuess = new BigDecimal( unscaledValue.toString(), mc );
        //System.out.println(unscaledValue.toString());
        unscaledValue = new BigDecimal(1.0, mc).divide(unscaledValue, mc);
        //System.out.println("Aha="+unscaledValue.toString());
        BigDecimal result, dummy1, dummy2, dummy3;
        result = getInitialApproximation(dummyForGuess);
        result = new BigDecimal(1.0, mc).divide(result, mc);
        //System.out.println("Aha=" + result.toString());
        for( int i = 0; i < 20; i++ ){
            dummy1 = unscaledValue.multiply(result.multiply(result, mc), mc);
            dummy2 = new BigDecimal(3.0, mc).subtract(dummy1);
            dummy3 = result.multiply(dummy2, mc);
            result = dummy3.divide(new BigDecimal(2.0, mc), mc);
        }
        result = new BigDecimal(1.0, mc).divide(result, mc);
        result = result.scaleByPowerOfTen( scale/2 );
        
        BigDecimal errorCheck = new BigDecimal(1.0, mc).divide(result, mc);
        errorCheck = errorCheck.multiply(errorCheck, mc);
        errorCheck = errorCheck.subtract(test, mc);
//        System.out.println("Result = " + result.toString() );
//        System.out.println("Error = " + errorCheck.toString() );
        return result;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String s = "";
        for( int i = 1; i <= 100 ; i++ ){
            s += i;
        }
        System.out.println(s);
        BigDecimal test = new BigDecimal(s,mc);
        BigDecimal error = new BigDecimal(s,mc);
        test = new BigDecimal(1.0, mc).divide(test, mc);
        test = test.multiply(test, mc);
        System.out.println(test.toString());
        BigDecimal result = inverseSqrt(test);
        System.out.println(result.toString());
        error = error.subtract(result, mc);
        System.out.println("Error = " + error.toString() );
    }
    
}

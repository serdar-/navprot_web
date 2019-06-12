package prc.navprot.utils;

/**
 * Created by HP on 27.10.2014.
 *
 * rev. Serdar Özsezen
 */
/*
 * Pdb2TorsionAngleWInitialization.java
 *
 * Created on 19 Eylul 2011 Salı, 14:01
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 *
 * @author B. Ulutas & H.I. Bozma
 * Date Written: 30.7.2008
 */


 import org.biojava.bio.structure.*;
 import org.biojava.bio.structure.io.PDBFileParser;
 import java.io.*;

public class PDB2TorsionAngle {

    private static int ALPHA_CARBON_NO;
    private static Structure pdb1;
    private static String chain1;
    private static Structure pdb2;
    private static String chain2;
    private static File targetDirectory;
    private static double INPUTS[][];
    private static double VECTORS[][];
    private static double THETAS[];
    private static double PHIS[];

    private static double INITVECTORS[][];
    private static double INITTHETAS[];
    private static double INITPHIS[];

    private static double linkLength;

    /** Creates a new instance of PDBReader */
    public PDB2TorsionAngle() {
    }

    /**
     * Sets initial PDB structure
     * @param pdb
     * @throws IOException
     */
    public void setFirstStructure(File pdb) throws IOException{

        PDBFileParser parser = new PDBFileParser();
        pdb1 = parser.parsePDBFile(new BufferedReader(new FileReader(pdb)));


    }

    /**
     * Sets final PDB structure
     * @param pdb
     * @throws IOException
     */
    public void setSecondStructure(File pdb) throws IOException{
        PDBFileParser parser = new PDBFileParser();
        pdb2 = parser.parsePDBFile(new BufferedReader(new FileReader(pdb)));
    }

    /**
     * Sets chain name of initial conformation
     * @param chain
     */
    public void setFirstStructureChain(String chain){
        chain1 = chain;
    }

    public void setSecondStructureChain(String chain){
        chain2 = chain;
    }

    /**
     * Sets the target directory for torsion file
     * @param targetDir
     */
    public void setTargetDirectory(File targetDir){
        targetDirectory = targetDir;
    }


    private static void init(){

        try {
            Atom[] ca_array = StructureTools.getAtomCAArray(pdb1.getChainByPDB(chain1));
            ALPHA_CARBON_NO = ca_array.length;
            INPUTS = new double[ALPHA_CARBON_NO][3];
            VECTORS = new double[ALPHA_CARBON_NO][3];
            THETAS = new double[ALPHA_CARBON_NO];
            PHIS = new double[ALPHA_CARBON_NO];
            INITVECTORS = new double[ALPHA_CARBON_NO][3];
            INITTHETAS = new double[ALPHA_CARBON_NO];
            INITPHIS = new double[ALPHA_CARBON_NO];
        } catch (StructureException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method reads the coordinates by using Structure objects that are set with PDB file inputs.
     * @param pdb
     */
    private static void read(Structure pdb, String chain){
        try {
            Chain pdbChain = pdb.getChainByPDB(chain);
            Atom[] ca_array = StructureTools.getAtomCAArray(pdbChain);
            for (int i = 0; i < ca_array.length; i++) {
                INPUTS[i][0] = ca_array[i].getX();
                INPUTS[i][1] = ca_array[i].getY();
                INPUTS[i][2] = ca_array[i].getZ();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private static void findLinkLength(){
        int p = ALPHA_CARBON_NO;
        double total = 0.0, dist;
        for( int i = 0; i < p-1; i++ ){

            dist = Math.pow(INPUTS[i][0]-INPUTS[i+1][0],2) + Math.pow(INPUTS[i][1]-INPUTS[i+1][1],2)+
                    Math.pow(INPUTS[i][2]-INPUTS[i+1][2],2);
            total += Math.sqrt(dist);
        }
        linkLength = total/(p-1);
    }

    private static void findAngles(double[] thetas, double[] phis){
        for( int i = 1; i < ALPHA_CARBON_NO; i++ ){
            VECTORS[i][0] = INPUTS[i][0] - INPUTS[i-1][0];
            VECTORS[i][1] = INPUTS[i][1] - INPUTS[i-1][1];
            VECTORS[i][2] = INPUTS[i][2] - INPUTS[i-1][2];
        }
        double dot, length1, length2;
        for( int i = 1; i < ALPHA_CARBON_NO - 1; i++ ){
            dot = VECTORS[i][0]*VECTORS[i+1][0] + VECTORS[i][1]*VECTORS[i+1][1]
                    + VECTORS[i][2]*VECTORS[i+1][2];

            length1 = Math.pow( VECTORS[i][0], 2) + Math.pow( VECTORS[i][1], 2) +
                    Math.pow( VECTORS[i][2], 2);
            length1 = Math.sqrt(length1);

            length2 = Math.pow( VECTORS[i+1][0], 2) + Math.pow( VECTORS[i+1][1], 2) +
                    Math.pow( VECTORS[i+1][2], 2);
            length2 = Math.sqrt(length2);
            dot = dot / ((length1)*(length2));
            System.out.println("Dot = " + dot);
            thetas[i] = Math.PI - Math.acos(dot);
            if (thetas[i]> Math.PI) thetas[i]=-(2.0*Math.PI-thetas[i]);
            else if (thetas[i]< -Math.PI) thetas[i]= (2*Math.PI+thetas[i]);

        }

        double[] cross1 = new double[3];
        double[] cross2 = new double[3];
        double[] crossCross = new double[3];
        double dotOfCross;
        for( int i = 2; i < ALPHA_CARBON_NO - 1; i++ ){
            cross1[0] = VECTORS[i-1][1]*VECTORS[i][2] - VECTORS[i][1]*VECTORS[i-1][2];
            cross1[1] = - (VECTORS[i-1][0]*VECTORS[i][2] - VECTORS[i][0]*VECTORS[i-1][2]);
            cross1[2] = VECTORS[i-1][0]*VECTORS[i][1] - VECTORS[i][0]*VECTORS[i-1][1];

            cross2[0] = VECTORS[i][1]*VECTORS[i+1][2] - VECTORS[i+1][1]*VECTORS[i][2];
            cross2[1] = - (VECTORS[i][0]*VECTORS[i+1][2] - VECTORS[i+1][0]*VECTORS[i][2]);
            cross2[2] = VECTORS[i][0]*VECTORS[i+1][1] - VECTORS[i+1][0]*VECTORS[i][1];

            crossCross[0] = cross1[1]*cross2[2] - cross2[1]*cross1[2];
            crossCross[1] = -(cross1[0]*cross2[2] - cross2[0]*cross1[2]);
            crossCross[2] = cross1[0]*cross2[1] - cross2[0]*cross1[1];

            dotOfCross = crossCross[0]*VECTORS[i][0] + crossCross[1]*VECTORS[i][1] +
                    crossCross[2]*VECTORS[i][2];
            dot = cross1[0]*cross2[0] + cross1[1]*cross2[1]
                    + cross1[2]*cross2[2];

            length1 = Math.pow( cross1[0], 2) + Math.pow( cross1[1], 2) +
                    Math.pow( cross1[2], 2);
            length1 = Math.sqrt(length1);

            length2 = Math.pow( cross2[0], 2) + Math.pow( cross2[1], 2) +
                    Math.pow( cross2[2], 2);
            length2 = Math.sqrt(length2);
            dot = dot / ((length1)*(length2));

            if( dotOfCross > 0 ){
                phis[i] = Math.acos(dot);
            }else{
                phis[i] = 2*Math.PI - Math.acos(dot);
            }
            if (phis[i]> Math.PI)  phis[i]= -(2.0*Math.PI- phis[i]);
            else if (phis[i]< -Math.PI) phis[i]= 2.0*Math.PI+phis[i];

        }
    }

    private static void adjustAngles(double[] thetas, double[] phis, double[] gthetas, double[] gphis)
    {
        for( int i = 1; i < ALPHA_CARBON_NO - 1; i++ ){

            if (  (thetas[i]-gthetas[i] > Math.PI) )
            {
                if  ( gthetas[i]>0) gthetas[i]= -(2*Math.PI- gthetas[i]);
                else if (gthetas[i]<0) gthetas[i]= 2*Math.PI+ gthetas[i];
            }
            else if   (thetas[i]- gthetas[i] < -Math.PI)
            {
                if  ( gthetas[i]>0) gthetas[i]= -(2*Math.PI- gthetas[i]);
                else if (gthetas[i]<0) gthetas[i]= 2*Math.PI+ gthetas[i];

            }
            if (  (phis[i]-gphis[i] > Math.PI) )
            {
                if  ( gphis[i]>0) gphis[i]= -(2*Math.PI- gphis[i]);
                else if (gphis[i]<0) gphis[i]= 2*Math.PI+ gphis[i];
            }
            else if   (phis[i]- gphis[i] < -Math.PI)
            {
                if  ( gphis[i]>0) gphis[i]= -(2*Math.PI- gphis[i]);
                else if (gphis[i]<0) gphis[i]= 2*Math.PI+ gphis[i];

            }


        } // end of for

    }

    public static void write(){
/*        String saveFile = JOptionPane.showInputDialog("Enter the output data file name");*/
        try {

            BufferedWriter out = new BufferedWriter(new FileWriter(new File(targetDirectory,"torsion.txt")));
            out.flush();
            double radius;

            for( int i = 0; i < ALPHA_CARBON_NO; i++ ){

                if( i == 0 ){
                    out.write( 0.0 + " " + 0.0 + " " + 0.0 + " "  );
                    out.write( 0.0 + " " + 0.0 + " " + 0.0 );
                    out.newLine();
                }else if( i == 1 ){
                    out.write( INITTHETAS[i] + " "  + INITPHIS[i] + " "  );
                    out.write( THETAS[i]+" "+PHIS[i] );
                    out.newLine();
                }else if(  i == ALPHA_CARBON_NO-1  ){
                    out.write( INITTHETAS[i] + " "  + INITPHIS[i] + " "  );
                    out.write( THETAS[i]+" "+PHIS[i] );
                    out.newLine();

                }else{
                    out.write( INITTHETAS[i] + " "  + INITPHIS[i] + " "  );
                    out.write( THETAS[i]+" "+PHIS[i] );
                    out.newLine();
                }
            }



            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDouble(String s){
        try {Double.parseDouble(s);} catch (NumberFormatException e) {return false;}
        return true;
    }

    public static void convert() {
        // TODO code application logic here
        init();
        // Read the goal data
//        read("pdb", "Choose the goal pdb file");
        read(pdb2,chain2); // pdb2 -> final conformation PDB
        findAngles(THETAS, PHIS);
//        findAnglesAlt(THETAS,PHIS);
        findLinkLength();
        // Read the initial state data
//        read("pdb","Choose the initial state pdb file");
        read(pdb1,chain1); // pdb1 -> initial conformation PDB
        findAngles(INITTHETAS, INITPHIS);
//        findAnglesAlt(INITTHETAS, INITPHIS);
        adjustAngles(THETAS, PHIS, INITTHETAS, INITPHIS);
        findLinkLength();
        write();
//        System.exit(0);
    }

}


package controllers;

import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.StructureException;
import org.biojava.bio.structure.StructureTools;
import org.biojava.bio.structure.io.PDBFileParser;
import prc.navprot.utils.PDB2TorsionAngle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by Serdar on 10/21/14.
 */
public class CheckFiles {

    /**
     * This method checks if the residue numbers are the same.
     * @param pdb1
     * @param chain1
     * @param pdb2
     * @param chain2
     * @return
     * @throws IOException
     * @throws StructureException
     */
    public static boolean residueNumbersEqual( File pdb1, String chain1,  File pdb2, String chain2 ) {

        try{
            PDBFileParser parser = new PDBFileParser();
            Structure first = parser.parsePDBFile(new BufferedReader(new FileReader(pdb1)));
            Structure second = parser.parsePDBFile(new BufferedReader(new FileReader(pdb2)));
            Chain firstChain = first.getChainByPDB(chain1);
            Chain secondChain = second.getChainByPDB(chain2);
            firstChain.getSeqResLength();
            if (StructureTools.getAtomCAArray(firstChain).length == StructureTools.getAtomCAArray(secondChain).length){
                return true;
            } else {
                return false;
            }
        } catch (IOException|StructureException e){
            e.printStackTrace(); // If file was not able to be read or parsed return false
            return false;
        }
    }

    /**
     * Converts PDB files to torsion file and saves it to job directory
     * @param pdb1
     * @param chain1
     * @param pdb2
     * @param chain2
     * @param targetDir
     */
    public static void convertPDB2Torsion( File pdb1, String chain1, File pdb2, String chain2, File targetDir ){

        try {
            PDB2TorsionAngle converter = new PDB2TorsionAngle();
            converter.setFirstStructure(pdb1);
            converter.setFirstStructureChain(chain1);
            converter.setSecondStructure(pdb2);
            converter.setSecondStructureChain(chain2);
            converter.setTargetDirectory(targetDir);
            converter.convert();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

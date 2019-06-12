package prc.navprot.utils;

import org.apache.commons.io.FileUtils;
import org.biojava.bio.structure.*;
import org.biojava.bio.structure.align.StructureAlignment;
import org.biojava.bio.structure.align.StructureAlignmentFactory;
import org.biojava.bio.structure.align.fatcat.FatCatRigid;
import org.biojava.bio.structure.align.model.AFPChain;
import org.biojava.bio.structure.align.util.AFPAlignmentDisplay;
import org.biojava.bio.structure.io.PDBFileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by HP on 28.10.2014.
 */
public class StructuralAlignment {

    public static double align( File pdb1, String chain1, File pdb2, String chain2, String uid ) throws IOException, StructureException{

        PDBFileParser parser = new PDBFileParser();
        Structure structure1 = parser.parsePDBFile(new BufferedReader(new FileReader(pdb1)));
        Structure structure2 = parser.parsePDBFile(new BufferedReader(new FileReader(pdb2)));
        Atom[] ca1 = StructureTools.getAtomCAArray(structure1.getChainByPDB(chain1));
        Atom[] ca2 = StructureTools.getAtomCAArray(structure2.getChainByPDB(chain2));
        StructureAlignment algorithm  = StructureAlignmentFactory.getAlgorithm(FatCatRigid.algorithmName);
        AFPChain afp = algorithm.align(ca1, ca2);
        Structure alg1 = new StructureImpl();
        Structure alg2 = new StructureImpl();
        try {
            Structure aligned = AFPAlignmentDisplay.createArtificalStructure(afp, ca1, ca2);
            alg1.addChain(aligned.getModel(0).get(0));
            alg2.addChain(aligned.getModel(1).get(0));
            FileUtils.writeStringToFile(new File("userdata", uid + "/" + pdb1.getName() + "_aligned"),alg1.toPDB());
            FileUtils.writeStringToFile(new File("userdata", uid + "/" + pdb2.getName() + "_aligned"),alg2.toPDB());
        } catch (Exception e){
            e.printStackTrace();
        }
        return afp.getChainRmsd();

    }
}

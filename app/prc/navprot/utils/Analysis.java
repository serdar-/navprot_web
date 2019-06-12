package prc.navprot.utils;

import models.Task;
import org.apache.commons.io.FileUtils;
import org.biojava.bio.structure.*;
import org.biojava.bio.structure.align.StructureAlignment;
import org.biojava.bio.structure.align.StructureAlignmentFactory;
import org.biojava.bio.structure.align.fatcat.FatCatRigid;
import org.biojava.bio.structure.align.model.AFPChain;
import org.biojava.bio.structure.io.PDBFileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Serdar on 11/5/14.
 */
public class Analysis {

    /**
     * Creates chart data array of alignment of snapshots, for using in Google Charts API
     * @param task
     * @return
     */
    public static void createChartData(Task task){

        PDBFileParser parser = new PDBFileParser();
        Locale loc = new Locale("en","US");
        loc.setDefault(loc);
        StringBuilder builder = new StringBuilder();
        builder.append("[['Steps','Distance to final structure (\\u212b)','Distance to initial structure (\\u212b)'],");
        try{
            Chain initPDB = parser.parsePDBFile(new BufferedReader(new FileReader(new File("userdata",task.id + "/" + task.initialPDB))))
                .getChainByPDB(task.initialChain);
            Chain finalPDB = parser.parsePDBFile(new BufferedReader(new FileReader(new File("userdata",task.id + "/" + task.finalPDB))))
                .getChainByPDB(task.finalChain);
            Structure resultPDB = parser.parsePDBFile(new BufferedReader(new FileReader(new File("userdata",task.id + "/result.pdb"))));
            Atom[] caInit = StructureTools.getAtomCAArray(initPDB);
            Atom[] caFinal = StructureTools.getAtomCAArray(finalPDB);
            StructureAlignment algorithm  = StructureAlignmentFactory.getAlgorithm(FatCatRigid.algorithmName);
            for(int i = 0; i < resultPDB.nrModels(); i++){
                Atom[] ca = StructureTools.getAtomCAArray(resultPDB.getChain(i,0));
                AFPChain afp1 = algorithm.align(caFinal, ca);
                double rmsd1 = afp1.getChainRmsd();
                AFPChain afp2 = algorithm.align(caInit, ca);
                double rmsd2 = afp2.getChainRmsd();
                int k = i+1;
                builder.append("['Snapshot " + String.format("%d",k,loc) + "', " + String.format("%.2f",rmsd1,loc) + ", " + String.format("%.2f",rmsd2,loc) +"]");
                if(k != resultPDB.nrModels())
                    builder.append(",");
            }
            builder.append("]");
            FileUtils.writeStringToFile(new File("userdata",task.id + "/chartdata"), builder.toString());

        } catch( IOException|StructureException e ){
            e.printStackTrace();
        }

    }
}

package ImagemltYacc;

import ImagemltYacc.CFG.CFG;
import ImagemltYacc.CFG.Production;
import ImagemltYacc.CFG.Token;
import ImagemltYacc.LR.LR1Automation;
import ImagemltYacc.io.CodeGenerator;
import ImagemltYacc.io.YaccFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        YaccFile yaccFile;
        try {
            yaccFile = new YaccFile(args[0]);

            CFG cfg=new CFG(yaccFile.getStates());
            Vector<Token> tokens=yaccFile.getTokens();

            for(Production p:yaccFile.getProductions()){
                cfg.addProduction(p);
            }
            cfg.setStartState(yaccFile.getStartStates().get(0));
            cfg.Broaden();
            //cfg.print();
            System.out.println("[+]cfg generation done!");
            LR1Automation automation=new LR1Automation(cfg);
            automation.generateLR1Clousres();
            automation.generateActionGoto();

            System.out.println("[+]action goto table bingo!");
            CodeGenerator generator=new CodeGenerator(yaccFile.getNonTerminals(),tokens,automation,yaccFile);
            //System.out.println(generator.genCode());
            BufferedWriter writer=new BufferedWriter(new FileWriter(args[1]));
            writer.write(generator.genCode());
            writer.flush();
            writer.close();
            System.out.println("[+]generate Parser successfully:"+args[1]);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}

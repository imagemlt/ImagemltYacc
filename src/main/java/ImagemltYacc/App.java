package ImagemltYacc;

import ImagemltYacc.CFG.CFG;
import ImagemltYacc.CFG.Production;
import ImagemltYacc.CFG.Token;
import ImagemltYacc.LR.LR1Automation;
import ImagemltYacc.io.YaccFile;

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
            yaccFile = new YaccFile("/tmp/yacc2");

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

            System.out.println("[+]bingo!");
            Vector<Token> inputSample=new Vector<>();
            inputSample.add(tokens.get(5));
            inputSample.add(tokens.get(4));
            inputSample.add(tokens.get(0));
            inputSample.add(tokens.get(9));
            inputSample.add(tokens.get(6));
            automation.ShiftReduce(inputSample);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}

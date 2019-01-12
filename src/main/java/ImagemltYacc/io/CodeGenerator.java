package ImagemltYacc.io;

import ImagemltYacc.CFG.State;
import ImagemltYacc.CFG.Token;
import ImagemltYacc.LR.LR1Automation;

import java.util.Vector;

public class CodeGenerator {
    private Vector<State> states;
    private Vector<Token> tokens;
    private LR1Automation automation;
    private YaccFile yaccFile;
    private static String head="";
    private static String funcParse="";
    private static String constructor="";


    public CodeGenerator(Vector<State> states, Vector<Token> tokens, LR1Automation automation, YaccFile yaccFile) {
        this.states = states;
        this.tokens = tokens;
        this.automation = automation;
        this.yaccFile = yaccFile;
    }

    public String genCode(){
        String ActionGoto=dumpActionGoto();
        String UserCode=yaccFile.getUserCode();
        return head+ActionGoto+funcParse+constructor+UserCode+"\n}\n";
    }

    public String dumpActionGoto(){
        return "";
    }


}

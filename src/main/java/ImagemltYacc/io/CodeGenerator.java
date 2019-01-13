package ImagemltYacc.io;

import ImagemltYacc.CFG.CFG;
import ImagemltYacc.CFG.Production;
import ImagemltYacc.CFG.State;
import ImagemltYacc.CFG.Token;
import ImagemltYacc.LR.*;

import java.util.HashSet;
import java.util.Vector;

public class CodeGenerator {
    private Vector<State> states;
    private Vector<Token> tokens;
    private Vector<SingleProuction> singleProuctions;
    private LR1Automation automation;
    private YaccFile yaccFile;
    private static String head="import javafx.util.Pair;\n" +
            "\n" +
            "import java.io.*;\n" +
            "import java.util.*;\n" +
            "\n" +
            "public class Parser {\n" +
            "    enum ACTION{\n" +
            "        SHIFT,\n" +
            "        REDUCE,\n" +
            "        ACCEPT\n" +
            "    }\n" +
            "    enum NodeType{\n" +
            "        TOKEN,\n" +
            "        STATE\n" +
            "    };\n" +
            "    public class Token{\n" +
            "        private String tokenValue;\n" +
            "        private int TokenType;\n" +
            "\n" +
            "        public Token(String tokenValue, int tokenType) {\n" +
            "            this.tokenValue = tokenValue;\n" +
            "            TokenType = tokenType;\n" +
            "        }\n" +
            "\n" +
            "        public String getTokenValue() {\n" +
            "            return tokenValue;\n" +
            "        }\n" +
            "\n" +
            "        public void setTokenValue(String tokenValue) {\n" +
            "            this.tokenValue = tokenValue;\n" +
            "        }\n" +
            "\n" +
            "        public int getTokenType() {\n" +
            "            return TokenType;\n" +
            "        }\n" +
            "\n" +
            "        public void setTokenType(int tokenType) {\n" +
            "            TokenType = tokenType;\n" +
            "        }\n" +
            "\n" +
            "    }\n" +
            "    public class TreeNode{\n" +
            "        private NodeType type;\n" +
            "        private Integer value;\n" +
            "        private Vector<TreeNode> childs;\n" +
            "        private Token token;\n" +
            "        public TreeNode(NodeType type, Integer value, Vector<TreeNode> childs) {\n" +
            "            this.type = type;\n" +
            "            this.value = value;\n" +
            "            this.childs = childs;\n" +
            "        }\n" +
            "\n" +
            "        public TreeNode(NodeType type,Token token,Vector<TreeNode> childs){\n" +
            "            this.type = type;\n" +
            "            this.token = token;\n" +
            "            this.childs = childs;\n" +
            "        }\n" +
            "\n" +
            "        public Token getToken() {\n" +
            "            return token;\n" +
            "        }\n" +
            "\n" +
            "        public void setToken(Token token) {\n" +
            "            this.token = token;\n" +
            "        }\n" +
            "\n" +
            "        public NodeType getType() {\n" +
            "            return type;\n" +
            "        }\n" +
            "\n" +
            "        public void setType(NodeType type) {\n" +
            "            this.type = type;\n" +
            "        }\n" +
            "\n" +
            "        public Integer getValue() {\n" +
            "            return value;\n" +
            "        }\n" +
            "\n" +
            "        public void setValue(Integer value) {\n" +
            "            this.value = value;\n" +
            "        }\n" +
            "\n" +
            "        public Vector<TreeNode> getChilds() {\n" +
            "            return childs;\n" +
            "        }\n" +
            "\n" +
            "        public void setChilds(Vector<TreeNode> childs) {\n" +
            "            this.childs = childs;\n" +
            "        }\n" +
            "    }\n" +
            "    private TreeNode parsedTree;\n" +
            "    HashMap<String,Integer> tokenMap=new HashMap<>();";
    private static String funcParse="    private void generateTokenMap(){\n" +
            "        for(int i=0;i<Tokens.length;i++){\n" +
            "            tokenMap.put(Tokens[i],i);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    public void shiftReduce(Vector<Token> tokens) throws Exception{\n" +
            "        Stack<Integer> stateStack=new Stack<>();\n" +
            "        Stack<TreeNode> NodeStack=new Stack<>();\n" +
            "        stateStack.push(0);\n" +
            "        int pos=0;\n" +
            "        while(true){\n" +
            "            Integer top=stateStack.pop();\n" +
            "            stateStack.push(top);\n" +
            "            Pair<ACTION,Integer> act=actionTable.get(top).get(tokens.get(pos).getTokenType());\n" +
            "            if(act==null){\n" +
            "                throw new Exception(\"invalid token:\"+Tokens[tokens.get(pos).getTokenType()]);\n" +
            "            }\n" +
            "            if(act.getKey()== ACTION.SHIFT){\n" +
            "                stateStack.push(act.getValue());\n" +
            "                NodeStack.push(new TreeNode(NodeType.TOKEN,tokens.get(pos),new Vector<TreeNode>()));\n" +
            "                pos++;\n" +
            "            }\n" +
            "            else if(act.getKey()== ACTION.REDUCE){\n" +
            "                int pro=act.getValue();\n" +
            "                int size=productionLength[pro];\n" +
            "                Vector<TreeNode> popedNodes=new Vector<>();\n" +
            "                while(size>0){\n" +
            "                    stateStack.pop();\n" +
            "                    popedNodes.add(0,NodeStack.pop());\n" +
            "                    size--;\n" +
            "                }\n" +
            "                top=stateStack.pop();\n" +
            "                stateStack.push(top);\n" +
            "                NodeStack.push(new TreeNode(NodeType.STATE,productionRes[pro],popedNodes));\n" +
            "                stateStack.add(gotoTable[top][productionRes[pro]]);//\n" +
            "               System.out.print(productions[pro]+\"\\t\");\n" +
            "               for(TreeNode node:popedNodes){\n" +
            "                   if(node.type== NodeType.STATE){\n" +
            "                       System.out.print(States[node.value]+\" \");\n" +
            "                   }\n" +
            "                   else{\n" +
            "                       System.out.print(node.getToken().getTokenValue()+\" \");\n" +
            "                   }\n" +
            "               }\n" +
            "               System.out.println();\n" +
            "            }\n" +
            "            else if(act.getKey()== ACTION.ACCEPT){\n" +
            "                System.out.println(\"[+]ACCEPT!\");\n" +
            "                break;\n" +
            "            }\n" +
            "        }\n" +
            "        //if(pos!=tokens.size()-1)throw new Exception(\"syntax error:invalid token \"+Tokens[pos]);\n" +
            "        this.parsedTree=NodeStack.pop();\n" +
            "    }\n" +
            "\n" +
            "    public Vector<Token> readTokens(InputStreamReader reader)throws Exception{\n" +
            "        BufferedReader bufferedReader=new BufferedReader(reader);\n" +
            "        String line;\n" +
            "        Vector<Token> tokens=new Vector<>();\n" +
            "        while(true) {\n" +
            "            line=bufferedReader.readLine();\n" +
            "            if(line==null)break;\n" +
            "            String[] splits=line.split(\",\",2);\n" +
            "            if(splits.length!=2)break;\n" +
            "            if(!tokenMap.containsKey(splits[0]))throw new Exception(\"Invalid token:\"+splits[0]);\n" +
            "            Token t=new Token(splits[1],tokenMap.get(splits[0]));\n" +
            "            tokens.add(t);\n" +
            "        }\n" +
            "        tokens.add(new Token(\"EOF\",tokenMap.get(\"EOF\")));\n" +
            "        return tokens;\n" +
            "    }\n";
    private static String constructor="    public Parser(){\n" +
            "        generateActionTable();\n" +
            "        generateTokenMap();\n" +
            "    }\n";


    public CodeGenerator(Vector<State> states, Vector<Token> tokens,LR1Automation automation, YaccFile yaccFile) {
        this.states = states;
        this.tokens = tokens;
        tokens.add(CFG.EOF);
        this.automation = automation;
        this.yaccFile = yaccFile;
        this.singleProuctions=automation.getProductions();
    }

    public String genCode(){
        String TokensStatesProductions=dumpTokenStatesProductions();
        String ActionGoto=dumpActionGoto();
        String UserCode=yaccFile.getUserCode();
        return head+TokensStatesProductions+ActionGoto+funcParse+constructor+UserCode+"\n}\n";
    }

    public String dumpTokenStatesProductions(){
        String res="String[] Tokens={";
        for(Token t:tokens){
            if(t.getType()== Token.TokenType.EOF)
                res=res+"\"EOF\",";
            else
                res=res+"\""+t.getCh()+"\",";
        }
        res=res+"};\n";
        res=res+"String[] States={";
        for(State state:automation.getCfg().getNoneTerminals()) {
            res = res + "\"" + state.getDescription() + "\",";
        }
        res=res+"};\nint startState="+automation.getCfg().getStartState().getId()+";\n";
        res=res+"int[] productionRes={";
        for(SingleProuction pro:singleProuctions){
            res=res+pro.getResult().getId()+",";
        }
        res=res+"};\n";

        res=res+"int[] productionLength={";
        for(SingleProuction pro:singleProuctions){
            res=res+pro.getRightPart().size()+",";
        }
        res=res+"};\n";
        res=res+"String[] productions={";
        for(SingleProuction pro:singleProuctions){
            res=res+"\""+pro.getResult().getDescription()+"->";
            for(State s:pro.getRightPart()){
                res=res+s.getDescription()+" ";
            }
            res=res+"\",";
        }
        res=res+"};\n";
        return res;
    }

    public String dumpActionGoto(){
        String res="HashMap<Integer,HashMap<Integer,Pair<ACTION,Integer>>> actionTable=new HashMap<>();\npublic void generateActionTable(){\n\tHashMap<Integer,Pair<ACTION,Integer>> tmp;\n";
        //actionTable
        for(int i=0;i<automation.LRStates.size();i++){
            res=res+"\ttmp=new HashMap<>();\n" +
                    "\tactionTable.put("+i+",tmp);\n";
            int j=0;
            for(Token t:tokens){
                if(automation.actionTable.get(i).get(t)==null){
                }
                else{
                    Action action=automation.actionTable.get(i).get(t);
                    if(action.getAction()== Action.Act.SHIFT){
                        res=res+"\ttmp.put("+j+",new Pair<Parser.ACTION,Integer>(Parser.ACTION.SHIFT,"+action.getDestId()+"));\n";
                    }
                    else if(action.getAction()==Action.Act.REDUCE){
                        res=res+"\ttmp.put("+j+",new Pair<Parser.ACTION,Integer>(Parser.ACTION.REDUCE,"+action.getDestId()+"));\n";
                    }
                    else if(action.getAction()==Action.Act.ACCEPT){
                        res=res+"\ttmp.put("+j+",new Pair<Parser.ACTION,Integer>(Parser.ACTION.ACCEPT,0));\n";
                    }
                }
                j++;
            }
        }
        res=res+"};\n";

        res=res+"Integer[][] gotoTable={\n";
        for(int i=0;i<automation.LRStates.size();i++){
            res=res+"{";
            for(int j=0;j<states.size();j++){
                Integer dest=automation.gotoTable.get(i).get(states.get(j));
                if(dest==null){
                    res=res+"null,";
                }
                else{
                    res=res+dest+",";
                }
            }
            res=res+"},\n";
        }
        res=res+"};\n";
        return res;
    }


}

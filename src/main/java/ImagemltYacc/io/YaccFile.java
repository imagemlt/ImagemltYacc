package ImagemltYacc.io;

import ImagemltYacc.CFG.Production;
import ImagemltYacc.CFG.State;
import ImagemltYacc.CFG.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class YaccFile {
    private Vector<State> states;
    private Vector<Token> tokens;
    private Vector<Production> productions;
    private String UserCode;
    private BufferedReader reader;
    private HashMap<String,State> reMap=new HashMap<>();
    private Vector<State> startStates;
    private Vector<State> nonTerminals=new Vector<>();

    public Vector<State> getNonTerminals() {
        return nonTerminals;
    }

    public void setNonTerminals(Vector<State> nonTerminals) {
        this.nonTerminals = nonTerminals;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public Vector<State> getStartStates() {
        return startStates;
    }

    public void setStartStates(Vector<State> startStates) {
        this.startStates = startStates;
    }

    private HashMap<State,Production> productionHashMap=new HashMap<>();
    public YaccFile(String Path)throws Exception {
        reader=new BufferedReader(new FileReader(Path));
        tokens=new Vector<>();
        states=new Vector<>();
        productions=new Vector<>();
        startStates=new Vector<>();
        readTokens();
        readStarts();
        readStates();
        readUserCode();
    }

    private void readTokens() throws Exception{
        while(true){
            String line=reader.readLine();
            if(line==null)throw new Exception("Yacc Format Error");
            else if(line.startsWith("%%"))return;
            else{
                Token t=new Token(tokens.size(),line);
                tokens.add(t);
                State state=new State(states.size(),t);
                state.setDescription(line);
                states.add(state);
                reMap.put(line,state);
            }
        }
    }

    private void readStarts() throws Exception{
        while(true){
            String line=reader.readLine();
            if(line==null)throw new Exception("Yacc Format Error");
            else if(line.startsWith("%%"))return;
            else{
                //Token t=new Token(tokens.size(),line);
                //tokens.add(t);
                State state=new State(startStates.size(), State.STATUS.STRART);
                state.setDescription(line);
                states.add(state);
                startStates.add(state);
                reMap.put(line,state);
                nonTerminals.add(state);
            }
        }
    }

    private void readStates() throws Exception{
        HashMap<State,Vector<String>> map=new HashMap<>();
        while(true){
            String line=reader.readLine();
            if(line==null)throw new Exception("Yacc Format Error");
            else if(line.startsWith("%%"))break;
        }
        int i=startStates.size();
        while(true){
            String line=reader.readLine();
            if(line==null)throw new Exception("Yacc Format Error");
            else if(line.startsWith("%%"))break;
            else {
                String[] splits = line.split("[\t ]*:[\t ]*", 2);
                if (splits.length != 2) continue;
                State state;
                if (!reMap.containsKey(splits[0])) {
                    state = new State(i, State.STATUS.MID);
                    state.setDescription(splits[0]);
                    states.add(state);
                    reMap.put(splits[0], state);
                    nonTerminals.add(state);
                    i++;
                }
                else {
                    state=reMap.get(splits[0]);
                }
                if(map.get(state)==null)
                    map.put(state,new Vector<String>());
                map.get(state).add(splits[1]);
            }
        }
        Iterator it=map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<State,Vector<String>>entry=(Map.Entry<State,Vector<String>>)it.next();
            for(String right:entry.getValue()) {
                String[] splits = right.split("[\t ]");
                Production pro;
                if (productionHashMap.containsKey(entry.getKey())) {
                    pro = productionHashMap.get(entry.getKey());
                } else {
                    pro = new Production();
                    pro.setResult(entry.getKey());
                    Vector<Vector<State>> rightpart = new Vector<>();
                    pro.setRightPart(rightpart);
                    productionHashMap.put(entry.getKey(), pro);
                }
                Vector<State> vect = new Vector<>();
                for (String split : splits) {
                    vect.add(reMap.get(split));
                }
                pro.getRightPart().add(vect);
            }
        }
        for(State s:productionHashMap.keySet()){
            Production pro=productionHashMap.get(s);
            productions.add(pro);
        }
    }

    private void readUserCode() throws Exception{
        this.UserCode="";
        while(true){
            String line=reader.readLine();
            if(line==null) return;
            else{
                this.UserCode+=line+"\n";
            }
        }
    }

    public Vector<State> getStates() {
        return states;
    }

    public void setStates(Vector<State> states) {
        this.states = states;
    }

    public Vector<Token> getTokens() {
        return tokens;
    }

    public void setTokens(Vector<Token> tokens) {
        this.tokens = tokens;
    }

    public Vector<Production> getProductions() {
        return productions;
    }

    public void setProductions(Vector<Production> productions) {
        this.productions = productions;
    }
}

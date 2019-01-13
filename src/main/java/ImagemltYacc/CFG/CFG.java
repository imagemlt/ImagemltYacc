package ImagemltYacc.CFG;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class CFG {
    public CFG(Vector<State> states) {
        this.states = states;
        this.productions=new Vector<>();
        this.transitions=new HashMap<>();
        this.noneTerminals=new Vector<>();
        for(State s:states){
            if(s.getStatus()== State.STATUS.TOKEN){
                tokenMap.put(s.getToken(),s);
            }
            else{
                noneTerminals.add(s);
            }
        }
        tokenMap.put(EOF,EOFState);
    }
    public static Token emptyState=new Token(-1, Token.TokenType.EPSILON);
    public static Token EOF=new Token(-1, Token.TokenType.EOF);
    public static State EOFState=new State(-1, State.STATUS.EOF);
    public HashMap<Token,State> tokenMap=new HashMap<>();
    static Vector<State> empty=new Vector<>();


    private Vector<Production> productions;
    private Vector<State> states;
    private Vector<State> noneTerminals;

    public HashMap<State, Production> getTransitions() {
        return transitions;
    }

    public void setTransitions(HashMap<State, Production> transitions) {
        this.transitions = transitions;
    }

    private HashMap<State,Production> transitions;
    private HashMap<State,Boolean> isEmpty=new HashMap<>();
    private HashMap<State,HashSet<Token>> firstSet=new HashMap<>();
    private HashMap<State,HashSet<Token>> followSet=new HashMap<>();

    public Vector<State> getNoneTerminals() {
        return noneTerminals;
    }

    public void setNoneTerminals(Vector<State> noneTerminals) {
        this.noneTerminals = noneTerminals;
    }

    public State getStartState() {
        return startState;
    }

    public void setStartState(State startState) {
        this.startState = startState;
    }

    private State startState;

    public Vector<State> getStates() {
        return states;
    }

    public void setStates(Vector<State> states) {
        this.states = states;
    }

    public Vector<Production> getProductions() {
        return productions;
    }

    public void setProductions(Vector<Production> productions) {
        this.productions = productions;
    }



    /*public CFG(Vector<Production> productions){
        this.productions=productions;
    }
    */
    /*
    消除直接左递归
     */
    public boolean removeDirectLeftRecursion(Production production){
            boolean adjust=false;
            Vector<Vector<State>> recursions=new Vector<>();
            Vector<Vector<State>> noneRecursions=new Vector<>();

            for(Vector<State> dividence:production.getRightPart()){
                //System.out.println(dividence.size());
                if(dividence.size()>1 && dividence.get(0)==production.getResult()){
                    recursions.add(dividence);
                }
                else{
                    noneRecursions.add(dividence);
                }
            }

            if(recursions.size()>0) {
                //存在直接左递归
                State state = new State(states.size(), State.STATUS.MID);
                state.setDescription(production.getResult().getDescription()+states.size());
                Production newProduction = new Production();
                newProduction.setResult(state);
                for (Vector<State> pro : recursions) {
                    //System.out.println("add!");
                    pro.add(state);
                }
                recursions.add(new Vector<State>());
                newProduction.setRightPart(recursions);
                for (Vector<State> pro : noneRecursions) {
                    pro.add(state);
                }
                this.alterProduction(production, noneRecursions);
                states.add(state);
                this.addProduction(newProduction);
                adjust=true;
            }
            return adjust;

    }
    /*
    调用前保证所有的states均已加入states向量中
     */
    public void addProduction(Production pro){
        this.productions.add(pro);
        this.transitions.put(pro.getResult(),pro);
    }
    public void alterProduction(Production pro,Vector<Vector<State>> rightPart){
        pro.setRightPart(rightPart);
    }

    /*
    消除左递归
     */
    public void removeLeftRecurision(){
        int size=states.size();
        /*for(int i=0;i<size;i++){
            if (states.get(i).getStatus()!=State.STATUS.TOKEN){
                removeDirectLeftRecursion(transitions.get(states.get(i)));
            }
        }*/
        for(int i=0;i<size;i++){
            System.out.println(i);
            State state=states.get(i);
            //ystem.out.println(state.getDescription());
            if(state.getStatus()!=State.STATUS.TOKEN) {
                Production pro=transitions.get(state);
                int size2=pro.getRightPart().size();
                for(int j=0;j<size2;j++){
                    Vector<State>rightParts=pro.getRightPart().get(j);
                    if(rightParts.size()>0 && rightParts.get(0).getStatus()!=State.STATUS.TOKEN && rightParts.get(0)!=pro.getResult()){
                        State Aj=rightParts.get(0);
                        Production proj=transitions.get(Aj);
                        rightParts.remove(0);
                        pro.getRightPart().remove(rightParts);

                        for(int k=0;k<proj.getRightPart().size();k++){//Vector<State> rightpartsj:proj.getRightPart()){
                            Vector<State> rightpartsj=proj.getRightPart().get(k);
                            Vector<State> vec=new Vector<>();
                            vec.addAll(rightpartsj);
                            vec.addAll(rightParts);
                            pro.getRightPart().add(vec);
                        }
                    }
                }
                removeDirectLeftRecursion(transitions.get(state));
            }

        }
    }

    public void removeEpsilonExpressions(){
        HashSet<State> EpsilonStates=new HashSet<>();
        for(State s:states){
            if(s.getStatus()!=State.STATUS.TOKEN){
                Production pro=transitions.get(s);
                for(int i=0;i<pro.getRightPart().size();i++){
                    Vector rightpart=pro.getRightPart().get(i);
                    if(rightpart.size()==0){
                        EpsilonStates.add(s);
                        pro.getRightPart().remove(i);
                        i--;
                    }
                }
            }
        }
        for(State s:states){
            if(s.getStatus()!=State.STATUS.TOKEN){
                Production pro=transitions.get(s);
                int size=pro.getRightPart().size();
                for(int i=0;i<size;i++){
                    Vector<State> rightpart=pro.getRightPart().get(i);
                    Vector<State> newRightPart=new Vector<>();
                    for(int j=0;j<rightpart.size();j++){
                        State tmp=rightpart.get(j);
                        if(!EpsilonStates.contains(tmp))
                            newRightPart.add(tmp);
                    }
                    if(newRightPart.size()!=rightpart.size())pro.getRightPart().add(newRightPart);
                }
            }
        }

    }

    public void removeCommonSubExpressions(){
        for(int i=0;i<states.size();i++){
            State state=states.get(i);
            if(state.getStatus()!= State.STATUS.TOKEN) {
                Production pro = transitions.get(state);
                if (pro.getRightPart().size() > 1) {
                    for (int j = 0; j < pro.getRightPart().size(); j++) {
                        Vector<State> rightpart = pro.getRightPart().get(j);
                        for (int k = 0; k < pro.getRightPart().size(); k++) {
                            if (k == j) continue;
                            Vector<State> rightpart2 = pro.getRightPart().get(k);
                            Vector<State> longestCommon = new Vector<>();
                            int t = 0;
                            for (t = 0; t < rightpart.size() && t < rightpart2.size(); t++) {
                                if (rightpart.get(t) == rightpart2.get(t)) {
                                    longestCommon.add(rightpart.get(t));
                                } else break;
                            }
                            if (longestCommon.size() != 0) {
                                for (int m = 0; m < t; m++) {
                                    rightpart.remove(0);
                                    rightpart2.remove(0);
                                }


                                    pro.getRightPart().remove(rightpart);
                                    pro.getRightPart().remove(rightpart2);

                                    State newState = new State(states.size(), State.STATUS.MID);
                                    newState.setDescription(state.getDescription() + states.size());
                                    Production newProduction = new Production();
                                    newProduction.setResult(newState);
                                    Vector<Vector<State>> newS = new Vector<>();
                                    newS.add(rightpart);
                                    newS.add(rightpart2);
                                    newProduction.setRightPart(newS);
                                    this.states.add(newState);
                                    addProduction(newProduction);
                                    longestCommon.add(newState);
                                    pro.getRightPart().add(longestCommon);

                            }
                        }
                    }
                }
            }

        }
    }

    public void getFirsts(){
        HashMap<State,Integer> size=new HashMap<>();
        for(State state:this.states){
            if(state.getStatus()!=State.STATUS.TOKEN){
                firstSet.put(state,new HashSet<Token>());
                size.put(state,0);
            }
            else{
                HashSet<Token> single=new HashSet<>();
                single.add(state.getToken());
                firstSet.put(state,single);
            }
        }
        boolean increasing=true;
        while(increasing) {
            increasing=false;
            for(State state:this.states){
                if(state.getStatus()!=State.STATUS.TOKEN){
                    Production p=transitions.get(state);
                    for(Vector<State> vec:p.getRightPart()){
                        if(vec.size()==0)firstSet.get(state).add(emptyState);
                        else{
                            if(vec.get(0).getStatus()==State.STATUS.TOKEN){
                                firstSet.get(state).add(vec.get(0).getToken());
                            }
                            else{
                                boolean isEmpty=true;
                                for(int j=0;j<vec.size();j++) {
                                    //System.out.print("hahaha,");
                                    //System.out.println(vec.get(j).getStatus());
                                    if(vec.get(j).getStatus()==State.STATUS.TOKEN){
                                        //System.out.println("token");
                                        isEmpty=false;
                                        break;
                                    }
                                    if(isEmpty && !isFinallyEmpty(vec.get(j))){
                                        isEmpty=false;
                                    }
                                    HashSet<Token> firstOfY = firstSet.get(vec.get(j));
                                    if (firstOfY.contains(emptyState) && firstSet.get(state).contains(emptyState)) {
                                        firstSet.get(state).addAll(firstOfY);
                                    } else if (firstOfY.contains(emptyState)) {
                                        firstSet.get(state).addAll(firstOfY);
                                        firstSet.get(state).remove(emptyState);
                                    } else {
                                        firstSet.get(state).addAll(firstOfY);
                                    }
                                }
                                if(isEmpty){
                                    firstSet.get(state).add(emptyState);
                                }
                                //checkEmptyInvolves
                            }
                        }
                    }
                    increasing=increasing||(!(firstSet.get(state).size()==size.get(state)));
                    size.put(state,firstSet.get(state).size());
                }
            }
        }
    }
    public void getFollows(){
        if(firstSet.isEmpty())getFirsts();
        HashMap<State,Integer> size=new HashMap<>();
        for(State state:this.states){
            if(state.getStatus()!=State.STATUS.TOKEN){
                followSet.put(state,new HashSet<Token>());
                size.put(state,0);
                if(state.getStatus()==State.STATUS.STRART)followSet.get(state).add(EOF);
            }
        }

        boolean increasing=true;
        while(increasing){
            increasing=false;
            for(State state:this.states){
                if(state.getStatus()!=State.STATUS.TOKEN){
                    for(Production p:this.productions){
                        for(Vector<State> vec:p.getRightPart()){
                            if(vec.contains(state)){
                                int index=0;
                                int pos=vec.indexOf(state,index);
                                while(pos!=-1){
                                    if(pos!=vec.size()-1){
                                        State s2=vec.get(pos+1);
                                        HashSet firstOfs2=firstSet.get(s2);
                                        if(firstOfs2.contains(emptyState) && followSet.get(state).contains(emptyState))
                                            followSet.get(state).addAll(firstOfs2);
                                        else if(firstOfs2.contains(emptyState)){
                                            followSet.get(state).addAll(firstOfs2);
                                            followSet.get(state).remove(emptyState);
                                        }
                                        else{
                                            followSet.get(state).addAll(firstOfs2);
                                        }
                                    }
                                    if(pos==vec.size()-1){
                                        followSet.get(state).addAll(followSet.get(p.getResult()));
                                        break;
                                    }
                                    pos=vec.indexOf(state,pos+1);
                                }
                                int lastPos=vec.lastIndexOf(state);
                                if(lastPos!=vec.size()-1) {
                                    boolean isEmpty = true;
                                    for (lastPos++; lastPos<vec.size();lastPos++){
                                        if(!isFinallyEmpty(vec.get(lastPos))){
                                            isEmpty=false;
                                            break;
                                        }
                                    }
                                    if(isEmpty){followSet.get(state).addAll(followSet.get(p.getResult()));}
                                }
                            }
                        }
                    }
                    increasing=increasing||(!(followSet.get(state).size()==size.get(state)));
                    size.put(state,followSet.get(state).size());
                }
            }

        }
    }

    public HashSet<Token> getFirst(State s){
        if(this.firstSet.size()==0)getFirsts();
        if(s.getStatus()==State.STATUS.TOKEN){
            HashSet<Token> res=new HashSet<>();
            res.add(s.getToken());
            return res;
        }
        return firstSet.get(s);
    }
    public HashSet<Token> getFollow(State s){
        return followSet.get(s);
    }

    public boolean isFinallyEmpty(State s){
        //System.out.println(s.getStatus());
        if(s.getStatus()==State.STATUS.TOKEN)return false;
        if(isEmpty.containsKey(s))return isEmpty.get(s);
        if(transitions.get(s).getRightPart().contains(empty)) {
            isEmpty.put(s,true);
            return true;
        }
        for(Vector<State>vect:transitions.get(s).getRightPart()){
            boolean empty=true;
            for(State state:vect){
                if(!isFinallyEmpty(state)){
                    empty=false;
                    break;
                }
            }
            if(empty){
                isEmpty.put(s,true);
                return true;
            }
        }
        isEmpty.put(s,false);
        return false;
    }

    public void Broaden(){
        State s=new State(noneTerminals.size(),startState.getStatus());
        s.setDescription(startState.getDescription()+states.size());
        //startState.setId(states.size());
        startState.setStatus(State.STATUS.MID);
        Production pro=new Production();
        pro.setRightPart(new Vector<Vector<State>>());
        Vector<State> link=new Vector<>();
        link.add(startState);
        pro.setResult(s);
        pro.getRightPart().add(link);
        this.startState=s;
        addProduction(pro);
        this.states.add(s);
        this.noneTerminals.add(s);
    }

    public void print(){
        for(Production pro:productions){
            for(Vector<State> rightpart:pro.getRightPart()) {
                System.out.print(pro.getResult().getDescription());

                System.out.print("->");
                for (State s : rightpart) {
                    System.out.print(s.getDescription()+" ");
                }
                System.out.println();
            }
        }
    }
    public static void main(String args[]){
        State S=new State(0, State.STATUS.STRART);
        State A=new State(1, State.STATUS.MID);
        State B=new State(2, State.STATUS.MID);
        State C=new State(3, State.STATUS.MID);
        State D=new State(4, State.STATUS.MID);
        State a=new State(5, new Token(1,"a"));
        State b=new State(6, new Token(2,"b"));
        State c=new State(7, new Token(3,"c"));

        Vector<State> states=new Vector<>();
        states.addAll(Arrays.asList(
                new State[]{S,A,B,C,D,a,b,c}
        ));
        CFG cfg=new CFG(states);
        Production production1=new Production();
        production1.setResult(S);
        Vector<State> right1=new Vector<State>();
        right1.add(A);right1.add(B);//right1.add(c);
        Vector<State> right2=new Vector<State>();
        right2.add(b);right2.add(C);//right2.add(B);
        Vector<Vector<State>>right=new Vector<>();
        right.add(right1);right.add(right2);
        production1.setRightPart(right);
        cfg.addProduction(production1);

        Production production2=new Production();
        production2.setResult(A);
        Vector<State> right11=new Vector<State>();
        right11.add(b);//right11.add(C);right11.add(D);right11.add(E);
        Vector<State> right12=new Vector<State>();
        //right12.add(g);right12.add(D);right12.add(B);
        Vector<Vector<State>>right_2=new Vector<>();
        right_2.add(right11);right_2.add(right12);
        production2.setRightPart(right_2);
        cfg.addProduction(production2);

        Production production3=new Production();
        production3.setResult(B);
        Vector<State> right21=new Vector<State>();
        right21.add(a);right21.add(D);//right21.add(B);
        Vector<State> right22=new Vector<State>();
        //right22.add(c);right22.add(a);
        Vector<Vector<State>>right_3=new Vector<>();
        right_3.add(right21);right_3.add(right22);
        production3.setRightPart(right_3);
        cfg.addProduction(production3);

        Production production4=new Production();
        production4.setResult(C);
        Vector<State> right31=new Vector<State>();
        right31.add(A);right31.add(D);//right31.add(B);
        Vector<State> right32=new Vector<State>();
        right32.add(b);//right12.add(D);right12.add(B);
        Vector<Vector<State>>right_4=new Vector<>();
        right_4.add(right31);right_4.add(right32);
        production4.setRightPart(right_4);
        cfg.addProduction(production4);

        Production production5=new Production();
        production5.setResult(D);
        Vector<State> right51=new Vector<State>();
        right51.add(a);right51.add(S);//right51.add(f);//right11.add(E);
        Vector<State> right52=new Vector<State>();
        right52.add(c);//right52.add(D);right12.add(B);
        Vector<Vector<State>>right_5=new Vector<>();
        right_5.add(right51);right_5.add(right52);
        production5.setRightPart(right_5);
        cfg.addProduction(production5);
        cfg.removeLeftRecurision();
        cfg.getFollows();
        //s=cfg.getFollow(A);
        //s=cfg.getFollow(B);
        //s=cfg.getFollow(C);
        //s=cfg.getFollow(D);
        //System.out.println(s);
        System.out.println("a");
        State MAR=new State(0, State.STATUS.STRART);
        State ax=new State(1,new Token(0,"a"));
        State cx=new State(1,new Token(1,"c"));
        Vector<State> currentStates=new Vector<>(Arrays.asList(new State[]{
                MAR,ax,cx
        }));
        CFG cfg2=new CFG(currentStates);
        Production pro=new Production();
        pro.setResult(currentStates.get(0));
        Vector<Vector<State>>link=new Vector<>();
        Vector<State> r1=new Vector<State>();
        r1.add(currentStates.get(0));r1.add(currentStates.get(1));//right51.add(f);//right11.add(E);
        Vector<State> r2=new Vector<State>();
        r2.add(currentStates.get(2));//right52.add(D);right12.add(B);
        link.add(r1);
        link.add(r2);
        pro.setRightPart(link);
        cfg2.addProduction(pro);

        cfg2.removeLeftRecurision();
        System.out.println("a");
        //State s2=new State(1, State.STATUS.MID);

    }


}

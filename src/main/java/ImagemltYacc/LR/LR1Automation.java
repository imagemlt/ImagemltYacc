package ImagemltYacc.LR;

import ImagemltYacc.CFG.CFG;
import ImagemltYacc.CFG.Production;
import ImagemltYacc.CFG.State;
import ImagemltYacc.CFG.Token;

import java.util.*;

public class LR1Automation {
    private CFG cfg;
    public Vector<HashMap<State,LRItem>> Closures;
    public Vector<LR1Item> items;
    private HashMap<LR1Item,HashSet<LR1Item>> closureMap=new HashMap<>();

    public LR1Automation(CFG cfg) {
        this.cfg = cfg;
        int i=0;
        for(Production pro:cfg.getProductions()){
            for(Vector<State> s:pro.getRightPart()){
                SingleProuction single=new SingleProuction(pro.getResult(),s);
                this.productions.add(single);
                singleProductionIdMap.put(single,i);
                i++;
            }
        }
    }

    static Action accept=new Action();

    public HashMap<HashSet<LR1Item>,HashMap<State,Integer>> transitionTable=new HashMap<>();
    public Vector<HashSet<LR1Item>> LRStates=new Vector<>();
    private HashSet<HashSet<LR1Item>> hashSet=new HashSet<>();
    private HashMap<HashSet<LR1Item>,Integer> idMap=new HashMap<>();
    private Vector<SingleProuction> productions=new Vector<>();
    private HashMap<SingleProuction,Integer>singleProductionIdMap=new HashMap<>();
    private LR1Item startItem;//new LR1Item(cfg.getStartState(),0,cfg.getTransitions().get(cfg.getStartState()).getRightPart().get(0),CFG.EOF)
    private LR1Item endItem;//new LR1Item(cfg.getStartState(),1,cfg.getTransitions().get(cfg.getStartState()).getRightPart().get(0),CFG.EOF);
    public HashMap<Integer,HashMap<Token,Action>> actionTable=new HashMap<>();
    public HashMap<Integer,HashMap<State,Integer>> gotoTable=new HashMap<>();

    public Vector<SingleProuction> getProductions() {
        return productions;
    }

    public void setProductions(Vector<SingleProuction> productions) {
        this.productions = productions;
    }

    public Vector<LRItem> LRItems2LRI1tems(){
        Vector<LRItem> ans=new Vector<>();
        return ans;
    }

    public CFG getCfg() {
        return cfg;
    }

    public void setCfg(CFG cfg) {
        this.cfg = cfg;
    }

    public HashSet<LR1Item> getlr1Closure(LR1Item item){
        if(closureMap.containsKey(item)){
            return closureMap.get(item);
        }
        HashSet<LR1Item> ans=new HashSet<>();
        //ans.add(item);
        Stack<LR1Item> stack=new Stack<>();
        stack.push(item);
        while(!stack.empty()){

            LR1Item tmp=stack.pop();
            ans.add(tmp);
            int cur=tmp.getCursor();
            if(cur!=tmp.getRighPart().size()){
                State next=tmp.getRighPart().get(cur);
                if(next.getStatus()!= State.STATUS.TOKEN) {
                    Vector<Token> s2 = new Vector<>();
                    if (cur == tmp.getRighPart().size() - 1) {
                        s2.add(tmp.getFollow());
                    } else {
                        State next2 = tmp.getRighPart().get(cur + 1);
                        if (next2.getStatus() == State.STATUS.TOKEN) {
                            s2.add(next2.getToken());
                        } else {
                            for (Token t : cfg.getFirst(next2)) {
                                s2.add(t);
                            }
                        }
                    }
                    for (Token t : s2) {
                        Production pro = cfg.getTransitions().get(next);
                        for (Vector<State> rightpart : pro.getRightPart()) {
                            //if (cur == tmp.getRighPart().size() - 1) {
                                LR1Item lr = new LR1Item(next, 0, rightpart, t);
                                if(!ans.contains(lr)) {
                                    ans.add(lr);
                                    stack.push(lr);
                                }
                            //}
                        }
                    }
                }

            }
        }
        closureMap.put(item,ans);
        return ans;
    }

    public HashSet<LR1Item> getlr1Closure(HashSet<LR1Item> items){
        HashSet<LR1Item> res=new HashSet<>();
        res.addAll(items);
        boolean increasing=true;
        int oldsize;
        while(increasing) {
            increasing=false;
            HashSet<LR1Item> temp=new HashSet<>();
            for (LRItem l : res) {
                temp.addAll(getlr1Closure((LR1Item) l));
            }
            oldsize=res.size();
            res.addAll(temp);
            increasing=!(oldsize==res.size());
        }

        return res;
    }

    public HashSet<LR1Item> getExtendClosure(HashSet<LR1Item> items,State s){
        HashSet<LR1Item> baseSet=new HashSet<>();
        HashSet<LR1Item> res=new HashSet<>();
        for(LRItem l:items){
            LR1Item lr1=(LR1Item)l;
            if(l.getCursor()<l.getRighPart().size()) {
                if (l.getRighPart().get(l.getCursor()) != s) continue;
                baseSet.add(new LR1Item(l.getResult(), l.getCursor() + 1, l.getRighPart(), ((LR1Item) l).getFollow()));
            }
        }
        for(LRItem l:baseSet){
            res.addAll(getlr1Closure((LR1Item) l));
        }
        if(res.size()!=0){
            if(transitionTable.get(items)==null){
                transitionTable.put(items,new HashMap<State, Integer>());
            }
            if(hashSet.contains(res)){
                transitionTable.get(items).put(s,idMap.get(res));
            }
            else{
                LRStates.add(res);
                transitionTable.get(items).put(s, LRStates.size()-1);
                //
                idMap.put(res,LRStates.size()-1);
            }
        }

        return res;
    }


    public void generateLR1Clousres(){
        startItem=new LR1Item(cfg.getStartState(),0,cfg.getTransitions().get(cfg.getStartState()).getRightPart().get(0),CFG.EOF);
        endItem=new LR1Item(cfg.getStartState(),1,cfg.getTransitions().get(cfg.getStartState()).getRightPart().get(0),CFG.EOF);


        HashSet<LR1Item> initialSet=getlr1Closure(startItem);
        this.LRStates.add(initialSet);
        idMap.put(initialSet,0);
        boolean increase=true;
        Stack<HashSet<LR1Item>> stack=new Stack<>();
        stack.push(initialSet);
        while(!stack.isEmpty()){
            HashSet<LR1Item> tmp=stack.pop();
            HashSet<State> states=new HashSet<>();
            for(LR1Item lr:tmp){
                if(lr.getCursor()<lr.getRighPart().size()){
                    states.add(lr.getRighPart().get(lr.getCursor()));
                }
            }
            for(State s:states){
                HashSet<LR1Item> set=getExtendClosure(tmp,s);
                if(set.size()!=0 && !hashSet.contains(set)){
                    hashSet.add(set);
                    stack.push(set);
                }
            }
        }
    }

    public void generateActionGoto() throws Exception{
        for(HashSet<LR1Item> state:LRStates){
            gotoTable.put(idMap.get(state),new HashMap<State, Integer>());
            actionTable.put(idMap.get(state),new HashMap<Token, Action>());
            HashMap currentGoto=gotoTable.get(idMap.get(state));
            HashMap currentAction=actionTable.get(idMap.get(state));
            for(LR1Item item:state){
                if(item.equals(endItem)){
                    if(currentAction.containsKey(CFG.EOFState)) {
                        if (currentAction.get(CFG.EOFState) != accept)
                            throw new Exception("NOT PROPER LR1 SYNTAX");
                    }
                    else {
                        currentAction.put(CFG.EOF, accept);
                    }
                }
                else if(item.getCursor()==item.getRighPart().size()){
                    //规约
                    if(item.getResult()==cfg.getStartState())
                        throw new Exception("NOT PROPER LR1 SYNTAX");
                       Action act=new Action(singleProductionIdMap.get(new SingleProuction(item.getResult(),item.getRighPart())),Action.Act.REDUCE);
                    if(currentAction.containsKey(item.getFollow())){
                        Action dest=(Action)currentAction.get(item.getFollow());
                        if(!dest.equals(act)) {
                            if(dest.getAction()== Action.Act.SHIFT){
                                int reduceId=act.getDestId();//要规约的id;
                                int shiftId=dest.getShiftId();//要移入的id;
                                if(shiftId>reduceId)continue;//shift优先级更高
                                else{//reduce优先级更高，采取reduce
                                    currentAction.put(item.getFollow(),act);
                                    continue;
                                }
                            }
                            throw new Exception("NOT PROPER LR1 SYNTAX:"+productions.get(dest.getDestId()).getResult().getDescription()+","+productions.get(act.getDestId()).getResult().getDescription());
                        }
                    }else {
                        currentAction.put(item.getFollow(), act);
                    }
                }
                else if(item.getRighPart().get(item.getCursor()).getStatus()== State.STATUS.TOKEN){
                    //移入
                    Action act= new Action(transitionTable.get(state).get(item.getRighPart().get(item.getCursor())), Action.Act.SHIFT,singleProductionIdMap.get(new SingleProuction(item.getResult(),item.getRighPart())));
                    if(currentAction.containsKey(item.getRighPart().get(item.getCursor()).getToken())) {
                        Action dest=(Action)currentAction.get(item.getRighPart().get(item.getCursor()).getToken());
                        if(!dest.equals(act)) {
                            if(dest.getAction()==Action.Act.REDUCE){
                                int reduceId=dest.getDestId();
                                int shiftId=act.getShiftId();
                                if(reduceId>=shiftId)continue;//reduce优先级更高，采取reduce
                                else{//shift优先级更高 采取shift
                                    currentAction.put(item.getRighPart().get(item.getCursor()).getToken(),act);
                                    continue;
                                }
                            }
                            throw new Exception("NOT PROPER LR1 SYNTAX");
                        }
                    }
                    else {
                        currentAction.put(item.getRighPart().get(item.getCursor()).getToken(), act);
                    }
                }
                else if(item.getRighPart().get(item.getCursor()).getStatus()!=State.STATUS.TOKEN){
                    int dest=transitionTable.get(state).get(item.getRighPart().get(item.getCursor()));
                    if(currentGoto.containsKey(item.getRighPart().get(item.getCursor()))) {
                        if(dest!=(int)currentGoto.get(item.getRighPart().get(item.getCursor())))
                        throw new Exception("NOT PROPER LR1 SYNTAX");
                    }
                    currentGoto.put(item.getRighPart().get(item.getCursor()),dest);

                }
            }
        }
    }

    /*
    移入规约算法
     */
    public void ShiftReduce(Vector<Token> tokens)throws Exception{
        tokens.add(CFG.EOF);
        Stack<HashSet<LR1Item>> stateStack=new Stack<>();
        Stack<State> symbolStack=new Stack<>();
        stateStack.push(LRStates.get(0));
        int pos=0;
        while(true){
            HashSet<LR1Item> top=stateStack.pop();
            stateStack.push(top);
            Action act=actionTable.get(idMap.get(top)).get(tokens.get(pos));
            if(act==null){
                throw new Exception("invalid token:"+tokens.get(pos).getCh());
            }
            if(act.getAction()== Action.Act.SHIFT){
                stateStack.push(LRStates.get(act.getDestId()));
                symbolStack.push(cfg.tokenMap.get(actionTable.get(top)));
                pos++;
            }
            else if(act.getAction()== Action.Act.REDUCE){
                SingleProuction pro=productions.get(act.getDestId());
                int size=pro.getRightPart().size();
                while(size>0){
                    stateStack.pop();
                    symbolStack.pop();
                    size--;
                }
                symbolStack.push(pro.getResult());
                top=stateStack.pop();
                stateStack.push(top);
                stateStack.add(LRStates.get(gotoTable.get(idMap.get(top)).get(pro.getResult())));
                System.out.print(pro.getResult().getDescription());
                System.out.print("->");
                for(State s:pro.getRightPart()){
                    System.out.print(s.getDescription()+" ");
                }
                System.out.println();
            }
            else if(act.getAction()== Action.Act.ACCEPT){
                System.out.println("Match Success");
                break;
            }
        }


    }




    public static void main(String args[]){

        //赋值语法生成自动机test case

        Vector<Token> tokens=new Vector<Token>(Arrays.asList(new Token[]{
                new Token(1,"a"),new Token(2,"="),new Token(3,"*")
        }));
        State S=new State(0, State.STATUS.STRART);
        S.setDescription("S");
        State L=new State(1, State.STATUS.MID);
        L.setDescription("L");
        State R=new State(2, State.STATUS.MID);
        R.setDescription("R");
        //State C=new State(3, State.STATUS.MID);
        //State D=new State(4, State.STATUS.MID);
        State a=new State(5, tokens.get(0));
        a.setDescription("a");
        State eq=new State(6, tokens.get(1));
        eq.setDescription("=");
        State mul=new State(7, tokens.get(2));
        mul.setDescription("*");

        Vector<State> states=new Vector<>();
        states.addAll(Arrays.asList(
                new State[]{S,L,R,a,eq,mul}
        ));

        CFG cfg=new CFG(states);
        Production production1=new Production();
        production1.setResult(S);
        Vector<State> right1=new Vector<State>();
        right1.add(L);right1.add(eq);right1.add(R);                             //S->L=R
        Vector<State> right2=new Vector<State>();
        right2.add(R);                                                          //S->R
        Vector<Vector<State>>right=new Vector<>();
        right.add(right1);right.add(right2);
        production1.setRightPart(right);
        cfg.addProduction(production1);

        Production production2=new Production();
        production2.setResult(L);
        Vector<State> right11=new Vector<State>();
        right11.add(mul);right11.add(R);                                        //L->*R
        Vector<State> right12=new Vector<State>();
        right12.add(a);//right12.add(D);right12.add(B);                         //L->a
        Vector<Vector<State>>right_2=new Vector<>();
        right_2.add(right11);right_2.add(right12);
        production2.setRightPart(right_2);
        cfg.addProduction(production2);

        Production production3=new Production();
        production3.setResult(R);
        Vector<State> right21=new Vector<State>();
        right21.add(L);//right21.add(B);                                        //R->L
        Vector<Vector<State>>right_3=new Vector<>();
        right_3.add(right21);
        production3.setRightPart(right_3);
        cfg.addProduction(production3);
        cfg.setStartState(S);
        //cfg.removeLeftRecurision();
        cfg.Broaden();
        cfg.getFollows();



        LR1Automation automation=new LR1Automation(cfg);

        automation.generateLR1Clousres();

        try {
            automation.generateActionGoto();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        Vector<Token> serial=new Vector<>(Arrays.asList(
                new Token[]{
                     tokens.get(0)
                }
        ));

        try {
            automation.ShiftReduce(serial);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("end");

    }

}

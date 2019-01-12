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

    public LR1Automation(CFG cfg) {
        this.cfg = cfg;
    }

    public HashMap<HashSet<LR1Item>,HashMap<State,Integer>> transitionTable=new HashMap<>();
    public Vector<HashSet<LR1Item>> LRStates=new Vector<>();
    private HashSet<HashSet<LR1Item>> hashSet=new HashSet<>();
    private HashMap<HashSet<LR1Item>,Integer> idMap=new HashMap<>();



    public Vector<LRItem> LRItems2LRI1tems(){
        Vector<LRItem> ans=new Vector<>();
        return ans;
    }

    public HashSet<LR1Item> getlr1Closure(LR1Item item){
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
                                ans.add(lr);
                                stack.push(lr);
                            //}
                        }
                    }
                }

            }
        }
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
        HashSet<LR1Item> initialSet=getlr1Closure(new LR1Item(cfg.getStartState(),0,cfg.getTransitions().get(cfg.getStartState()).getRightPart().get(0),CFG.EOF));
        this.LRStates.add(initialSet);
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

    public static void main(String args[]){
        State S=new State(0, State.STATUS.STRART);
        State L=new State(1, State.STATUS.MID);
        State R=new State(2, State.STATUS.MID);
        //State C=new State(3, State.STATUS.MID);
        //State D=new State(4, State.STATUS.MID);
        State a=new State(5, new Token(1,'a'));
        State eq=new State(6, new Token(2,'='));
        State mul=new State(7, new Token(3,'*'));

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
        cfg.removeLeftRecurision();
        cfg.Broaden();
        cfg.getFollows();



        LR1Automation automation=new LR1Automation(cfg);

        automation.generateLR1Clousres();




        System.out.println("end");

    }

}

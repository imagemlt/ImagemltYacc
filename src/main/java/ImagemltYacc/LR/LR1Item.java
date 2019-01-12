package ImagemltYacc.LR;

import ImagemltYacc.CFG.State;
import ImagemltYacc.CFG.Token;

import java.util.Vector;

public class LR1Item extends LRItem {

    private Token follow;

    public Token getFollow() {
        return follow;
    }

    public void setFollow(Token follow) {
        this.follow = follow;
    }

    public LR1Item(State result, int cursor, Vector<State> righPart, Token follow) {
        super(result, cursor, righPart);
        this.follow = follow;
    }
    public LR1Item(LRItem lritem,Token follow){
        super(lritem.getResult(),lritem.getCursor(),lritem.getRighPart());
        this.follow=follow;
    }

    @Override
    public int hashCode(){
        return this.result.hashCode()+this.cursor+this.righPart.hashCode()+this.follow.hashCode();
    }

    @Override
    public boolean equals(Object object){
        LR1Item i2=(LR1Item)object;
        return this.result==i2.result && this.cursor==i2.cursor && this.righPart.equals(i2.righPart) && this.follow==i2.follow;
    }

}

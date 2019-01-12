package ImagemltYacc.LR;

import ImagemltYacc.CFG.State;

import java.util.Vector;

public class LRItem {
    protected int cursor;
    protected Vector<State> righPart;
    protected State result;

    public LRItem(State result, int cursor, Vector<State> righPart) {
        this.result = result;
        this.cursor = cursor;
        this.righPart = righPart;
    }

    public State getResult() {
        return result;
    }

    public void setResult(State result) {
        result = result;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public Vector<State> getRighPart() {
        return righPart;
    }

    public void setRighPart(Vector<State> righPart) {
        this.righPart = righPart;
    }

}

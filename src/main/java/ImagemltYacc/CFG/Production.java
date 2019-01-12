package ImagemltYacc.CFG;

import java.util.Vector;

public class Production {
    Vector<Vector<State>> rightPart;
    State Result;

    public State getResult() {
        return Result;
    }

    public void setResult(State result) {
        Result = result;
    }

    public Vector<Vector<State>> getRightPart() {
        return rightPart;
    }

    public void setRightPart(Vector<Vector<State>> rightPart) {
        this.rightPart = rightPart;
    }
}

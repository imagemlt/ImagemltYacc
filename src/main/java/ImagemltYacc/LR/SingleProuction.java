package ImagemltYacc.LR;

import ImagemltYacc.CFG.State;

import java.util.Objects;
import java.util.Vector;

public class SingleProuction {
    State result;
    Vector<State> rightPart;

    public SingleProuction(State result, Vector<State> rightPart) {
        this.result = result;
        this.rightPart = rightPart;
    }

    public State getResult() {
        return result;
    }

    public void setResult(State result) {
        this.result = result;
    }

    public Vector<State> getRightPart() {
        return rightPart;
    }

    public void setRightPart(Vector<State> rightPart) {
        this.rightPart = rightPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleProuction that = (SingleProuction) o;
        return Objects.equals(result, that.result) &&
                Objects.equals(rightPart, that.rightPart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, rightPart);
    }
}

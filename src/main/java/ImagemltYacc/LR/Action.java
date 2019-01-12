package ImagemltYacc.LR;

import java.util.Objects;

public class Action {
    private int destId;
    public enum Act{
        SHIFT,
        REDUCE,
        ACCEPT
    };
    private Act action;
    private int shiftId;

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public Action(int destId, Act action) {
        this.destId = destId;
        this.action = action;
        this.shiftId=destId;
    }
    public Action(int destId,Act action,int shiftId){
        this.destId=destId;
        this.action=action;
        this.shiftId=shiftId;
    }

    public Action(){
        this.action=Act.ACCEPT;
    }

    public int getDestId() {
        return destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }

    public Act getAction() {
        return action;
    }

    public void setAction(Act action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action1 = (Action) o;
        return destId == action1.destId &&
                action == action1.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destId, action);
    }
}

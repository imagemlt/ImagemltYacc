package ImagemltYacc.CFG;

import java.util.Objects;

public class State {
    public enum STATUS {
        STRART,
        TOKEN,
        MID,
        EMPTY,
        EOF
    };
    private int id;

    public String getDescription() {
        return description;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    private Token token;

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    private STATUS status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State(int id, STATUS status){
        //开始或者中间
        this.id=id;
        this.status=status;
    }
    public State(int id,Token token){
        //类型为一个token
        this.id=id;
        this.token=token;
        this.status=STATUS.TOKEN;
    }

}

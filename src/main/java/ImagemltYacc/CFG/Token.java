package ImagemltYacc.CFG;

public class Token {
    private int id;
    private Character ch;
    public enum TokenType{
        EOF,
        NORMAL,
        EPSILON
    }

    private TokenType type;

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public Token(int id, Character ch) {
        this.id = id;
        this.ch = ch;
        this.type=TokenType.NORMAL;
    }

    public Token(int id,TokenType type){
        this.id=id;
        this.type=type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Character getCh() {
        return ch;
    }

    public void setCh(Character ch) {
        this.ch = ch;
    }
}


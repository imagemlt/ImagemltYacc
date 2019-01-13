# ImagemltYacc

编译原理大作业 yacc

配合ImagemltLex使用

lex文件：
```
( \(
) \)
ASS =
; ;
{ {
} }
IF if
ELSE else
RETURN return
TYPE (int)|(float)|(boolean)
ID [a-zA-Z][a-zA-Z0-9]*
DIGITS \d+
arithmetic_op \+|-|\*|/
logical_op (&&)|(==)|(\|\|)|([&|><])
%%
	private HashSet<Character> symbols=new HashSet<>(
		Arrays.asList(new Character[]{
			'(',')',',','=','+','-','*','/',';','{','}','&','|','>','<'
		})
	);
	private String[] matchTable={"(,%s","),%s","ASS,%s",";,%s","{,%s","},%s","IF,%s","ELSE,%s","RETURN,%s","TYPE,%s","ID,%s","DIGITS,%s","arithmetic_op,%s","logical_op,%s",};

%%
 public static void main(String args[]){
            try {
                Scanner scanner=new Scanner(true);
                Vector<Pair<Integer,String>> tokens=scanner.Scan(new FileReader("/tmp/Source.c"));
                BufferedWriter writer=new BufferedWriter(new FileWriter("/tmp/tokens"));
                for(Pair<Integer,String>token:tokens){
                    writer.write(String.format(scanner.matchTable[scanner.stateIds[token.getKey()]],token.getValue())+"\n");
                }
                writer.flush();
                writer.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
```
yacc文件:
```
ID
DIGITS
ASS
IF
ELSE
RETURN
TYPE
{
}
(
)
;
arithmetic_op
logical_op
%%
START
%%

%%
START:{ START }
START:function_statement
START:TERM ;
START:control_statement
START:TERM ; START

TERM:arithmetic_expression
TERM:assignment_statement
TERM:declaration_statement

assignment_statement:ID ASS arithmetic_expression

function_statement:TYPE ID ( ) { START }
function_statement:TYPE ID ( ) { START RETURN arithmetic_expression ; }

declaration_statement:TYPE ID

control_statement:IF ( logical_expression ) START
control_statement:IF ( logical_expression ) START ELSE START

arithmetic_expression:arithmetic_expression arithmetic_op arithmetic_expression
arithmetic_expression:ID
arithmetic_expression:DIGITS

logical_expression:logical_expression logical_op logical_expression
logical_expression:ID
logical_expression:DIGITS

%%
public static void main(String[] args) {
        Parser parser = new Parser();
        try{
            parser.shiftReduce(parser.readTokens(new FileReader("/tmp/tokens")));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
```

测试程序:
```c
int main(){
int a;
a=0;
if(a>0)
{
a=a+1;
}
else
{
a=5;
}
}
```
首先运行`java -jar ImagemltLex.jar lex文件 Scanner.java`生成Scanner,
运行Scanner可以生成`/tmp/tokens`文件

之后运行`java -jar ImagemltYacc.jar yacc文件 Parser.java`生成Parser,
运行parser可以输出自底向上算法的每一步规约过程。

生成的Parser类的成员属性parsedTree即为解析后的语法树。
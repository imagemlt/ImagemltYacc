# ImagemltYacc

### 实验2 语法分析器Yacc

#### 实验目的

- Parser生成器：根据指定的CFG生成对应的`Parser.java`
- `Parser.java` 跟据由Lab1生成的token序列构造语法树并且输出自底向上算法中的每一步规约过程。

#### 实现的思路：

- LR1分析算法，根据CFG生成LR1状态转换表ACTION与GOTO
- 处理二义性的文法的移入规约冲突：如果移入的产生式的优先级大于规约的产生式的优先级则采取移入动作；否则采取规约动作

#### 案例

##### 文法描述文件：

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
COMMA
arithmetic_op
logical_op
%%
START
%%

%%
START:{ START }
START:function_statement
START:START function_statement
START:TERM ;
START:control_statement
START:TERM ; START

TERM:arithmetic_expression
TERM:assignment_statement
TERM:declaration_statement

assignment_statement:ID ASS arithmetic_expression



function_statement:TYPE ID ( ) { START }
function_statement:TYPE ID ( ) { START RETURN arithmetic_expression ; }
function_statement:TYPE ID ( func_parameter_declaration ) { START }
function_statement:TYPE ID ( func_parameter_declaration ) { START RETURN arithmetic_expression ; }
function_statement:TYPE ID ( func_parameter_declaration ) { RETURN arithmetic_expression ; }

declaration_statement:TYPE ID
declaration_statement:declaration_statement COMMA ID
declaration_statement:declaration_statement COMMA declaration_statement

control_statement:IF ( logical_expression ) START
control_statement:IF ( logical_expression ) START ELSE START
call_expression:ID ( parameters_expression )

parameters_expression:parameters_expression COMMA parameters_expression
parameters_expression:arithmetic_expression

func_parameter_declaration:TYPE ID
func_parameter_declaration:func_parameter_declaration COMMA func_parameter_declaration


arithmetic_expression:arithmetic_expression arithmetic_op arithmetic_expression
arithmetic_expression:ID
arithmetic_expression:DIGITS
arithmetic_expression:call_expression
arithmetic_expression:( logical_expression )

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

这里的文法是一个存在二义性的文法，然而仍然可以利用带有优先级判断的LR1分析来解决产生的移入规约冲突问题。

运行`java -jar ImagemltYacc.jar /tmp/yacc /tmp/Parser.java`可以生成针对该文法的Parser

#### 源文件Source.c

```c
float func(int m,int n){
	return m+n;
}
int main(){
	int a,b;
	a=0;
	if(a>0){
		a=func(a,b);
	}
	else
	{
		a=5;
	}
	return 1;
}
```

#### token序列

```
TYPE,float
ID,func
(,(
TYPE,int
ID,m
COMMA,,
TYPE,int
ID,n
),)
{,{
RETURN,return
ID,m
arithmetic_op,+
ID,n
;,;
},}
TYPE,int
ID,main
(,(
),)
{,{
TYPE,int
ID,a
COMMA,,
ID,b
;,;
ID,a
ASS,=
DIGITS,0
;,;
IF,if
(,(
ID,a
logical_op,>
DIGITS,0
),)
{,{
ID,a
ASS,=
ID,func
(,(
ID,a
COMMA,,
ID,b
),)
;,;
},}
ELSE,else
{,{
ID,a
ASS,=
DIGITS,5
;,;
},}
RETURN,return
DIGITS,1
;,;
},}

```



#### 生成的Parser

```java
/*
* too long to show 
*/

```

运行`javac Parser.java` `java Parser`可以对Lab1生成的Token序列进行语法分析。Parser类的成员属性parsedTree即为解析后的多叉树，同时输出自底向上分析流程的规约步骤

```
func_parameter_declaration->TYPE ID 	int m
func_parameter_declaration->TYPE ID 	int n
func_parameter_declaration->func_parameter_declaration COMMA func_parameter_declaration 	func_parameter_declaration , func_parameter_declaration
arithmetic_expression->ID 	m
arithmetic_expression->ID 	n
arithmetic_expression->arithmetic_expression arithmetic_op arithmetic_expression 	arithmetic_expression + arithmetic_expression
function_statement->TYPE ID ( func_parameter_declaration ) { RETURN arithmetic_expression ; } 	float func ( func_parameter_declaration ) { return arithmetic_expression ; }
START->function_statement 	function_statement
declaration_statement->TYPE ID 	int a
declaration_statement->declaration_statement COMMA ID 	declaration_statement , b
TERM->declaration_statement 	declaration_statement
arithmetic_expression->DIGITS 	0
assignment_statement->ID ASS arithmetic_expression 	a = arithmetic_expression
TERM->assignment_statement 	assignment_statement
logical_expression->ID 	a
logical_expression->DIGITS 	0
logical_expression->logical_expression logical_op logical_expression 	logical_expression > logical_expression
arithmetic_expression->ID 	a
parameters_expression->arithmetic_expression 	arithmetic_expression
arithmetic_expression->ID 	b
parameters_expression->arithmetic_expression 	arithmetic_expression
parameters_expression->parameters_expression COMMA parameters_expression 	parameters_expression , parameters_expression
call_expression->ID ( parameters_expression ) 	func ( parameters_expression )
arithmetic_expression->call_expression 	call_expression
assignment_statement->ID ASS arithmetic_expression 	a = arithmetic_expression
TERM->assignment_statement 	assignment_statement
START->TERM ; 	TERM ;
START->{ START } 	{ START }
arithmetic_expression->DIGITS 	5
assignment_statement->ID ASS arithmetic_expression 	a = arithmetic_expression
TERM->assignment_statement 	assignment_statement
START->TERM ; 	TERM ;
START->{ START } 	{ START }
control_statement->IF ( logical_expression ) START ELSE START 	if ( logical_expression ) START else START
START->control_statement 	control_statement
START->TERM ; START 	TERM ; START
START->TERM ; START 	TERM ; START
arithmetic_expression->DIGITS 	1
function_statement->TYPE ID ( ) { START RETURN arithmetic_expression ; } 	int main ( ) { START return arithmetic_expression ; }
START->START function_statement 	START function_statement
[+]ACCEPT!
```

#### 核心算法描述

##### 项目结构


- ImagemltYacc.CFG.CFG类：CFG文法描述
- ImagemltYacc.CFG.State类：CFG中的各个状态
- ImagemltYacc.CFG.Token类：表示Token
- ImagemltYacc.CFG.Production类：表示产生式
- ImagemltYacc.LR.Action类 表示Action表中的移入、规约、接受动作
- ImagemltYacc.LR1Automation类 LR1自动机
- ImagemltYacc.LR1Item  LR1项目
- ImagemltYacc.LRItem LR0项目
- ImagemltYacc.SingleProduction类 表示单个产生式
- io包中的类用于解析Yacc文件、输出Scanner



#### 核心算法

- `class CFG`
  - `public boolean removeDirectLeftRecursion(Production production)`
    - 消除直接左递归 其实LR1中并不需要
  - removeLeftRecurision()
    - 消除左递归算法（LR1不需要）
  - removeEpsilonExpressions()
    - 消除空产生式
  - `public void removeCommonSubExpressions()`
    - 消除公共左因子
  - `public void getFirsts()`
    - 获取所有项目的first集
  - public void getFollows()
    - 获取所有项目的follow集
  - Broaden()
    - 扩展CFG，加入新的开始点
- class LR1Automation
  - `public HashSet<LR1Item> getlr1Closure(LR1Item item)`
    - 获取某个LR1项目的LR1闭包
  - `public HashSet<LR1Item> getlr1Closure(HashSet<LR1Item> items)`
    - 获取某个项目集的LR1闭包
  - `public HashSet<LR1Item> getExtendClosure(HashSet<LR1Item> items,State s)`
    - 获取某个项目集转移后的LR1闭包
  - `public void generateLR1Clousres()`
    - 根据CFG获取所有的LR1项目闭包
  - `public void generateActionGoto() throws Exception`
    - 根据CFG生成Action表和Goto表，并且加入了错误处理，当产生移入规约冲突并且冲突无法解决的情况下报错。
- 生成的`Parser`类
  - 内部类`class TreeNode`
    - 多叉树的节点，用于构造最后的语法树
  - 内部类Token
    - 表示Token的一个类
  - `Integer[][] gotoTable` 
    - goto表，用一个二维数组表示
  - `HashMap<Integer,HashMap<Integer,Pair<ACTION,Integer>>> actionTable`
    - Action表，用一个HashMap表示
  - `public void shiftReduce(Vector<Token> tokens) throws Exception`
    - 移入规约推导算法,如果检测到语法错误将跑出异常。
    - 利用两个栈来辅助构造最后的语法树。
  - `private TreeNode parsedTree;`
    - 这个成员变量就是最后的语法多叉树，可以在F5调试中看一下结构
    - Child成员表示所有的子节点，依次类推。

#### 程序开发中遇到的一些问题以及解决方法：

遇到的最显著的问题就是移入规约的冲突问题，比如在if else语句中的移入规约冲突。最后使用引入优先级来解决了问题。

与词法分析器的项目类似，多次使用Stack HashMap HashSet以优化程序的运行效率。



#### 感受


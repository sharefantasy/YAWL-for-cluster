package cluster.ditribute.strategy;

/**
 * Created by fantasy on 2016/1/26.
 */
public class A {
    public void method(){
        System.out.println("A");
    }
    public A(){
        method();
    }
    public static void main(String[] args){
        A a = new B();

    }
}
class B extends A{
    public void method() {
        System.out.println("B");
    }

}

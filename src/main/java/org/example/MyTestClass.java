package org.example;

public class MyTestClass {
    @RxField(info = "", name = "int1")
    public Integer i1;
    @RxField(info = "", name = "int2")
    private Integer i2;
    @RxField(info = "", name = "int3")
    protected Integer i3;

    @RxField(info = "", name = "str1")
    public String s1;
    @RxField(info = "", name = "str2")
    private String s2;

    private Integer not1;
    private String not2;

    MyTestClass() {
        i1 = 100;
        i2 = 200;
        i3 = 300;

        s1 = "Hello";
        s2 = "Reflection";

        not1 = 123;
        not2 = "World";
    }

    public void print() {
        System.out.println(
                "i1 = " + i1 +
                        "\ni2 = " + i2 +
                        "\ni3 = " + i3 +
                        "\ns1 = " + s1 +
                        "\ns2 = " + s2 +
                        "\nnot1 = " + not1 +
                        "\nnot2 = " + not2
        );
    }

    public void i1(Integer val) {
        i1 = val;
    }

    public void i2(Integer val) {
        i2 = val;
    }

    public void i3(Integer val) {
        i3 = val;
    }

    public void s1(String val) {
        s1 = val;
    }

    public void s2(String val) {
        s2 = val;
    }
}

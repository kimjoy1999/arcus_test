// HelloArcusTest.java
package com.navercorp.arcus;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class HelloArcusTest {

    HelloArcus helloArcus = new HelloArcus("127.0.0.1:2181", "test");

    @Before
    public void sayHello() {
        helloArcus.sayHello();
    }

    @Test
    public void listenHello() {
        Assert.assertEquals("성공", helloArcus.listenHello());
    }

}

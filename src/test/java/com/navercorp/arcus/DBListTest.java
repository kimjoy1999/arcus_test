// DBListTest.java
package com.navercorp.arcus;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class DBListTest {

	DBList db = new DBList();

	@Before
	public void makeDB() {
		db.makeDB();
	}

	@Test
	public void checkDB() {
		Assert.assertEquals("성공", db.checkDB());
	}

}

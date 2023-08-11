package br.com.rlindner.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.rlindner.entities.Usuario;

public class AssertTest {

	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		Assert.assertEquals(2,2);
		Assert.assertEquals(1.2, 1.2, 0.03);
		Assert.assertEquals(Math.PI, 3.14, 0.01);
		
		int i = 5;
		Integer i2 = 5;
		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());
		
		Assert.assertEquals("bola", "bola");
		Assert.assertTrue("bola".equalsIgnoreCase("BOLA"));
		Assert.assertTrue("bola".startsWith("bol"));
		
		Usuario u1 = new Usuario("Usuario 2");
		Usuario u2 = new Usuario("Usuario 2");
		Usuario u3 = null;
		
		Assert.assertEquals(u1, u2);	

		Assert.assertSame(u2, u2);
		Assert.assertNotSame(u1, u2);
		
		Assert.assertNull(u3);
		
	}
}

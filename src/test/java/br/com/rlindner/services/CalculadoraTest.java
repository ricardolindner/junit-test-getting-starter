package br.com.rlindner.services;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.rlindner.exceptions.NaoPodeDividirPorZeroException;
import br.com.rlindner.runners.ParallelRunner;

@RunWith(ParallelRunner.class)
public class CalculadoraTest {
	
	public static StringBuffer ordem = new StringBuffer();

	private Calculadora calc;

	@Before
	public void setup() {
		calc = new Calculadora();
		System.out.println("Iniciando 1");
		ordem.append("1");
	}

	@After
	public void tearDown() {
		System.out.println("Finalizando 1");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(ordem.toString());
	}

	@Test
	public void testDeveSomarDoisValores() {
		// Assemble
		int a = 5;
		int b = 3;

		// Act
		int resultado = calc.somar(a, b);

		// Check
		Assert.assertEquals(8, resultado);
	}

	@Test
	public void testDeveSubtrairDoisValores() {
		// Assemble
		int a = 8;
		int b = 5;

		// Act
		int resultado = calc.subtrair(a, b);

		// Check
		Assert.assertEquals(3, resultado);
	}

	@Test
	public void testDeveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		// Assemble
		int a = 6;
		int b = 2;

		// Act
		int resultado = calc.dividir(a, b);

		// Check
		Assert.assertEquals(3, resultado);
	}

	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void testDeveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
		// Assemble
		int a = 10;
		int b = 0;

		// Act
		calc.dividir(a, b);

		// Check
	}

	@Test
	public void deveDividir() throws NaoPodeDividirPorZeroException {
		// Assemble
		String a = "6";
		String b = "3";

		// Act
		int resultado = calc.dividir(a, b);

		// Check
		Assert.assertEquals(2, resultado);
	}

}

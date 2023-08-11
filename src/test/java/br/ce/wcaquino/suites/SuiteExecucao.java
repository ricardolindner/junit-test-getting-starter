package br.ce.wcaquino.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Suite.SuiteClasses;

import br.com.rlindner.services.CalculoValorLocacaoTest;
import br.com.rlindner.services.LocacaoServiceTest;

//@RunWith(Suite.class)
@SuiteClasses({ 
	//CalculadoraTest.class, 
	CalculoValorLocacaoTest.class, 
	LocacaoServiceTest.class
	})
public class SuiteExecucao {

	@BeforeClass
	public static void before() {
		System.out.println("Before Class");
	}

	@AfterClass
	public static void after() {
		System.out.println("After Class");
	}
}

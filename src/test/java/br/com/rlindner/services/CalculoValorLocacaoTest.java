package br.com.rlindner.services;

import static br.com.rlindner.builders.FilmeBuilder.umFilme;
import static br.com.rlindner.builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.rlindner.daos.LocacaoDAO;
import br.com.rlindner.entities.Filme;
import br.com.rlindner.entities.Locacao;
import br.com.rlindner.entities.Usuario;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	@InjectMocks
	private LocacaoService service;

	@Mock
	private LocacaoDAO dao;

	@Mock
	private SPCService spc;

	@Parameter
	public List<Filme> filmes;

	@Parameter(value = 1)
	public double valorLocacao;

	@Parameter(value = 2)
	public String cenario;

	private static Filme filme1 = umFilme().agora();
	private static Filme filme2 = umFilme().agora();
	private static Filme filme3 = umFilme().agora();
	private static Filme filme4 = umFilme().agora();
	private static Filme filme5 = umFilme().agora();
	private static Filme filme6 = umFilme().agora();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("Iniciando 3");
		CalculadoraTest.ordem.append("3");
	}

	@Parameters(name = "Teste {2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] { { Arrays.asList(filme1, filme2), 8.0, "02 Filmes: Sem Desconto" },
				{ Arrays.asList(filme1, filme2, filme3), 11.0, "03 Filmes: 25%" },
				{ Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "04 Filmes: 50%" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "05 Filmes: 75%" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "06 Filmes: 100%" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme6), 18.0,
						"07 Filmes: Sem Desconto" } });
	}

	@After
	public void tearDown() {
		System.out.println("Finalizando 3");
	}

	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}

	@Test
	public void testeDeveCalcularValorLocacaoConsiderandoDesconto() throws Exception {
		// Assemble
		Usuario usuario = umUsuario().agora();

		// Act
		Locacao result = service.alugarFilme(usuario, filmes);

		// Check
		assertThat(result.getValor(), is(valorLocacao));
	}

	@Test
	public void printTest() {
		System.out.println(valorLocacao);
	}
}

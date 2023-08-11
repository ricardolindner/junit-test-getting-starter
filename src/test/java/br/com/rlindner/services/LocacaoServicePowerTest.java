package br.com.rlindner.services;

import static br.ce.wcaquino.servicos.matchers.MatchersProprios.caiNumaSegunda;
import static br.com.rlindner.builders.FilmeBuilder.umFilme;
import static br.com.rlindner.builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.com.rlindner.builders.UsuarioBuilder;
import br.com.rlindner.daos.LocacaoDAO;
import br.com.rlindner.entities.Filme;
import br.com.rlindner.entities.Locacao;
import br.com.rlindner.entities.Usuario;
import br.com.rlindner.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DataUtils.class, LocacaoService.class })
public class LocacaoServicePowerTest {

	@InjectMocks
	private LocacaoService service;

	@Mock
	private LocacaoDAO dao;

	@Mock
	private SPCService spc;

	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service = PowerMockito.spy(service);
		System.out.println("Iniciando 4");
		CalculadoraTest.ordem.append("4");
	}

	@After
	public void tearDown() {
		System.out.println("Finalizando 4");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println(CalculadoraTest.ordem.toString());
	}

	@Test
	public void testeDeveAlugarFilmeComSucesso() throws Exception {
		// Assemble
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());

		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(28, 4, 2017));

		// Act
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// Check
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}

	@Test
	public void testeNaoDeveDevolverFilmeDomingo() throws Exception {
		// Assemble
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(29, 4, 2017));

		// Act
		Locacao result = service.alugarFilme(usuario, filmes);

		// Check
		assertThat(result.getDataRetorno(), caiNumaSegunda());

		PowerMockito.verifyStatic();
	}

	@Test
	public void testDeveAlugarFilmeSemCalcularValor() throws Exception {
		// Assemble
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);

		// Act
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// Check
		Assert.assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}

	@Test
	public void testDeveCalcularValorLocacao() throws Exception {
		// Assemble
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// Act
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);

		// Check
		Assert.assertThat(valor, is(4.0));
	}

}

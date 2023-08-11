package br.com.rlindner.services;

import static br.ce.wcaquino.servicos.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.servicos.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.servicos.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.com.rlindner.builders.FilmeBuilder.umFilme;
import static br.com.rlindner.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.rlindner.builders.UsuarioBuilder.umUsuario;
import static br.com.rlindner.utils.DataUtils.isMesmaData;
import static br.com.rlindner.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import br.com.rlindner.builders.LocacaoBuilder;
import br.com.rlindner.builders.UsuarioBuilder;
import br.com.rlindner.daos.LocacaoDAO;
import br.com.rlindner.entities.Filme;
import br.com.rlindner.entities.Locacao;
import br.com.rlindner.entities.Usuario;
import br.com.rlindner.exceptions.FilmeSemEstoqueException;
import br.com.rlindner.exceptions.LocadoraException;
import br.com.rlindner.runners.ParallelRunner;
import br.com.rlindner.utils.DataUtils;

@RunWith(ParallelRunner.class)
public class LocacaoServiceTest {

	@InjectMocks
	@Spy
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
		System.out.println("Iniciando 2");
		CalculadoraTest.ordem.append("2");
	}

	@After
	public void tearDown() {
		System.out.println("Finalizando 2");
	}

	@BeforeClass
	public static void setupClass() {
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

		Mockito.doReturn(DataUtils.obterData(28, 04, 2017)).when(service).obterData();

		// Act
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// Check
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(28, 4, 2017)), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(29, 4, 2017)), is(true));
	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void testeDeveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		// Assemble
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

		// Act
		Locacao locacao = service.alugarFilme(usuario, filmes);

		// Check
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}

	@Test
	public void testeDeveLancarExcecaoUsuario() throws Exception {
		// Assemble
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// act
		try {
			service.alugarFilme(null, filmes);
		} catch (FilmeSemEstoqueException e) {
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario Vazio"));
		}
	}

	@Test
	public void testeDeveLancarExcecaoSemFilme() throws Exception {
		// Assemble
		Usuario usuario = umUsuario().agora();

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme Vazio");

		// act
		service.alugarFilme(usuario, null);
	}

	@Test
	public void testeNaoDeveDevolverFilmeDomingo() throws Exception {
		// Assemble
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		Mockito.doReturn(DataUtils.obterData(29, 04, 2017)).when(service).obterData();

		// Act
		Locacao result = service.alugarFilme(usuario, filmes);

		// Check
		assertThat(result.getDataRetorno(), caiNumaSegunda());
	}

	@Test
	public void testNaoDeveAlugarFilmeParaNegativado() throws Exception {
		// Assemble
		Usuario usuario = umUsuario().agora();
		// Usuario usuario2 = umUsuario().comNome("Jorge").agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);

		// Act
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuário Negativado"));
			// Assert.assertTrue("Usuário Negativado".equals(e.getMessage()));
		}

		// Check
		Mockito.verify(spc).possuiNegativacao(usuario);
	}

	@Test
	public void testDeveEnviarEmailParaLocacoesAtrasadas() {
		// Assemble
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Jorge").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Kleber").agora();
		List<Locacao> locacoes = Arrays.asList(LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario).agora(),
				LocacaoBuilder.umLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora(),
				LocacaoBuilder.umLocacao().atrasada().comUsuario(usuario3).agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

		// Act
		service.notificarAtrasos();

		// Check
		Mockito.verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		Mockito.verify(emailService).notificarAtraso(usuario);
		Mockito.verify(emailService, Mockito.times(2)).notificarAtraso(usuario3);
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verifyNoMoreInteractions(emailService);
	}

	@Test
	public void testDeveTratarErroSPC() throws Exception {
		// Assemble
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Fala Grave"));

		// Check
		exception.expect(LocadoraException.class);
		exception.expectMessage("Probelmas com SPC, tente novamente");

		// Act
		service.alugarFilme(usuario, filmes);
	}

	@Test
	public void testDeveProrrogarUmaLocacao() {
		// Assemble
		Locacao locacao = LocacaoBuilder.umLocacao().agora();

		// Act
		service.prorrogarLocacao(locacao, 3);

		// Check
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();

		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDias(3));
	}

	@Test
	public void testDeveCalcularValorLocacao() throws Exception {
		// Assemble
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// Act
		Class<LocacaoService> claz = LocacaoService.class;
		Method metodo = claz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		Double valor = (Double) metodo.invoke(service, filmes);

		// Check
		Assert.assertThat(valor, is(4.0));
	}

}

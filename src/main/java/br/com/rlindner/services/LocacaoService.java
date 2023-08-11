package br.com.rlindner.services;

import static br.com.rlindner.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.rlindner.daos.LocacaoDAO;
import br.com.rlindner.entities.Filme;
import br.com.rlindner.entities.Locacao;
import br.com.rlindner.entities.Usuario;
import br.com.rlindner.exceptions.FilmeSemEstoqueException;
import br.com.rlindner.exceptions.LocadoraException;
import br.com.rlindner.utils.DataUtils;

public class LocacaoService {

	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws Exception {

		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme Vazio");
		}

		for (Filme filme : filmes) {
			if (filme.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}

		if (usuario == null) {
			throw new LocadoraException("Usuario Vazio");
		}

		try {
			if (spcService.possuiNegativacao(usuario)) {
				throw new LocadoraException("Usuário Negativado");
			}
		} catch (LocadoraException e) {
			throw new LocadoraException(e.getMessage());
		} catch (Exception e) {
			throw new LocadoraException("Probelmas com SPC, tente novamente");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(obterData());
		double valorTotal = calcularValorLocacao(filmes);
		locacao.setValor(valorTotal);

		// Entrega no dia seguinte
		Date dataEntrega = obterData();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}

	protected Date obterData() {
		return new Date();
	}

	private double calcularValorLocacao(List<Filme> filmes) {
		double valorTotal = 0d;
		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			switch (i) {
			case 2:
				valorFilme = valorFilme * 0.75;
				break;
			case 3:
				valorFilme = valorFilme * 0.50;
				break;
			case 4:
				valorFilme = valorFilme * 0.25;
				break;
			case 5:
				valorFilme = valorFilme * 0.00;
				break;
			}
			valorTotal += valorFilme;
		}
		return valorTotal;
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao : locacoes) {
			if (locacao.getDataRetorno().before(obterData())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(obterData());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor()* dias);
		dao.salvar(novaLocacao);
	}

}

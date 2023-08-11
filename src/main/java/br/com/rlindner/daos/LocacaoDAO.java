package br.com.rlindner.daos;

import java.util.List;

import br.com.rlindner.entities.Locacao;

public interface LocacaoDAO {

	public void salvar(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();
}

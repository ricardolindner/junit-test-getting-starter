package br.com.rlindner.builders;

import br.com.rlindner.entities.Usuario;

public class UsuarioBuilder {

	private Usuario usuario;

	private UsuarioBuilder() {
	}

	public static UsuarioBuilder umUsuario() {
		UsuarioBuilder builder = new UsuarioBuilder();
		builder.usuario = new Usuario();
		builder.usuario.setNome("Usuario 1");
		return builder;
	}
	
	public UsuarioBuilder comNome (String name) {
		usuario.setNome(name);
		return this;
	}
	
	public Usuario agora() {
		return usuario;
	}

}

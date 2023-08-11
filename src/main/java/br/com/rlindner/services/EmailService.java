package br.com.rlindner.services;

import br.com.rlindner.entities.Usuario;

public interface EmailService {
	
	public void notificarAtraso(Usuario usuario);

}

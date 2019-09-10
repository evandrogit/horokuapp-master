package com.webapp.repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.webapp.model.Usuario;
import com.webapp.util.jpa.Transacional;

public class Usuarios implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	public Usuario porId(Long id) {
		return this.manager.find(Usuario.class, id);
	}
	
	@Transacional
	public Usuario save(Usuario usuario) {
		return this.manager.merge(usuario);
	}

	@Transacional
	public void remove(Usuario usuario) {
		Usuario usuarioTemp = new Usuario();
		usuarioTemp = this.manager.merge(usuario);

		this.manager.remove(usuarioTemp);
	}

	public Usuario porLogin(String login) {
		Usuario usuario = null;
		
		try {
			usuario = this.manager.createQuery("from Usuario where lower(login) = :login", Usuario.class)
				.setParameter("login", login.toLowerCase()).getSingleResult();
		} catch (NoResultException e) {
			// nenhum usuário encontrado com o login informado
		}
		
		return usuario;
	}
	
}
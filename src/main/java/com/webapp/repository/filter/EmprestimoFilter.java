package com.webapp.repository.filter;

import java.io.Serializable;

public class EmprestimoFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome;
	private Boolean emprestimoVencido;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getEmprestimoVencido() {
		return emprestimoVencido;
	}

	public void setEmprestimoVencido(Boolean emprestimoVencido) {
		this.emprestimoVencido = emprestimoVencido;
	}

}
package com.webapp.controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.webapp.model.Despesa;
import com.webapp.repository.Despesas;
import com.webapp.repository.filter.DespesaFilter;
import com.webapp.util.jsf.FacesUtil;

@Named
@ViewScoped
public class LancamentoDespesaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Despesas despesas;

	@Inject
	private Despesa despesa;

	private List<Despesa> todosLacamentos;

	private Despesa despesaSelecionada;

	private DespesaFilter filtro = new DespesaFilter();
	
	private static final Locale BRAZIL = new Locale("pt", "BR");

	private static final DecimalFormatSymbols REAL = new DecimalFormatSymbols(BRAZIL);
	
	private NumberFormat nf = new DecimalFormat("###,##0.00", REAL);

	private String totalDespesas;

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			pesquisar();
			totalDespesas();
		}
	}

	public void prepararNovoLancamento() {
		despesa = new Despesa();
	}

	public void prepararEditarLancamento() {
		despesa = despesaSelecionada;
	}

	public void salvar() {

		despesas.save(despesa);

		despesaSelecionada = null;

		pesquisar();
		
		totalDespesas();

		PrimeFaces.current().executeScript(
				"PF('downloadLoading').hide(); swal({ type: 'success', title: 'Concluído!', text: 'Despesa registrada com sucesso!' });");
	}

	public void excluir() {

		despesas.remove(despesaSelecionada);

		despesaSelecionada = null;

		pesquisar();
		
		totalDespesas();

		PrimeFaces.current().executeScript(
				"swal({ type: 'success', title: 'Concluído!', text: 'Despesa excluída com sucesso!' });");

	}

	public void pesquisar() {
		todosLacamentos = despesas.filtrados(filtro);
		
		for (Despesa despesa : todosLacamentos) {
			despesa.setValorTemp(nf.format(despesa.getValor().doubleValue()));
		}
		
		despesaSelecionada = null;
	}

	/*
	private void listarTodos() {
		todosLacamentos = despesas.filtrados(filtro);
	}
	*/
	
	public void totalDespesas() {
		
		if(despesas.totalDespesas() != null) {
			totalDespesas = nf.format(despesas.totalDespesas().doubleValue());
			
		} else {
			totalDespesas = "0,00";
		}
		
	}

	public List<Despesa> getTodosLancamentos() {
		return todosLacamentos;
	}

	public DespesaFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(DespesaFilter filtro) {
		this.filtro = filtro;
	}

	public Despesa getDespesaSelecionada() {
		return despesaSelecionada;
	}

	public void setDespesaSelecionada(Despesa despesaSelecionada) {
		this.despesaSelecionada = despesaSelecionada;
	}

	public Despesa getDespesa() {
		return despesa;
	}

	public void setDespesa(Despesa despesa) {
		this.despesa = despesa;
	}

	public String getTotalDespesas() {
		return totalDespesas;
	}

}
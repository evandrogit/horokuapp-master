package com.webapp.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import com.webapp.model.Cliente;
import com.webapp.model.Emprestimo;
import com.webapp.model.Parcela;
import com.webapp.repository.Emprestimos;
import com.webapp.repository.Parcelas;
import com.webapp.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroEmprestimoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Emprestimos emprestimos;

	@Inject
	private Parcelas parcelas;

	//@Inject
	private Emprestimo emprestimo = new Emprestimo();

	public void inicializar() {
		if (FacesUtil.isNotPostback()) {

		}
	}
	
	public void buscar() {
		emprestimo = emprestimos.porId(emprestimo.getId());
	}

	public void definirCliente(Cliente cliente) {
		emprestimo = new Emprestimo();
		emprestimo.setCliente(cliente);
	}

	public void salvar() {

		emprestimo.setProximoVencimento(emprestimo.getDataVencimento());
		BigDecimal valorEmprestado = BigDecimal.valueOf(Double.parseDouble(emprestimo.getValorEmprestimo()));
		BigDecimal emprestimoComJuros = valorEmprestado
				.multiply(BigDecimal.valueOf((((double) emprestimo.getPercentualJuros() / 100) + 1)));

		emprestimo.setDebito(valorEmprestado);
		emprestimo.setJuros(emprestimoComJuros.add(valorEmprestado.multiply(BigDecimal.valueOf(-1))));
		emprestimo.setTotal(emprestimoComJuros);
		emprestimo.setTotalPago(BigDecimal.valueOf(0));
		emprestimo.setSaldoDevedorInicial(emprestimoComJuros);
		emprestimo.setJurosInicial(emprestimo.getJuros());
		emprestimo.setJurosFinal(BigDecimal.valueOf(0));

		if (emprestimo.getId() == null) {

			emprestimo = emprestimos.save(emprestimo);

			Calendar calendar = Calendar.getInstance();

			int size = String.valueOf(emprestimo.getId()).length();

			switch (size) {
			case 1:
				emprestimo.setContrato("SB-000");
				break;

			case 2:
				emprestimo.setContrato("SB-00");
				break;

			case 3:
				emprestimo.setContrato("SB-0");
				break;

			default:
				emprestimo.setContrato(String.valueOf(emprestimo.getId()));
				break;
			}

			emprestimo.setContrato(emprestimo.getContrato().concat(String.valueOf(emprestimo.getId())) + "/"
					+ calendar.get(Calendar.YEAR));
			emprestimo = emprestimos.save(emprestimo);

			// emprestimo = new Emprestimo();

			PrimeFaces.current().executeScript(
					"swal({ type: 'success', title: 'Concluído!', text: 'Empréstimo registrado com sucesso!' });");
		
		} else {
			
			List<Parcela> todasParcelas = parcelas.todasParcelas(emprestimo.getId());

			if (todasParcelas.size() > 0) {
				PrimeFaces.current().executeScript(
						"swal({ type: 'error', title: 'Erro!', text: 'Já existem parcelas lançadas para esse contrato.' });");
				
			} else {
				emprestimo = emprestimos.save(emprestimo);
				PrimeFaces.current().executeScript(
						"swal({ type: 'success', title: 'Concluído!', text: 'Empréstimo atualizado com sucesso!' });");
			}
			
		}

	}

	public Emprestimo getEmprestimo() {
		return emprestimo;
	}

	public void setEmprestimo(Emprestimo emprestimo) {
		this.emprestimo = emprestimo;
	}

}

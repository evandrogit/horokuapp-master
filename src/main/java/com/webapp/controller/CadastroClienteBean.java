package com.webapp.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webapp.model.Anexo;
import com.webapp.model.Cliente;
import com.webapp.model.Emprestimo;
import com.webapp.model.Parcela;
import com.webapp.repository.Clientes;
import com.webapp.repository.Emprestimos;
import com.webapp.repository.Parcelas;
import com.webapp.repository.filter.ClienteFilter;
import com.webapp.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroClienteBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;

	@Inject
	private Cliente cliente;
	
	@Inject
	private Emprestimos emprestimos;
	
	@Inject
	private Parcelas parcelas;

	private List<Cliente> todosClientes;

	private Cliente clienteSelecionado;

	private ClienteFilter filtro = new ClienteFilter();
	
	private List<Anexo> anexos = new ArrayList<Anexo>();
	
	private UploadedFile file;
	 
 
	public void inicializar() {
		if (FacesUtil.isNotPostback()) {
			listarTodos();
		}
	}

	public void prepararNovoCadastro() {
		cliente = new Cliente();
	}

	public void prepararEditarCadastro() {
		cliente = clienteSelecionado;
		anexos = cliente.getAnexos();
	}

	public void salvar() {

		clientes.save(cliente);

		clienteSelecionado = null;

		listarTodos();

		PrimeFaces.current().executeScript(
				"PF('downloadLoading').hide(); swal({ type: 'success', title: 'Concluído!', text: 'Cliente salvo com sucesso!' });");
	}

	public void excluir() {
		
		List<Emprestimo> emprestimos = this.emprestimos.porCliente(clienteSelecionado.getId());
		
		for (Emprestimo emprestimo : emprestimos) {
			
			List<Parcela> listaParcelas = parcelas.todasParcelas(emprestimo.getId());
			
			if(listaParcelas.size() > 0) {
				for (Parcela parcela : listaParcelas) {
					parcelas.remove(parcela);
				}
			}

			this.emprestimos.remove(emprestimo);
		}
		
		clientes.remove(clienteSelecionado);

		clienteSelecionado = null;

		pesquisar();

		PrimeFaces.current().executeScript(
				"swal({ type: 'success', title: 'Concluído!', text: 'Cliente excluído com sucesso!' });");

	}

	public void pesquisar() {
		todosClientes = clientes.filtrados(filtro);
		clienteSelecionado = null;
	}

	private void listarTodos() {
		todosClientes = clientes.todos();
	}

	public List<Cliente> getTodosClientes() {
		return todosClientes;
	}

	public ClienteFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ClienteFilter filtro) {
		this.filtro = filtro;
	}

	public Cliente getClienteSelecionado() {
		return clienteSelecionado;
	}

	public void setClienteSelecionado(Cliente clienteSelecionado) {
		this.clienteSelecionado = clienteSelecionado;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public List<Anexo> getAnexos() {
		return anexos;
	}
	
	public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public void upload() {
		
		Anexo anexo = new Anexo();
		anexo.setFile(file.getContents());
		anexo.setFileName(file.getFileName());
		anexo.setCliente(cliente);
		
		anexos.add(anexo);
		
		cliente.setAnexos(anexos);
		
		clientes.save(cliente);
		
		anexos = cliente.getAnexos();

		FacesUtil.addInfoMessage("Arquivo anexado com sucesso!");
	}
	
	public void removerAnexo(Anexo anexo) {
		anexos.remove(anexo);
		cliente.setAnexos(anexos);
		
		clientes.save(cliente);
	}
	
	public StreamedContent download(Anexo anexo) {
		
		InputStream is = new ByteArrayInputStream(anexo.getFile());	   
	    StreamedContent stream = new DefaultStreamedContent(is, "file", anexo.getFileName());
	    
	    return stream;
	}

}
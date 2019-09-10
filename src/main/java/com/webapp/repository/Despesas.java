package com.webapp.repository;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import com.webapp.model.Despesa;
import com.webapp.repository.filter.DespesaFilter;
import com.webapp.util.jpa.Transacional;

public class Despesas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	public Despesa porId(Long id) {
		return this.manager.find(Despesa.class, id);
	}

	@Transacional
	public Despesa save(Despesa despesa) {
		return this.manager.merge(despesa);
	}

	@Transacional
	public void remove(Despesa despesa) {
		Despesa despesaTemp = new Despesa();
		despesaTemp = this.manager.merge(despesa);

		this.manager.remove(despesaTemp);
	}

	public List<Despesa> todos() {
		return this.manager.createQuery("from Despesa order by id", Despesa.class).getResultList();
	}

	public List<Despesa> filtrados(DespesaFilter filter) {
		
		TypedQuery<Despesa> typedQuery;

		if (StringUtils.isNotBlank(filter.getItem())) {
			typedQuery = manager.createQuery("from Despesa d where d.item like :item order by d.data desc", Despesa.class)
					.setParameter("item", "%" + filter.getItem() + "%");

		} else {
			typedQuery = manager.createQuery("select d from Despesa d order by d.data desc", Despesa.class);
		}

		return typedQuery.getResultList();
	}
	
	public Number totalDespesas() {		
		String jpql = "SELECT sum(d.valor) FROM Despesa d";
		Query q = manager.createQuery(jpql);
		Number count = (Number) q.getSingleResult();
		
		return count;
	}
}
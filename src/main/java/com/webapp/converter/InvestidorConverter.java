package com.webapp.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.webapp.model.Investidor;
import com.webapp.repository.Investidores;
import com.webapp.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Investidor.class)
public class InvestidorConverter implements Converter {

	private Investidores investidores = CDIServiceLocator.getBean(Investidores.class);;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Investidor retorno = null;

		if (value != null) {
			retorno = this.investidores.porId(new Long(value));
		}

		return retorno;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			Long codigo = ((Investidor) value).getId();
			String retorno = (codigo == null ? null : codigo.toString());

			return retorno;
		}

		return "";
	}
}
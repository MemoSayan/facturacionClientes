package mx.com.spring.app.view.csv;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.xml.MarshallingView;

import mx.com.spring.app.models.entity.Cliente;
import mx.com.spring.app.view.xml.ClienteList;

@Component("listar.xml")
public class ClienteListXmlView extends MarshallingView {
	
	
	@Autowired
	public ClienteListXmlView(Jaxb2Marshaller marshaller) {
		super(marshaller);
		
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		model.remove("titulo");
		model.remove("page");
		Page<Cliente> clientes = (Page<Cliente>) model.get("clientes");
		model.remove("cliente");
		model.put("clienteList", new ClienteList(clientes.getContent()));
		
		
		super.renderMergedOutputModel(model, request, response);
	}
	

}

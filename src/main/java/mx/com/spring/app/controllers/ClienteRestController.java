package mx.com.spring.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import mx.com.spring.app.models.entity.Cliente;
import mx.com.spring.app.models.service.IClienteService;
import mx.com.spring.app.view.xml.ClienteList;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {
	@Autowired
	private IClienteService clienteService;
	
	@RequestMapping(value = {"/listar-rest"}, method = RequestMethod.GET)
	public ClienteList listarRest() {
		return new ClienteList(clienteService.findAll());
	}
	
}

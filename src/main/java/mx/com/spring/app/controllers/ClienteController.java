package mx.com.spring.app.controllers;

import java.lang.ProcessBuilder.Redirect;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import mx.com.spring.app.models.dao.IClienteDao;
import mx.com.spring.app.models.entity.Cliente;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	@Qualifier("clienteDaoJPA") // se indica el nombre del componente o bean concreto
	private IClienteDao clienteDao;

	// Metodo para listar clientes
	@RequestMapping(value = "listar", method = RequestMethod.GET)
	public String listar(Model model) {
		model.addAttribute("titulo", "Listado de Clientes");
		model.addAttribute("clientes", clienteDao.findAll());
		return "listar";
	}
	/*
	 * Metodo que muestra el formulario,
	 */

	@RequestMapping(value = "/form")
	public String Crear(Map<String, Object> model) {

		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}

	/*
	 * Este metodo retorna la pantalla editar
	 */

	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model) {
		Cliente cliente = null;
		if (id > 0) { // validamos que exista el id
			cliente = clienteDao.findOne(id);
		} else {
			return "redirect:/listar";
		}
		model.put("cliente", cliente);// pasamos datos a la vista
		model.put("titulo", "Editar Cliente");
		return "form";
	}

	/*
	 * metodo para guardar el cliente, valida los campos y retorna en caso de error
	 * 
	 * @ModelAttribute("cliente") es para indicar explicitamente el nombre a la
	 * clase, en este caso es redundante
	 */

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, 
			BindingResult result, Model model,
			SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de cliente");
			return "form";
		}
		clienteDao.save(cliente);
		status.setComplete();
		return "redirect:listar";
	}
	
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id){
		if (id > 0) { // validamos que exista el id
			clienteDao.delete(id);
		} else {
			return "redirect:/listar";
		}
		return "redirect:/listar";
	}
}

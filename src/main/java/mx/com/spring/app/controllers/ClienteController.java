package mx.com.spring.app.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mx.com.spring.app.controllers.util.paginator.PageRender;
import mx.com.spring.app.models.entity.Cliente;
import mx.com.spring.app.models.service.IClienteService;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	// @Qualifier("clienteDaoJPA") // se indica el nombre del componente o bean
	// concreto
	private IClienteService clienteService;

	// Metodo para listar clientes
	@RequestMapping(value = "listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Cliente> clientes = clienteService.findALL(pageRequest);// paginacion de la lista
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("titulo", "Listado de Clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
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
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = null;
		if (id > 0) { // validamos que exista el id
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("warning", "El id no existe");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El id no puede ser 0");
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
	public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result, Model model,
		@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de cliente");
			return "form";
		}
		if(!foto.isEmpty()) {
			 Path directorioRecursos = Paths.get(null, null)
		}
		String mensajeflash = cliente.getId() != null ? "Cliente editado con exito" : "Cliente creado con Exito!";
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeflash);
		return "redirect:listar";
	}
	

	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) { // validamos que exista el id
			clienteService.delete(id);
			flash.addFlashAttribute("info", "Cliente eliminado con Exito!");
		} else {
			return "redirect:/listar";
		}
		return "redirect:/listar";
	}
}

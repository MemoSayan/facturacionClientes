package mx.com.spring.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import mx.com.spring.app.controllers.util.paginator.PageRender;
import mx.com.spring.app.models.entity.Cliente;
import mx.com.spring.app.models.service.IClienteService;
import mx.com.spring.app.models.service.IUploadFileService;
 
@Controller
@SessionAttributes("cliente")
public class ClienteController {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final static String UPLOADS_FOLDER = "uploads";

	@Autowired
	// @Qualifier("clienteDaoJPA") // se indica el nombre del componente o bean
	// concreto
	private IClienteService clienteService;

	@Autowired
	private IUploadFileService uploadsFileService;

	@Secured("ROLE_USER")
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		// inyectamos el metodo load
		Resource recurso = null;
		try {
			recurso = uploadsFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = clienteService.fetchByIdWFacturas(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Detalle del Cliente: " + cliente.getNombre());
		return "ver";
	}

	// Metodo para listar clientes
	@RequestMapping(value = {"listar","/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") 
	int page, Model model , Authentication authentication, HttpServletRequest request) {
		
		if(authentication != null) {
		log.info("usuario autenticado , el username es: ".concat(authentication.getName()));
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth != null) {
			log.info("Utilizando forma estatica::.. " .concat(auth.getName()));
		}
	
		if(hasRole("ROLE_ADMIN")) {
			log.info("..::username: ".concat(auth.getName()).concat(" Tienes acceso!"));
		}else {
			log.info("..::username: ".concat(auth.getName()).concat(" No Tienes acceso!"));
		}
		
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
		
		if(securityContext.isUserInRole("ADMIN")) {
			log.info("..::forma usando la clase securitycontexHolder...");
		}
		
		if(request.isUserInRole("ROLE_ADMIN")) {
			log.info("..::forma usando la clase HttpServletRe...");
		}
		
		
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
	@Secured("ROLE_ADMIN")
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

	@PreAuthorize("hasRole('ROLE_ADMIN')")
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
	 * clase, en este caso es redundante guarda la foto en el directorio asignado
	 */

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de cliente");
			return "form";
		}
		if (!foto.isEmpty()) {

			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				uploadsFileService.delete(cliente.getFoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadsFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flash.addFlashAttribute("info", "La imagen se ha guardado correctamente '" + uniqueFilename + "'");
			cliente.setFoto(uniqueFilename);
		}

		String mensajeflash = cliente.getId() != null ? "Cliente editado con exito" : "Cliente creado con Exito!";
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeflash);
		return "redirect:listar";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) { // validamos que exista el id
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute("info", "Cliente eliminado con Exito!");

			if (uploadsFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + "eliminado con exito");
			}
		} else {
			return "redirect:/listar";
		}
		return "redirect:/listar";
	}
	
	private boolean hasRole(String role) {
		
		SecurityContext context = SecurityContextHolder.getContext();
		if(context == null) {
			return false;
		}
		
		Authentication auth = context.getAuthentication();
		
		if(auth == null) {
			return false;
		}
	
	 Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
	/*	for(GrantedAuthority authority: authorities) {
			if(role.equals(authority.getAuthority())) {
				log.info("..:: Hey usuario: ".concat(auth.getName()).concat(" Tu role es ".concat(authority.getAuthority())));
				return true;
			}
		}
		return false; */
	 return authorities.contains(new SimpleGrantedAuthority(role));
	}

}




















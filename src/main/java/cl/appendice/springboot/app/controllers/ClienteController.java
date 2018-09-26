package cl.appendice.springboot.app.controllers;


import java.io.IOException;
import java.net.MalformedURLException;

import java.util.Map;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.appendice.springboot.app.models.entity.Cliente;
import cl.appendice.springboot.app.models.service.IClienteService;
import cl.appendice.springboot.app.models.service.IUploadFileService;
import cl.appendice.springboot.app.util.Common;
import cl.appendice.springboot.app.util.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@SuppressWarnings("unused")
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	
	@GetMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		
		Resource recurso = null;
		
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename: \""+recurso.getFilename()+"\"").body(recurso);
	}
	
	@GetMapping(value="/ver/{id}")
	public String ver(@PathVariable(value="id") Long id,Model model,RedirectAttributes flash) {
		
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);
		
		if(cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos.");
			return "redirect:/listar";
		}
		
		model.addAttribute("titulo", "Detalle cliente: "+cliente.getNombre());
		model.addAttribute("cliente", cliente);
		
		return "ver";
	}
		
	@RequestMapping(value="/listar",method= RequestMethod.GET)
	public String listar(@RequestParam(name="page",defaultValue = "0") String page,Model model) {
		
		if(!Common.isNumeric(page)) {
			return "redirect:listar";
		}
		
		int paginaActual = Integer.parseInt(page);
		
		if( paginaActual < 0) {
			return "redirect:listar";
		}
		
		Pageable pageRequest = new PageRequest(paginaActual,4);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		if( paginaActual >=  pageRender.getTotalPaginas()){
			return "redirect:listar";
		}
		model.addAttribute("page", pageRender);
		return "listar";	
	}
	
	@RequestMapping(value="/form")
	public String crear(Map<String,Object> model) {
		
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario cliente");
		model.put("nombreBoton", "Crear Cliente");
		return "form";	
	}
	
	@RequestMapping(value="/form/{id}")
	public String editar(@PathVariable(value="id") Long id,RedirectAttributes flash, Map<String,Object> model) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = clienteService.findOne(id);
			
			if (cliente == null) {		
				flash.addFlashAttribute("error", "El ID del cliente no existe.");
				return "redirect:/listar";
			}
		}else{
			flash.addFlashAttribute("error", "El ID del cliente no puede ser 0 u otro valor.");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		model.put("nombreBoton", "Editar Cliente");
		return "form";	
	}
	
	
	@RequestMapping(value="/form",method= RequestMethod.POST)
	public String guardar(@Valid Cliente cliente,BindingResult result,Model model,@RequestParam("file") MultipartFile file,RedirectAttributes flash, SessionStatus session) {
		
		if (result.hasErrors()) {
			String mensajeBoton = (cliente.getId() != null && cliente.getId() > 0) ? "Editar cliente" : "Crear cliente";
			
			model.addAttribute("titulo", "Formulario cliente");
			model.addAttribute("nombreBoton", mensajeBoton);
			return "form";
		}
		
		if(!file.isEmpty()) {
			
			if(cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null && cliente.getFoto().length() > 0) {
				uploadFileService.delete(cliente.getFoto());
			}
			
			String uniqueFilename = null;
			
			try {
				uniqueFilename = uploadFileService.copy(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			flash.addFlashAttribute("info", "Se ha subido correctamente el archivo "+"("+uniqueFilename+")");
			cliente.setFoto(uniqueFilename);
			
		}
		
		String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con exito" : "Cliente creado con exito";	
	
		this.clienteService.save(cliente);
		session.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";	
	}
	
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,RedirectAttributes flash,Map<String,Object> model) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con exito");
			
			if(uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", "Foto: "+cliente.getFoto() + " eliminada con exito.");
			}
		}
		return "redirect:/listar";		
	}
}

/**
 * 
 */
package com.social.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.social.entidades.Publicacion;
import com.social.servicios.PublicacionService;
import com.social.servicios.UsuarioService;

/**
 * <h1>Panel Controller</h1> 
 * 
 * Controlador que se encarga de dar respuesta a las
 * peticiones correspondientes a el panel de la aplicacion
 * 
 * @author Antonio Paya Gonzalez
 * @author Pablo Diaz Rancaño
 *
 */
@Controller
public class PanelController {
	private UsuarioService usuarioService;
	private PublicacionService postService;

	@Autowired
	public PanelController(UsuarioService usuarioService, PublicacionService postService) {
		this.usuarioService = usuarioService;
		this.postService = postService;
	}
	
	@GetMapping(value = { "/" })
	public String home(Model model,Pageable pageable,@RequestParam(value = "", required=false) String searchText) {
		Page<Publicacion> publicaciones;
		if (searchText != null && !searchText.isEmpty()) {
			publicaciones = postService.buscarPostPorTituloYContenido(pageable, searchText);
		}
		else {
			publicaciones = postService.getPublicacionesAmigos(pageable,usuarioService.getUsuarioActivo());
		}
		model.addAttribute("usuarioActivo", usuarioService.getUsuarioActivo());
		model.addAttribute("listPost",publicaciones.getContent());
		model.addAttribute("page", publicaciones);
		return "/panel";
	}
	
	@GetMapping(value = "/error")
	public String loginError(Model model) {
		return "/error";
	}
}
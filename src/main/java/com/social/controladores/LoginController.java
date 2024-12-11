/**
 * 
 */
package com.social.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.social.entidades.Usuario;
import com.social.servicios.SecurityService;
import com.social.servicios.UsuarioService;
import com.social.validadores.RegistroValidator;

/**
 * <h1>Login Controller</h1> Controlador que se encarga de dar respuesta a las
 * peticiones de Login y Register
 * 
 * @author Antonio Paya Gonzalez
 * @author Pablo Diaz Rancaño
 *
 */
@Controller
public class LoginController {
	private UsuarioService usuarioService;
	private SecurityService securityService;
	private RegistroValidator registroValidator;

	@Autowired
	public LoginController(UsuarioService usuarioService, SecurityService securityService, RegistroValidator registroValidator) {
		this.usuarioService = usuarioService;
		this.securityService = securityService;
		this.registroValidator = registroValidator;
	}
	
	@GetMapping(value = "/registro")
	public String registro(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "login/registro";
	}
	
	@PostMapping(value = "/registro")
	public String registro(@ModelAttribute @Validated Usuario usuario,BindingResult result,Model model) {
		registroValidator.validate(usuario, result);
		if(result.hasErrors()) {
			return "login/registro";
		}
		usuarioService.addNuevoUsuario(usuario);
		securityService.autoLogin(usuario.getUsername(), usuario.getPasswordConfirm());
		model.addAttribute("usuarioActivo", usuarioService.getUsuarioActivo());
		return "redirect:/";
	}

	@GetMapping(value = "/login")
	public String login(Model model) {
		return "/login/login";
	}

	@GetMapping(value = "/login/error")
	public String loginError(Model model) {
		return "/login/error";
	}
}
/**
 * 
 */
package com.social.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.social.entidades.Publicacion;
import com.social.entidades.Usuario;
import com.social.servicios.RolesService;
import com.social.servicios.SecurityService;
import com.social.servicios.UsuarioService;

/**
 * <h1>Login Controller</h1> Controlador que se encarga de dar respuesta a las
 * peticiones de Administrador
 * 
 * @author Antonio Paya Gonzalez
 * @author Pablo Diaz Rancaño
 *
 */
@Controller
public class AdminController {
	private UsuarioService usersService;
	private RolesService rolesService;
	private SecurityService securityService;

	@Autowired
	public AdminController(UsuarioService usersService, RolesService rolesService, SecurityService securityService) {
		this.usersService = usersService;
		this.rolesService = rolesService;
		this.securityService = securityService;
	}
	
	@RequestMapping("/admin/list")
	public String getList(Model model, Pageable pageable, @RequestParam(value = "", required=false) String searchText)
	{
		Usuario usuarioActivo = usersService.getUsuarioActivo();
		List<Usuario> adminUsers;
		
		if (searchText != null && !searchText.isEmpty()) {
			adminUsers = usersService.buscarUsuariosPorNombreOEmail(pageable, searchText).getContent();
		}
		else {
			adminUsers = usersService.getUsuarios(pageable).getContent();
		}
		adminUsers = adminUsers.stream().filter(x -> x.getId().equals(usuarioActivo.getId())).collect(Collectors.toList());
		Page<Usuario> usuarios = new PageImpl<>(adminUsers);
		model.addAttribute("usuarioActivo", usuarioActivo);
		model.addAttribute("userList", usuarios.getContent());
		model.addAttribute("page", usuarios);
		return "/admin/list";
		
	}
	
	@RequestMapping(value = "/admin/eliminarUsuario/{id}")
	public String deleteUser(Model model, @ModelAttribute Publicacion form, @PathVariable Long id) {
		Usuario activo = usersService.getUsuarioActivo();
		usersService.deleteUsuario(id);
		model.addAttribute("usuarioActivo", activo);
		return "redirect:/admin/list";
	}
	
	@GetMapping(value = "/admin/login")
	public String login(Model model) {
		return "/admin/login";
	}
	
	@PostMapping(value = "/admin/login")
	public String login(Model model,@ModelAttribute Usuario u) {
		String username = u.getUsername();
		String passwd = u.getPassword();
		Usuario intento = usersService.getUserByUsername(username);
		if(intento == null || !intento.getRole().equals(rolesService.getRoles()[1]))
			return "redirect:/login/error";
		securityService.autoLogin(username, passwd);
		return "redirect:/admin/list";
	}
}
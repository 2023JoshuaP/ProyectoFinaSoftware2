package com.social.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.social.entidades.Usuario;
import com.social.servicios.UsuarioService;

/**
 * <h1>Users Controller</h1> Controlador que se encarga de dar respuesta a las
 * peticiones relacionadas con usuarios
 * 
 * @author Antonio Paya Gonzalez
 * @author Pablo Diaz Rancaño
 *
 */
@Controller
public class UsersController 
{
	private static final String USUARIO_ACTIVO = "usuarioActivo";
	private static final String USER_LIST = "userList";

	@Autowired 
	private UsuarioService usersService;
	
	@RequestMapping("/users/lista-usuarios")
	public String getList(Model model, Pageable pageable, @RequestParam(value = "", required=false) String searchText)
	{	
		Page<Usuario> usuarios;
		Usuario usuarioActivo = usersService.getUsuarioActivo();
		
		if (searchText != null && !searchText.isEmpty()) {
			usuarios = usersService.buscarUsuariosPorNombreOEmail(pageable, searchText);
		}
		else {
			usuarios = usersService.getUsuarios(pageable);
		}
		model.addAttribute(USUARIO_ACTIVO, usuarioActivo);
		model.addAttribute(USER_LIST, usuarios.getContent());
		model.addAttribute("usuariosPeticionesEnviadas", usersService.getPeticionesEnviadas( usuarioActivo ));
		model.addAttribute("page", usuarios);
		return "/users/lista-usuarios";
	}
	
	@RequestMapping("/users/perfil/{username}")
	public String getPerfil(Model model,@PathVariable String username)
	{	
		model.addAttribute("usuario", usersService.getUserByUsername(username));
		model.addAttribute(USUARIO_ACTIVO, usersService.getUsuarioActivo());
		return "/users/perfil";
		
	}
	
	@PostMapping(value="/user/edit")
	public String editUser(Model model,  @ModelAttribute Usuario form)
	{	
		Usuario activo = usersService.getUsuarioActivo();
		if(form.getNombre() != null && !form.getNombre().isEmpty())
			activo.setNombre(form.getNombre());
		if(form.getApellidos() != null && !form.getApellidos().isEmpty())
			activo.setApellidos(form.getApellidos());
		if(form.getEmail() != null && !form.getEmail().isEmpty())
			activo.setEmail(form.getEmail());
		if(form.getDescripcion() != null && !form.getDescripcion().isEmpty())
			activo.setDescripcion(form.getDescripcion());
		
		usersService.addUsuario(activo);
		model.addAttribute("usuario", activo);
		model.addAttribute(USUARIO_ACTIVO, activo);
		return "/users/perfil";
		
	}
	
	@RequestMapping("/users/lista-amigos")
	public String getAmigos(Model model, Pageable pageable, @RequestParam(value = "", required=false) String searchText)
	{	
		Page<Usuario> usuarios;
		Usuario usuarioActivo = usersService.getUsuarioActivo();
		
		if (searchText != null && !searchText.isEmpty()) {
			usuarios = usersService.buscarUsuariosAmigosPorNombreOEmail(pageable, usuarioActivo, searchText);
			
		}
		else {
			usuarios = usersService.getUsuariosAmigos(pageable,usersService.getUsuarioActivo());
		}
		model.addAttribute(USUARIO_ACTIVO, usersService.getUsuarioActivo());
		model.addAttribute(USER_LIST, usuarios.getContent());
		model.addAttribute("page", usuarios);
		return "/users/lista-amigos";
		
	}
	
	
	@RequestMapping("/users/enviarAmistad/{id}")
	public String addPeticionAmistad(Model model, @PathVariable long id, Pageable pageable, @RequestParam(value = "", required=false) String searchText)
	{	
		Usuario u1 = usersService.getUsuarioActivo();
		Usuario u2 = usersService.getUsuario( id );
		usersService.addPeticionAmistad(u1, u2);
		
		Page<Usuario> usuarios;
		usuarios = usersService.buscarUsuariosPorNombreOEmail(pageable, searchText);
		
		model.addAttribute(USUARIO_ACTIVO, u1);
		model.addAttribute(USER_LIST, usuarios.getContent());
		model.addAttribute("page", usuarios);
		return "redirect:/users/lista-usuarios";
	}
	
	@RequestMapping("/users/aceptarPeticion/{id}")
	public String aceptarPeticion(Model model, @PathVariable long id, Pageable pageable)
	{	
		Usuario u1 = usersService.getUsuarioActivo();
		Usuario u2 = usersService.getUsuario( id );
		usersService.aceptarPeticionAmistad(u1, u2);
		
		return "redirect:/users/lista-peticiones";
	}
	
	@RequestMapping("/users/rechazarPeticion/{id}")
	public String rechazarPeticion(Model model, @PathVariable long id, Pageable pageable)
	{	
		Usuario u1 = usersService.getUsuarioActivo();
		Usuario u2 = usersService.getUsuario( id );
		usersService.rechazarPeticionAmistad(u1, u2);
		
		return "redirect:/users/lista-peticiones";
	}
	
	
	@RequestMapping("/users/lista-peticiones")
	public String getPeticiones(Model model, Pageable pageable)
	{	
		Page<Usuario> usuarios;
		
		Usuario usuarioActivo = usersService.getUsuarioActivo();
		
		usuarios = usersService.buscarPeticionesAmistad( pageable, usuarioActivo );
		
		model.addAttribute(USUARIO_ACTIVO, usersService.getUsuarioActivo());
		model.addAttribute(USER_LIST, usuarios.getContent());
		model.addAttribute("page", usuarios);
		return "/users/peticiones-amistad";
	}
}
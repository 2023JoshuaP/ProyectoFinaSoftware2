package com.social.controladores;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.social.entidades.Publicacion;
import com.social.entidades.Usuario;
import com.social.servicios.PublicacionService;
import com.social.servicios.UsuarioService;

/**
 * <h1>Post Controller</h1> Controlador que se encarga de dar respuesta a las
 * peticiones de Publicaciones
 * 
 * @author Antonio Paya Gonzalez
 * @author Pablo Diaz Rancaño
 *
 */
@Controller
public class PostController {
	private static final String USUARIO_ACTIVO = "usuarioActivo";
	private static final String REDIRECT_POST_LIST = "redirect:/post/list";

	private PublicacionService postService;
	private UsuarioService usersService;

	@Autowired
	public PostController(PublicacionService postService, UsuarioService usersService) {
		this.postService = postService;
		this.usersService = usersService;
	}

	@RequestMapping("/post/add")
	public String addPost(Model model) {
		model.addAttribute(USUARIO_ACTIVO, usersService.getUsuarioActivo());
		return "/post/crear-post";
	}

	@PostMapping(value = "/post/add")
	public String addPost(Model model, @RequestParam("titulo") String titulo,
			@RequestParam("contenido") String contenido, @RequestParam("imagen") MultipartFile imagen) {
		Usuario activo = usersService.getUsuarioActivo();
		Publicacion post = new Publicacion();
		post.setAutor(activo);
		post.setContenido(contenido);
		post.setTitulo(titulo);
		post.setFecha(new Date());
		activo.addPost(post);
		postService.addPublicacion(post);
		usersService.addUsuario(activo);
		String rutaImagen = postService.addImagen(imagen, post);
		post.setImagen(rutaImagen);
		postService.addPublicacion(post);
		model.addAttribute("usuario", activo);
		model.addAttribute(USUARIO_ACTIVO, usersService.getUsuarioActivo());
		return REDIRECT_POST_LIST;
	}

	@RequestMapping(value = "/post/list")
	public String getPostList(Model model) {
		Usuario activo = usersService.getUsuarioActivo();
		model.addAttribute(USUARIO_ACTIVO, activo);
		return "post/list";
	}

	@RequestMapping(value = "/post/edit/{id}")
	public String editPost(Model model, @PathVariable Long id) {
		model.addAttribute("post", postService.getPublicacion(id));
		model.addAttribute(USUARIO_ACTIVO, usersService.getUsuarioActivo());
		return "/post/editar-post";
	}

	@PostMapping(value = "/post/edit/{id}")
	public String editPost(Model model, @ModelAttribute Publicacion form, @PathVariable Long id) {
		Usuario activo = usersService.getUsuarioActivo();
		Publicacion post = postService.getPublicacion(id);
		if (form.getTitulo() != null && !form.getTitulo().trim().isEmpty())
			post.setTitulo(form.getTitulo());
		if (form.getContenido() != null && !form.getContenido().trim().isEmpty())
			post.setContenido(form.getContenido());

		postService.addPublicacion(post);
		model.addAttribute(USUARIO_ACTIVO, activo);
		return REDIRECT_POST_LIST;
	}

	@RequestMapping(value = "/post/delete/{id}")
	public String deletePost(Model model, @ModelAttribute Publicacion form, @PathVariable Long id) {
		Usuario activo = usersService.getUsuarioActivo();
		postService.deletePublicacion(id);
		model.addAttribute(USUARIO_ACTIVO, activo);
		return REDIRECT_POST_LIST;
	}
	
	@RequestMapping(value="/post/like/{id}")
	public String addLike(Model model,@PathVariable Long id){
		Publicacion post = postService.getPublicacion(id);
		Usuario activo = usersService.getUsuarioActivo();
		post.addLike(activo);
		activo.addLike(post);
		
		postService.addPublicacion(post);
		usersService.addUsuario(activo);
		model.addAttribute(USUARIO_ACTIVO, activo);
		return "redirect:/";
	}
}
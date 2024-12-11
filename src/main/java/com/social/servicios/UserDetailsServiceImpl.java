/**
 * 
 */
package com.social.servicios;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.social.entidades.Usuario;
import com.social.repositorios.UsuariosRepository;

/**
 * <h1>UserDetailsServiceImpl</h1>
 * 
 * Clase que implementa la interfaz {@link org.springframework.security.core.userdetails.UserDetailsService}
 * 
 * @author Antonio Paya Gonzalez
 * @author Pablo Diaz Rancaño
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private UsuariosRepository usuariosRepository;
	@Autowired
	public UserDetailsServiceImpl(UsuariosRepository usuariosRepository) {
		this.usuariosRepository = usuariosRepository;
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario user = usuariosRepository.findByUsername(username);

		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));

		return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
		
	}
}

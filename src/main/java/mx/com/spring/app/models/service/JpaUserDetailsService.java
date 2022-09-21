package mx.com.spring.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.com.spring.app.models.dao.IUsuarioDaoRepo;
import mx.com.spring.app.models.entity.Role;
import mx.com.spring.app.models.entity.Usuario;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService{
	
	@Autowired
	private IUsuarioDaoRepo usuarioDao;
	
	private Logger LOG = LoggerFactory.getLogger(JpaUserDetailsService.class);
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioDao.findByUsername(username);
		
		if(usuario == null) {
			LOG.error("Error Login: no existe el usuario".concat(username));
			throw new UsernameNotFoundException("usuario :".concat(username).concat("no existe "));
		}
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for(Role role: usuario.getRoles()) {
			LOG.info("..::ROLE: ".concat(role.getAuthority()));
			authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
		}
		
		if(authorities.isEmpty()) {
			LOG.error("Error Login: El usuario".concat(username).concat("no esta asignado"));
			throw new UsernameNotFoundException("usuario :".concat(username).concat("no tiene ROLES "));
		}
		return new User(usuario.getUsername(),usuario.getPassword() , usuario.getEnabled(), true, true, true, authorities);
	}
	

}

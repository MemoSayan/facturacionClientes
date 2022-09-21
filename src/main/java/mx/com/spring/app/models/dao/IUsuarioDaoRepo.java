package mx.com.spring.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import mx.com.spring.app.models.entity.Usuario;

public interface IUsuarioDaoRepo extends CrudRepository<Usuario, Long>{
	
	
	public Usuario findByUsername(String username);
	
	

}

package mx.com.spring.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import mx.com.spring.app.models.entity.Cliente;

						//Primero el nombre de la clase entity y el tipo de la llave
public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long>{
	
	

}

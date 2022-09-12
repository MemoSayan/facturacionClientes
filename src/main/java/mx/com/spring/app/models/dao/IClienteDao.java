package mx.com.spring.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import mx.com.spring.app.models.entity.Cliente;

						//Primero el nombre de la clase entity y el tipo de la llave
public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long>{
	
		@Query("select c from Cliente c left join fetch c.facturas f where c.id=?1")
		public Cliente fetchByIdWFacturas(Long id);

}

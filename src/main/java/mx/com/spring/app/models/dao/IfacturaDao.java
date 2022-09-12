package mx.com.spring.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import mx.com.spring.app.models.entity.Factura;

public interface IfacturaDao extends CrudRepository<Factura, Long>{
		
	@Query("Select f from Factura f join fetch f.cliente c join fetch f.items l join fetch l.producto where f.id=?1")
	public Factura fetchByIdWClienteWIFacturaWProducto(Long id);
	
	
}

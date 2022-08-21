package mx.com.spring.app.models.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mx.com.spring.app.models.entity.Cliente;

@Repository("clienteDaoJPA") // identificador
public class ClienteDaoImpl implements IClienteDao {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> findAll() {

		return em.createQuery("from Cliente").getResultList();
	}

	/*
	 ** Este metodo guarda un nuevo registro. 
	 *si id > 0 se edita
	 */
	@Override
	public void save(Cliente cliente) {
		if (cliente.getId() != null && cliente.getId() > 0) {
			em.merge(cliente);
		} else {
			em.persist(cliente);
		}
	}

	/*
	 * Este metodo busca al cliente en la base de datos
	 */
	@Override
	public Cliente findOne(Long id) {
		return em.find(Cliente.class, id);
	}

	/*
	 * borra un registro
	 */
	@Override
	public void delete(Long id) {
		Cliente cliente = findOne(id); // lo buscamos
		em.remove(cliente);// em.remove(findOne(id))

	}

}

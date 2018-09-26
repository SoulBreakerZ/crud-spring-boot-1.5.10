package cl.appendice.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import cl.appendice.springboot.app.models.entity.Cliente;

public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long> {
	
	@Query("select c from Cliente c left join fetch c.facturas f where c.id = ?")
	public Cliente fetchByIdWithFacturas(Long id);
}

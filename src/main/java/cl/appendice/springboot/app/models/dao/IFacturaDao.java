package cl.appendice.springboot.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cl.appendice.springboot.app.models.entity.Factura;

public interface IFacturaDao extends CrudRepository<Factura, Long> {
	@Query("select f from Factura f join fetch f.cliente c join fetch f.itemFacturas l join fetch l.producto where f.id=?1")
	public Factura fetchByIdWithClienteWhithItemFacturaWithProducto(Long id);
}

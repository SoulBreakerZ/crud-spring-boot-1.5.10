package cl.appendice.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.appendice.springboot.app.models.dao.IClienteDao;
import cl.appendice.springboot.app.models.dao.IFacturaDao;
import cl.appendice.springboot.app.models.dao.IProductoDao;
import cl.appendice.springboot.app.models.entity.Cliente;
import cl.appendice.springboot.app.models.entity.Factura;
import cl.appendice.springboot.app.models.entity.Producto;

@Service
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	private IClienteDao clienteDao;
	
	@Autowired
	private IProductoDao productoDao;
	
	@Autowired
	private IFacturaDao facturaDao;
	
	@Override
	@Transactional(readOnly=true)
	public List<Cliente> findAll() {
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	@Transactional
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente findOne(Long id) {
		return clienteDao.findOne(id);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.delete(id);
	}

	@Override
	public Page<Cliente> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return clienteDao.findAll(pageable);
	}

	@Override
	@Transactional
	public List<Producto> findByNombre(String term) {
		// TODO Auto-generated method stub
		
		StringBuffer termino = new StringBuffer().append("%").append(term).append("%");
		return productoDao.findByNombreLikeIgnoreCase(termino.toString());
	}

	@Override
	@Transactional
	public void saveFactura(Factura factura) {
		facturaDao.save(factura);
	}
	
	@Override
	@Transactional
	public void deleteFactura(Long id) {
		facturaDao.delete(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Producto findProductoById(Long id) {
		return productoDao.findOne(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Factura findFacturaById(Long id) {
		return facturaDao.findOne(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Factura fetchFacturaByIdWithClienteWhithItemFacturaWithProducto(Long id) {
		return facturaDao.fetchByIdWithClienteWhithItemFacturaWithProducto(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente fetchByIdWithFacturas(Long id) {
		return clienteDao.fetchByIdWithFacturas(id);
	}

}

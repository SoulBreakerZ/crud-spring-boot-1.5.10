package cl.appendice.springboot.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

// Por defecto el nombre de la clase es el nombre de la tabla pero dejo la anotacion entity para destacar que tambien se puede cambiar el nombre.

@Entity
@Table(name="factura")
public class Factura implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
	
	@NotEmpty
	private String descripcion;
	
    private String observacion;
    
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern="dd/MM/yyyy")
    private Date fecha;
    
    @ManyToOne(fetch=FetchType.LAZY)
    private Cliente cliente;
    
    @OneToMany(fetch=FetchType.LAZY,cascade= CascadeType.ALL)
    @JoinColumn(name="factura_id")
    private List<FacturaItem> itemFacturas;
    
	public Factura() {
		itemFacturas = new ArrayList<FacturaItem>();
	}
    
    @PrePersist
    public void prePersist() {
    	fecha = new Date();
    }
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<FacturaItem> getItemFacturas() {
		return itemFacturas;
	}

	public void setItemFacturas(List<FacturaItem> itemFacturas) {
		this.itemFacturas = itemFacturas;
	}
	
	public void addItemFactura(FacturaItem itemFactura) {
		itemFacturas.add(itemFactura);
	}
    
	public Double getTotal() {
		Double total = 0.0;
		
		for (FacturaItem itemFactura : itemFacturas) {
			total += itemFactura.calcularImporte();
		}
		return total;
	}
}
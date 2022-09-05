package mx.com.spring.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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


@Entity
@Table(name = "facturas")
public class Factura implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	private String description;
	private String observacion;
	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")
	private Date createAt;
	@ManyToOne(fetch = FetchType.LAZY)
	private Cliente cliente;
	@OneToMany(fetch = FetchType.LAZY, 
			cascade = CascadeType.ALL
			)
	@JoinColumn(name = "factura_id")
	private  List<ItemFactura> items;
	
	
	public Factura() {
		this.items = new ArrayList<ItemFactura>();
	}



	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}
	
	

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<ItemFactura> getItems() {
		return items;
	}

	public void setItems(List<ItemFactura> items) {
		this.items = items;
	}

	public void addItemFactura(ItemFactura item) {
	this.items.add(item);
	}
	
	public Double getTotal() {
		Double total = 0.0;
		int size  = items.size();
				for(int i=0; i<size; i++) {
					total += items.get(i).calcularImporte();
				}
				return total;
	}
}

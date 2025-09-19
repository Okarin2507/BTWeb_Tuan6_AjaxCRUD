package vn.nhuttan.entity;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id") 
	private Long productId;

	@Column(name = "name", length = 200, columnDefinition = "nvarchar(200) not null")
	private String name;

	@Column(name = "quantity")
	private int quantity;

	@Column(name = "unit_price")
	private double unitPrice;

	@Column(name = "discount")
	private double discount;

	@Column(name = "images", length = 255)
	private String images;

	@Column(name = "description", columnDefinition = "nvarchar(500)")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "YYYY-MM-DD hh:mi:ss")
	@Column(name = "create_date")
	private Date createDate;

	@Column(name = "status")
	private short status;

	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name = "category_id")
	private Category category;
}
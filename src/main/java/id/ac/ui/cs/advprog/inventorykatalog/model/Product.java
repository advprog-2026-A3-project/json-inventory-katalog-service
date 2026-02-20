package id.ac.ui.cs.advprog.inventorykatalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nama;

    private String deskripsi;

    @Column(nullable = false)
    private double harga;

    @Column(nullable = false)
    private int stok;

    private String negaraAsal;
    private LocalDate tanggalPembelian;
    private LocalDate tanggalKembali;

    @Column(nullable = false)
    private String jastiperId;
}
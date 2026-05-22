package id.ac.ui.cs.advprog.inventorykatalog.dto;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import id.ac.ui.cs.advprog.inventorykatalog.model.ProductType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductRequest {
    private String nama;
    private String deskripsi;
    private double harga;
    private int stok;
    private String negaraAsal;
    private LocalDate tanggalPembelian;
    private LocalDate tanggalKembali;
    private List<String> imageUrls;
    private ProductType productType;
    private LocalDateTime warStartTime;
    private LocalDateTime warEndTime;
    private int maxQuantityPerCheckout;

    public Product toProduct() {
        return Product.builder()
                .nama(nama)
                .deskripsi(deskripsi)
                .harga(harga)
                .stok(stok)
                .negaraAsal(negaraAsal)
                .tanggalPembelian(tanggalPembelian)
                .tanggalKembali(tanggalKembali)
                .imageUrls(imageUrls)
                .productType(productType)
                .warStartTime(warStartTime)
                .warEndTime(warEndTime)
                .maxQuantityPerCheckout(maxQuantityPerCheckout)
                .build();
    }
}
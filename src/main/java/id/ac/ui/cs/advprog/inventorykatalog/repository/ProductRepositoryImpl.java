package id.ac.ui.cs.advprog.inventorykatalog.repository;

import id.ac.ui.cs.advprog.inventorykatalog.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    @Override
    public Optional<Product> findById(String id) {
        Product product = entityManager.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    @Override
    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    @Override
    public void deleteById(String id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product); // Hapus data
        }
    }

    @Override
    public boolean existsById(String id) {
        Product product = entityManager.find(Product.class, id);
        return product != null;
    }

    @Override
    public List<Product> findByNamaContainingIgnoreCase(String nama) {
        String jpql = "SELECT p FROM Product p WHERE LOWER(p.nama) LIKE LOWER(:nama)";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        query.setParameter("nama", "%" + nama + "%");
        return query.getResultList();
    }

    @Override
    public List<Product> findByJastiperId(String jastiperId) {
        String jpql = "SELECT p FROM Product p WHERE p.jastiperId = :jastiperId";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        query.setParameter("jastiperId", jastiperId);
        return query.getResultList();
    }

    @Override
    public long count() {
        return entityManager.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
    }
}
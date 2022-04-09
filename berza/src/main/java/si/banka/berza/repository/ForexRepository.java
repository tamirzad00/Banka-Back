package si.banka.berza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import si.banka.berza.model.Akcije;
import si.banka.berza.model.Forex;

import java.util.List;

public interface ForexRepository extends JpaRepository<Forex, Long>, JpaSpecificationExecutor<Forex> {

    Forex findAkcijeById_hartije_od_vrednosti(Long id);

    @Query("SELECT f FROM Forex f WHERE (:berzaPrefix IS NULL OR LOWER(f.berza.naziv_name) LIKE CONCAT('%', :berzaPrefix)) AND " +
            "(:priceLowBound = 0 OR f.cena >= :priceLowBound) AND (:priceUpperBound = 0 OR f.cena <= :priceUpperBound) AND " +
            "(:askLowBound = 0 OR f.ask >= :askLowBound) AND (:askUpperBound = 0 OR f.ask <= :askUpperBound) AND " +
            "(:bidLowBound = 0 OR f.bid >= :bidLowBound) AND (:bidUpperBound = 0 OR f.bid <= :bidUpperBound) AND " +
            "(:volumeLowBound = 0 OR f.volume >= :volumeLowBound) AND (:volumeUpperBound = 0 OR f.volume <= :volumeUpperBound)")
    List<Forex> filterForex(String berzaPrefix, Double priceLowBound, Double priceUpperBound, Double askLowBound, Double askUpperBound,
                              Double bidLowBound, Double bidUpperBound, Long volumeLowBound, Long volumeUpperBound);

}

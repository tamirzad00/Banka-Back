package si.banka.berza.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class FuturesUgovori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_futures_ugovori;

    private double contract_size;
    private String contract_unit;
    private double maintenance_margin;
    private Date settlement_date;


}

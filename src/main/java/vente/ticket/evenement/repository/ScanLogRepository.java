package vente.ticket.evenement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vente.ticket.evenement.entity.ScanLog;

import java.util.List;

public interface ScanLogRepository extends JpaRepository<ScanLog, Long> {
    List<ScanLog> findByEvenementIdOrderByDateScanDesc(Long evenementId);
}

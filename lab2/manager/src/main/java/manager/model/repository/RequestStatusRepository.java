package manager.model.repository;

import manager.model.entity.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.Date;

public interface RequestStatusRepository extends MongoRepository<RequestStatus, String> {
    RequestStatus findByRequestId(String requestId);
    Collection<RequestStatus> findAllByUpdatedBeforeAndStatusEquals(Date timestamp, RequestStatus.Status status);
}
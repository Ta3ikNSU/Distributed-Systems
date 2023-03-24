package manager.model.repository;

import manager.model.entity.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RequestStatusRepository extends MongoRepository<RequestStatus, Long> {
}
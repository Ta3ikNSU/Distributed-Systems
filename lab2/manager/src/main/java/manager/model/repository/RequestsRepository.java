package manager.model.repository;

import manager.model.entity.Request;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RequestsRepository extends MongoRepository<Request, String> {
}

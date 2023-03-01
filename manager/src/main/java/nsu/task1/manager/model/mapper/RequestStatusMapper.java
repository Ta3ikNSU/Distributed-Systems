package nsu.task1.manager.model.mapper;

import nsu.task1.manager.api.DTO.RequestStatusDTO;
import nsu.task1.manager.model.entity.RequestStatus;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestStatusMapper {

    RequestStatusMapper INSTANCE = Mappers.getMapper(RequestStatusMapper.class);

    RequestStatusDTO toRequestStatusDTO(RequestStatus requestStatus);

    @InheritInverseConfiguration
    RequestStatus toRequestStatus(RequestStatusDTO requestStatusDTO);
}

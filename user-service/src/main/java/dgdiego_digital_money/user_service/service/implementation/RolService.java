package dgdiego_digital_money.user_service.service.implementation;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.domian.User;
import dgdiego_digital_money.user_service.entity.dto.RolDto;
import dgdiego_digital_money.user_service.exceptions.ResourceNotFoundException;
import dgdiego_digital_money.user_service.repository.IRolRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RolService {
    @Autowired
    IRolRepository rolRepository;

    public Rol findByName(String name){
        Optional<Rol> rolSearched = rolRepository.findByName(name);
        if (rolSearched.isPresent()) {
            return rolSearched.get();
        } else {
            String message = "No se encontro el ROL con nombre " + name;
            log.info(message);
            throw new ResourceNotFoundException(message);
        }
    }

    public RolDto mapToResponseDto(Rol rol){
        RolDto response = null;
        if(rol != null){
            response = new RolDto();
            response.setId(rol.getId());
            response.setName(rol.getName());
        }
        return response;
    }
}

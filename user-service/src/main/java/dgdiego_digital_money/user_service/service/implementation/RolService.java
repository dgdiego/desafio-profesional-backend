package dgdiego_digital_money.user_service.service.implementation;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.entity.dto.RolDto;
import dgdiego_digital_money.user_service.repository.IRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolService {
    @Autowired
    IRolRepository rolRepository;

    public Rol findByName(String name){
        return rolRepository.findByName("USER").get();
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

package dgdiego_digital_money.user_service.config;

import dgdiego_digital_money.user_service.entity.domian.Rol;
import dgdiego_digital_money.user_service.repository.IRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ApplicationRunner: interfaz funcional que se utiliza para ejecutar código cuando la aplicación se ha iniciado y está
 * lista para aceptar solicitudes.
 */
@Component
public class RolesLoader implements ApplicationRunner {

    @Autowired
    private IRolRepository rolRepository;

    /**
     * Tiene un solo metodo "run" el mismo se ejecuta una vez que el contexto de la
     *  aplicación de Spring Boot se ha cargado correctamente.
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        rolRepository.deleteAll();

        Rol adminRol = Rol.builder()
                .name("ADMIN")
                .build();

        Rol userRol = Rol.builder()
                .name("USER")
                .build();

        rolRepository.save(adminRol);
        rolRepository.save(userRol);
    }
}

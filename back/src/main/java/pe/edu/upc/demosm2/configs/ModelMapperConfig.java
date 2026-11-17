package pe.edu.upc.demosm2.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig
{
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.typeMap(pe.edu.upc.demosm2.entities.AsignacionDocente.class, pe.edu.upc.demosm2.dtos.AsignacionDTOList.class)
                .addMappings(m -> {
                    m.map(s -> s.getCurso().getId_curso(), pe.edu.upc.demosm2.dtos.AsignacionDTOList::setId_curso);
                    m.map(s -> s.getPeriodoAcademico().getIdPeriodo(), pe.edu.upc.demosm2.dtos.AsignacionDTOList::setId_periodo);
                    m.map(s -> s.getPersona().getIdPersona(), pe.edu.upc.demosm2.dtos.AsignacionDTOList::setId_persona);
                    m.map(s -> s.getColegio().getIdColegio(), pe.edu.upc.demosm2.dtos.AsignacionDTOList::setId_colegio);
                });
        return mapper;
    }

}

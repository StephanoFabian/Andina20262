package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.dtos.AccesoVideollamadaDTO;
import pe.edu.upc.demosm2.dtos.FragmentoTraduccionDTO;

public interface IVideollamadaService {
    AccesoVideollamadaDTO acceso(Long sesion, String usuario);
    FragmentoTraduccionDTO enviar(Long sesion, String usuario, FragmentoTraduccionDTO fragmento);
    FragmentoTraduccionDTO resultado(Long sesion, String usuario, String fragmento);
}

package pe.edu.upc.demosm2.serviceinterfaces;

import pe.edu.upc.demosm2.dtos.MaterialIADTO;

public interface IMaterialIAService {
    MaterialIADTO generar(String usuario, MaterialIADTO solicitud);
    MaterialIADTO consultar(String usuario, String id);
    byte[] archivo(String usuario, String id, String nombre);
}

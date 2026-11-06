import { modulo as configuracion } from '../models/catalogo';
const GESTION = ['ADMIN', 'ADMIN_ESCUELA', 'ESPECIALISTA', 'LOCAL'] as const;
const TODOS = [...GESTION, 'DOCENTE', 'ESTUDIANTE'] as const;
export const PERMISOS_EXTRA = {
  materialConsultas: TODOS,
  materialReporte: GESTION,
  materialCursoReporte: GESTION,
  personasListar: GESTION,
  conveniosPorVencer: GESTION,
};
export function modulo(key: string) {
  const config = configuracion(
    (
      {
        material: 'materiales',
        materialcurso: 'materiales-cursos',
        convenio: 'convenios',
      } as Record<string, string>
    )[key],
  );
  return {
    permisos: {
      insertar: config.roles,
      modificar: config.editable ? config.roles : [],
      eliminar: config.roles,
    },
  };
}

import { MODULOS, modulo as configuracion } from '../models/catalogo';
import { Modulo } from '../models/modulo';

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

/* ---------------------------------------------------------------------------
 * Qué módulos VE cada rol (menú lateral, vista de inicio y acceso por URL).
 * Es una regla de presentación del frontend: el backend deja leer (GET) a
 * cualquier usuario con sesión y solo restringe crear, editar y eliminar.
 *
 *  - ADMIN: todos los módulos.
 *  - ADMIN_ESCUELA, ESPECIALISTA y LOCAL (gestión): todos menos Roles.
 *    Personas la ven en solo lectura (PERMISOS_EXTRA.personasListar).
 *  - DOCENTE: lo que usa para dar clases.
 *  - ESTUDIANTE: lo que usa para asistir a clases.
 * ------------------------------------------------------------------------- */
const VE_DOCENTE = [
  'sesiones-virtuales',
  'asistencias',
  'horarios',
  'materiales',
  'materiales-cursos',
  'asignaciones',
  'cursos',
  'perfiles-academicos',
  'incidencias',
];
const VE_ESTUDIANTE = [
  'sesiones-virtuales',
  'horarios',
  'materiales',
  'materiales-cursos',
  'asistencias',
];

/** Roles que pueden abrir un módulo del catálogo */
export function rolesLectura(key: string): readonly string[] {
  if (key === 'roles') return ['ADMIN'];
  if (key === 'personas') return PERMISOS_EXTRA.personasListar;
  return [
    ...GESTION,
    ...(VE_DOCENTE.includes(key) ? ['DOCENTE'] : []),
    ...(VE_ESTUDIANTE.includes(key) ? ['ESTUDIANTE'] : []),
  ];
}

export function puedeVer(key: string, roles: readonly string[]): boolean {
  return rolesLectura(key).some((r) => roles.includes(r));
}

/** Módulos del catálogo que corresponden a los roles del usuario, en el orden del catálogo */
export function modulosVisibles(roles: readonly string[]): Modulo[] {
  return MODULOS.filter((m) => puedeVer(m.key, roles));
}

/** Estudiante "puro": no tiene además un rol de docente o de gestión */
export function esEstudiante(roles: readonly string[]): boolean {
  return (
    roles.includes('ESTUDIANTE') &&
    !roles.includes('DOCENTE') &&
    !GESTION.some((r) => roles.includes(r))
  );
}

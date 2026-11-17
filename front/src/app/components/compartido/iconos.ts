/** Icono (Material Icons) de cada módulo del catálogo; lo usan el menú lateral, la vista de inicio y los listados */
const ICONOS: Record<string, string> = {
  colegios: 'account_balance',
  aulas: 'meeting_room',
  grados: 'stairs',
  cursos: 'menu_book',
  'periodos-academicos': 'date_range',
  matriculas: 'how_to_reg',
  'detalles-matricula': 'assignment',
  personas: 'people',
  roles: 'admin_panel_settings',
  'perfiles-academicos': 'insights',
  materiales: 'folder_open',
  'materiales-cursos': 'library_books',
  asignaciones: 'co_present',
  'sesiones-virtuales': 'videocam',
  asistencias: 'fact_check',
  horarios: 'schedule',
  convenios: 'handshake',
  incidencias: 'report_problem',
};

export function iconoModulo(key: string): string {
  return ICONOS[key] ?? 'school';
}

/** Una línea que explica qué es cada módulo; se muestra bajo el título de su listado */
const DESCRIPCIONES: Record<string, string> = {
  colegios: 'Instituciones educativas y su ubicación.',
  aulas: 'Salones de cada colegio, con su sección y capacidad.',
  grados: 'Grados de primaria y secundaria.',
  cursos: 'Cursos que se dictan y el área a la que pertenecen.',
  'periodos-academicos': 'Periodos del año escolar y sus fechas.',
  matriculas: 'Estudiantes inscritos en cada colegio.',
  'detalles-matricula':
    'Curso, grado y periodo de cada matrícula, con su estado.',
  personas: 'Cuentas de estudiantes, docentes y personal.',
  roles: 'Tipos de usuario de la plataforma.',
  'perfiles-academicos': 'Notas y observaciones de seguimiento académico.',
  materiales: 'Lecturas, guías, videos y otros recursos de estudio.',
  'materiales-cursos': 'Qué materiales corresponden a cada curso.',
  asignaciones: 'Qué docente dicta cada curso, en qué aula y periodo.',
  'sesiones-virtuales': 'Clases en vivo por videollamada.',
  asistencias: 'Registro de quién asistió a cada sesión.',
  horarios: 'Día y hora de cada clase.',
  convenios: 'Acuerdos con entidades, su vigencia y presupuesto.',
  incidencias: 'Problemas reportados y su estado de atención.',
};

export function descripcionModulo(key: string): string {
  return DESCRIPCIONES[key] ?? '';
}

interface TipoMaterial {
  icono: string;
  texto: string;
}

/** Icono y nombre legible de cada tipo de material. La clave va en mayúsculas y sin tildes. */
const TIPOS_MATERIAL: Record<string, TipoMaterial> = {
  PDF: { icono: 'picture_as_pdf', texto: 'PDF' },
  VIDEO: { icono: 'smart_display', texto: 'Video' },
  AUDIO: { icono: 'headphones', texto: 'Audio' },
  PRESENTACION: { icono: 'slideshow', texto: 'Presentación' },
  DOCUMENTO: { icono: 'description', texto: 'Documento' },
  GUIA: { icono: 'menu_book', texto: 'Guía' },
  FICHA: { icono: 'assignment', texto: 'Ficha' },
  LECTURA: { icono: 'auto_stories', texto: 'Lectura' },
  ENLACE: { icono: 'link', texto: 'Enlace' },
  LINK: { icono: 'link', texto: 'Enlace' },
  URL: { icono: 'link', texto: 'Enlace' },
  IMAGEN: { icono: 'image', texto: 'Imagen' },
};

export function tipoMaterial(tipo: string | null | undefined): TipoMaterial {
  const original = (tipo ?? '').trim();
  if (!original) return { icono: 'insert_drive_file', texto: 'Sin tipo' };
  const clave = original
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toUpperCase();
  return (
    TIPOS_MATERIAL[clave] ?? {
      icono: 'insert_drive_file',
      texto:
        original === original.toUpperCase()
          ? original.charAt(0) + original.slice(1).toLowerCase()
          : original,
    }
  );
}

/** Cantidad de colores de curso definidos en mejoras.css (.chip-curso--0 … --7) */
const COLORES_CURSO = 8;

/**
 * Color fijo de un curso: el mismo nombre da siempre el mismo número,
 * en cualquier pantalla y sin guardar nada.
 */
export function colorCurso(nombre: string | null | undefined): number {
  const texto = (nombre ?? '').trim().toLowerCase();
  let suma = 0;
  for (let i = 0; i < texto.length; i++) {
    suma = (suma * 31 + texto.charCodeAt(i)) % 9973;
  }
  return suma % COLORES_CURSO;
}

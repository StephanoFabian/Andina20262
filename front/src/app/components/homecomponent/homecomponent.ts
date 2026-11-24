import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/auth.service';
import { modulo } from '../../models/catalogo';
import { SesionVirtual } from '../../models/SesionVirtual';
import { SesionVirtualService } from '../../services/sesiones-virtuales.service';
import { PERMISOS_EXTRA, puedeVer } from '../../services/permisos';
import { SesionUsuario } from '../compartido/sesion-usuario';
import { iconoModulo } from '../compartido/iconos';
import { Chip } from '../compartido/chip/chip';
import { FechaPePipe, HoraPePipe } from '../compartido/formato';

type Perfil = 'estudiante' | 'docente' | 'gestion' | 'admin' | 'general';

interface Acceso {
  ruta: string;
  icono: string;
  titulo: string;
  detalle: string;
}

interface Reporte {
  ruta: string;
  titulo: string;
  /** Roles que pueden abrirlo, tomados de permisos.ts o del catálogo de módulos */
  roles: readonly string[];
}

const GESTION = ['ADMIN_ESCUELA', 'ESPECIALISTA', 'LOCAL'];

const acceso = (key: string, titulo: string, detalle: string): Acceso => ({
  ruta: '/' + key,
  icono: iconoModulo(key),
  titulo,
  detalle,
});

/** Accesos destacados de cada perfil. Solo son atajos a rutas que ya existen. */
const ACCESOS: Record<Perfil, Acceso[]> = {
  estudiante: [
    acceso(
      'sesiones-virtuales',
      'Sesiones virtuales',
      'Entra a la clase en vivo con un clic.',
    ),
    acceso(
      'materiales',
      'Materiales',
      'Lecturas, guías y recursos de estudio.',
    ),
    acceso(
      'materiales-cursos',
      'Materiales por curso',
      'Los recursos ordenados por cada curso.',
    ),
    acceso('asistencias', 'Asistencias', 'Consulta el registro de asistencia.'),
  ],
  docente: [
    acceso(
      'sesiones-virtuales',
      'Sesiones virtuales',
      'Programa una clase y abre su videollamada.',
    ),
    acceso(
      'asistencias',
      'Asistencias',
      'Registra quién asistió a cada sesión.',
    ),
    acceso('materiales', 'Materiales', 'Recursos y guías para las clases.'),
  ],
  gestion: [
    acceso('matriculas', 'Matrículas', 'Registra y revisa matrículas.'),
    acceso('convenios', 'Convenios', 'Vigencia y renovaciones.'),
    acceso('periodos-academicos', 'Periodos académicos', 'Abre y cierra cada periodo.'),
    acceso('grados', 'Grados', 'Grados de cada nivel.'),
    acceso('detalles-matricula', 'Detalles de matrícula', 'Cursos de cada matrícula.'),
    acceso('materiales', 'Materiales', 'Recursos y guías para las clases.'),
  ],
  admin: [
    acceso('matriculas', 'Matrículas', 'Registra y revisa matrículas.'),
    acceso('convenios', 'Convenios', 'Vigencia y renovaciones.'),
    acceso('periodos-academicos', 'Periodos académicos', 'Abre y cierra cada periodo.'),
    acceso('grados', 'Grados', 'Grados de cada nivel.'),
    acceso('detalles-matricula', 'Detalles de matrícula', 'Cursos de cada matrícula.'),
    acceso('materiales', 'Materiales', 'Recursos y guías para las clases.'),
  ],
  general: [
    acceso(
      'sesiones-virtuales',
      'Sesiones virtuales',
      'Entra a la clase en vivo con un clic.',
    ),
    acceso(
      'materiales',
      'Materiales',
      'Lecturas, guías y recursos de estudio.',
    ),
  ],
};

const TEXTOS: Record<Perfil, { saludo: string; titulo: string }> = {
  estudiante: {
    saludo: 'Aquí tienes lo que necesitas para tus clases.',
    titulo: 'Para tus clases',
  },
  docente: {
    saludo:
      'Programa tus sesiones, registra asistencias y comparte materiales.',
    titulo: 'Accesos rápidos',
  },
  gestion: {
    saludo:
      'Gestiona matrículas, periodos y convenios de tu comunidad educativa.',
    titulo: 'Gestión frecuente',
  },
  admin: {
    saludo: 'Administra las cuentas, los colegios y revisa los reportes.',
    titulo: 'Gestión',
  },
  general: {
    saludo: 'Organiza, conecta y acompaña el recorrido de tu comunidad.',
    titulo: 'Accesos rápidos',
  },
};

@Component({
  selector: 'app-home',
  imports: [
    RouterLink,
    MatIconModule,
    MatButtonModule,
    Chip,
    FechaPePipe,
    HoraPePipe,
  ],
  templateUrl: './homecomponent.html',
})
export class HomeComponent implements OnInit {
  readonly auth = inject(AuthService);
  readonly usuario = inject(SesionUsuario);
  private readonly sesionesService = inject(SesionVirtualService);

  readonly perfil = computed<Perfil>(() => {
    const roles = this.auth.roles();
    if (roles.includes('ADMIN')) return 'admin';
    if (roles.some((r) => GESTION.includes(r))) return 'gestion';
    if (roles.includes('DOCENTE')) return 'docente';
    if (roles.includes('ESTUDIANTE')) return 'estudiante';
    return 'general';
  });
  readonly textos = computed(() => TEXTOS[this.perfil()]);
  /** Atajos del perfil, sin los módulos que el rol no puede abrir (permisos.ts) */
  readonly accesos = computed(() =>
    ACCESOS[this.perfil()].filter((a) =>
      puedeVer(a.ruta.slice(1), this.auth.roles()),
    ),
  );
  /** Estudiantes y docentes ven tarjetas grandes; gestión ve más accesos y más compactos */
  readonly compacto = computed(
    () => this.perfil() === 'admin' || this.perfil() === 'gestion',
  );

  /** Cada reporte se muestra solo a los roles que el proyecto ya autoriza */
  private readonly todosLosReportes: Reporte[] = [
    {
      ruta: '/matriculas/reportes',
      titulo: 'Matrículas por colegio',
      roles: modulo('matriculas').roles,
    },
    {
      ruta: '/periodos-academicos/reportes',
      titulo: 'Evolución por periodo',
      roles: modulo('periodos-academicos').roles,
    },
    {
      ruta: '/detalles-matricula/reportes',
      titulo: 'Retiros, aulas y retención',
      roles: modulo('detalles-matricula').roles,
    },
    {
      ruta: '/convenios/por-vencer',
      titulo: 'Convenios por vencer',
      roles: PERMISOS_EXTRA.conveniosPorVencer,
    },
    {
      ruta: '/materiales/reporte-tipo',
      titulo: 'Materiales por tipo',
      roles: PERMISOS_EXTRA.materialReporte,
    },
    {
      ruta: '/materiales-cursos/reporte',
      titulo: 'Materiales por curso',
      roles: PERMISOS_EXTRA.materialCursoReporte,
    },
    {
      ruta: '/convenios/dashboard',
      titulo: 'Dashboard de convenios',
      roles: PERMISOS_EXTRA.conveniosPorVencer,
    },
    {
      ruta: '/materiales/dashboard',
      titulo: 'Dashboard de materiales',
      roles: PERMISOS_EXTRA.materialReporte,
    },
    {
      ruta: '/materiales-cursos/dashboard',
      titulo: 'Dashboard de materiales por curso',
      roles: PERMISOS_EXTRA.materialCursoReporte,
    },
  ];
  readonly reportes = computed(() =>
    this.compacto()
      ? this.todosLosReportes.filter((r) =>
          r.roles.some((rol) => this.auth.roles().includes(rol)),
        )
      : [],
  );

  /** Sesiones programadas o en curso, las más próximas primero */
  readonly sesiones = signal<SesionVirtual[]>([]);
  readonly verSesiones = computed(() => !this.compacto());

  readonly saludo = computed(() => {
    const nombre = this.usuario.primerNombre();
    return nombre ? `¡Hola, ${nombre}!` : '¡Hola!';
  });

  ngOnInit(): void {
    if (!this.verSesiones()) return;
    // Mismo endpoint del listado de sesiones; si falla, la sección simplemente no aparece
    this.sesionesService.pagina(0, 50).subscribe({
      next: (p) =>
        this.sesiones.set(
          (p.content ?? [])
            .filter((s) => s.estado === 'PROGRAMADA' || s.estado === 'EN_CURSO')
            .sort((a, b) =>
              `${a.fecha}T${a.horaInicio}`.localeCompare(
                `${b.fecha}T${b.horaInicio}`,
              ),
            )
            .slice(0, 3),
        ),
      error: () => this.sesiones.set([]),
    });
  }
}

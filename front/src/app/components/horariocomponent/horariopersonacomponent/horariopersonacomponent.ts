import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HorarioListDTO } from '../../../models/HorarioListDTO';
import { Shorario } from '../../../services/shorario';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

interface GrupoHorarioDia {
  dia: string;
  items: HorarioListDTO[];
}

@Component({
  selector: 'app-horariopersonacomponent',
  imports: [CommonModule, MatCardModule],
  templateUrl: './horariopersonacomponent.html',
  styleUrl: './horariopersonacomponent.css',
})
export class Horariopersonacomponent implements OnInit {
  dataSource: HorarioListDTO[] = [];
  horariosPorDia: GrupoHorarioDia[] = [];

  private readonly ordenDias: Record<string, number> = {
    Lunes: 1,
    Martes: 2,
    Miércoles: 3,
    Miercoles: 3,
    Jueves: 4,
    Viernes: 5,
    Sábado: 6,
    Sabado: 6,
    Domingo: 7,
  };

  constructor(
    private sH: Shorario,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.obtenerHorariosPersona(1); // Reemplaza 1 con el ID de la persona que deseas obtener
  }

  obtenerHorariosPersona(id: number) {
    this.sH.getHorarioPersona(id).subscribe({
      next: (data) => {
        this.dataSource = data;
        this.horariosPorDia= this.agruparPorDia(data);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Ocurrió un error!!', err);
        this.horariosPorDia = [];
        this.cdr.detectChanges();
      },
    });
  }

private agruparPorDia(horarios: HorarioListDTO[]): GrupoHorarioDia[]{
  const mapa = new Map<string, HorarioListDTO[]>();
  for (const horario of horarios) {
      const dia = String(horario.diaSemana || 'Sin día');
      const lista = mapa.get(dia) ?? [];
      lista.push(horario);
      mapa.set(dia, lista);
    }

    return Array.from(mapa.entries())
      .map(([dia, items]) => ({ dia, items }))
      .sort((a, b) => (this.ordenDias[a.dia] ?? 99) - (this.ordenDias[b.dia] ?? 99));
}

}

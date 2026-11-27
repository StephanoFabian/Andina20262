import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HorarioListDTO } from '../../../models/HorarioListDTO';
import { Shorario } from '../../../services/shorario';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-horalistarcomponent',
  imports: [MatTableModule, MatButtonModule, MatIconModule, RouterLink],
  templateUrl: './horalistarcomponent.html',
  styleUrl: './horalistarcomponent.css',
})
export class Horalistarcomponent implements OnInit {
  displayedColumns: string[] = ['c1', 'c2', 'c3', 'c4', 'c5', 'c6', 'c7', 'c8', 'c9', 'c10'];
  dataSource: HorarioListDTO[] = [];
  constructor(
    private sH: Shorario,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.obtenerhorarios();
  }

  obtenerhorarios() {
    this.sH.getListHorario().subscribe({
      next: (data) => {
        this.dataSource = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Ocurrió un error!!', err);
        this.cdr.detectChanges();
      },
    });
  }

  eliminarHorario(id: number) {
    this.sH.deleteHorario(id).subscribe({
      next: () => {
        this.obtenerhorarios();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Ocurrió un error!!', err);
        this.cdr.detectChanges();
      },
    });
  }
}

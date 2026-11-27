import { ChangeDetectorRef, Component } from '@angular/core';
import { AsignacionDTOList } from '../../../models/AsignacionDTOList';
import { Sasignacion } from '../../../services/sasignacion';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-asigfiltrocomponent',
  imports: [MatSelectModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
  MatInputModule,
FormsModule],
  templateUrl: './asigfiltrocomponent.html',
  styleUrl: './asigfiltrocomponent.css',
})
export class Asigfiltrocomponent {
dataSource: AsignacionDTOList[] = [];
  displayedColumns: string[] = ['c1', 'c2', 'c3', 'c4'];


  minimo: number=0;
  maximo: number=0;

  mensaje: string = '';

  constructor(
  private aS:Sasignacion,private cdr: ChangeDetectorRef
  ){}

  ngOnInit(): void {}

  buscarentreHoras():void{
    if(!this.minimo || !this.maximo){
      this.dataSource=[];
      this.mensaje='Complete ambos campos';
      return
    }
    this.cdr.detectChanges();
    this.aS.GetRangoHoras(this.minimo,this.maximo).subscribe({
      next: (data) => {
        this.dataSource = data;
        this.mensaje = data.length === 0 ? 'No se encontraron registros de asignaciones!!' : '';
        this.cdr.detectChanges();
      },
    })
  }
}

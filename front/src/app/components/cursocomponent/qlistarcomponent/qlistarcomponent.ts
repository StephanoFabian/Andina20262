import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {MatTableModule} from '@angular/material/table';
import { CursoDTOList } from '../../../models/CursoDTOList';
import { Scurso } from '../../../services/scurso';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button'
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-qlistarcomponent',
  imports: [MatTableModule, MatButtonModule, MatIconModule, RouterLink],
  templateUrl: './qlistarcomponent.html',
  styleUrl: './qlistarcomponent.css',
})
export class Qlistarcomponent implements OnInit {

  displayedColumns: string[] =  ['c1', 'c2', 'c3', 'c4', 'c5'];
  dataSource: CursoDTOList[]=[]
  constructor(private qService:Scurso,
    private cdr:ChangeDetectorRef
  ){}

  ngOnInit(): void {
    this.obtenercursos();
  }

obtenercursos(){
  this.qService.lista().subscribe({
      next: data => {
        this.dataSource = data;
        this.cdr.detectChanges();
      }, error: err => {
        console.log('Ocurrió un error!!', err);
        this.cdr.detectChanges();
      }
    })
}
eliminarCurso(id:number){
  this.qService.delete(id).subscribe({
      next:()=>{
        this.obtenercursos()
        this.cdr.detectChanges();
      },error:err=>{
        console.log('Ocurrió un error!!', err);
        this.cdr.detectChanges();
      }
    })
}

}

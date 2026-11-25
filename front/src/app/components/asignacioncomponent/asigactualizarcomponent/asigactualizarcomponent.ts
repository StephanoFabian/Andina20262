import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Sasignacion } from '../../../services/sasignacion';
import { ActivatedRoute } from '@angular/router';
import { Router } from 'express';
import { AsignacionDTOList } from '../../../models/AsignacionDTOList';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-asigactualizarcomponent',
  providers: [provideNativeDateAdapter()],
  imports: [MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatCheckboxModule],
  templateUrl: './asigactualizarcomponent.html',
  styleUrl: './asigactualizarcomponent.css',
})
export class Asigactualizarcomponent {
  idAsignacion: any = null;
  formAsignacion!: FormGroup;
  mensaje: string = '';

  constructor(
    private aS: Sasignacion,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.idAsignacion = Number(this.route.snapshot.paramMap.get('id'));

    console.log('ID recibido:', this.idAsignacion);
    if (!this.idAsignacion || isNaN(this.idAsignacion)) {
      this.mensaje = 'ID no válido';
      return;
    }

    this.formAsignacion = this.fb.group({
      codigo: [this.idAsignacion],
      aula: ['', Validators.required],
      curso: ['', Validators.required],
      periodo: ['', Validators.required],
      persona: ['', Validators.required],
      modalidad: ['', Validators.required],
      horas: ['', Validators.required],
      colegio: ['', Validators.required],
    });

    this.buscarId();
  }

  buscarId(): void {
    this.aS.IdAsignacion(this.idAsignacion).subscribe({
      next: (data: AsignacionDTOList) => {
        console.log('Asignacion encontrada:', data);

        this.formAsignacion.patchValue({
          aula: data.id_aula,
          curso: data.id_curso,
          modalidad: data.modalidad,
          horas: data.horassemanales,
        });
      },
      error: (error) => {
        console.error('Error al buscar', error);
        this.mensaje = 'Asignacion no encontrada';
      },
    });
  }

  actualizarAsignacion(): void {
    const dto = {
      id_asignacion: this.formAsignacion.value.codigo,
      id_aula: this.formAsignacion.value.aula,
      id_curso: this.formAsignacion.value.curso,
      id_periodo: this.formAsignacion.value.periodo,
      id_persona: this.formAsignacion.value.persona,
      modalidad: this.formAsignacion.value.modalidad,
      horassemanales: this.formAsignacion.value.horas,
      id_colegio: this.formAsignacion.value.colegio,
    };

    this.aS.updateAsignacion(dto).subscribe({
      next: () => {
        this.mensaje = 'Asignado actualizada correctamente';
        this.cdr.detectChanges();
        this.formAsignacion.reset();
      },
      error: (error) => {
        console.error(error);
        this.mensaje = 'Error al actualizar!!';
      }
    });
  }
}
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { provideNativeDateAdapter } from '@angular/material/core';
import { Shorario } from '../../../services/shorario';
import { ActivatedRoute, Router } from '@angular/router';
import { HorarioInsertDTO } from '../../../models/HorarioInsertDTO';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-horaactualizarcomponent',
  providers: [provideNativeDateAdapter()],
  imports: [MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatCheckboxModule],
  templateUrl: './horaactualizarcomponent.html',
  styleUrl: './horaactualizarcomponent.css',
})
export class Horaactualizarcomponent {
  idHorario: any = null;
  formHorario!: FormGroup;
  mensaje: string = '';

  constructor(
    private hs: Shorario,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.idHorario = Number(this.route.snapshot.paramMap.get('id'));

    console.log('ID recibido:', this.idHorario);

    if (!this.idHorario || isNaN(this.idHorario)) {
      this.mensaje = 'ID no válido';
      return;
    }

    this.formHorario = this.fb.group({
      codigo: [this.idHorario],
      dia: ['', Validators.required],
      inicio: ['', Validators.required],
      fin: ['', Validators.required],
      asignacion: ['', Validators.required],
    });

    this.buscarId();
  }

  buscarId(): void {
    this.hs.IdHorario(this.idHorario).subscribe({
      next: (data: HorarioInsertDTO) => {
        console.log('Horario detectado:', data);

        this.formHorario.patchValue({
          codigo: data.id_horario,
          dia: data.diaSemana,
          inicio: data.horaInicio,
          fin: data.horaFin,
          asignacion: data.idAsignacion,
        });
      },
      error: (error) => {
        console.error('Error al buscar el curso', error);
        this.mensaje = 'Curso no encontrado';
      },
    });
  }

  actualizarHorario(): void {
    const dto = {
      id_horario: this.formHorario.value.codigo,
      diaSemana: this.formHorario.value.dia,
      horaInicio: this.formHorario.value.inicio,
      horaFin: this.formHorario.value.fin,
      idAsignacion: this.formHorario.value.asignacion,
    };

    this.hs.updateHorario(dto).subscribe({
      next: () => {
        this.mensaje = 'Horario actualizado correctamente';
        this.cdr.detectChanges();
        this.formHorario.reset();
      },
      error: (error) => {
        console.error(error);
        this.mensaje = 'Error al actualizar!!';
      },
    });
  }
}

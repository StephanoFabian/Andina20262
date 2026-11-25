import { Component } from '@angular/core';
import { Menucomponent } from '../menucomponent/menucomponent';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-asignacioncomponent',
  imports: [Menucomponent,RouterOutlet],
  templateUrl: './asignacioncomponent.html',
  styleUrl: './asignacioncomponent.css',
})
export class Asignacioncomponent {}

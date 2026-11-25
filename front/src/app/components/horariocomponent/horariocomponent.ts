import { Component } from '@angular/core';
import { Menucomponent } from '../menucomponent/menucomponent';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-horariocomponent',
  imports: [Menucomponent,RouterOutlet],
  templateUrl: './horariocomponent.html',
  styleUrl: './horariocomponent.css',
})
export class Horariocomponent {}

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { PermissionsRoutingModule } from './permissions-routing.module';
import { PermissionMatrixComponent } from './components/permission-matrix/permission-matrix.component';

@NgModule({
  declarations: [
    PermissionMatrixComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PermissionsRoutingModule
  ]
})
export class PermissionsModule { }

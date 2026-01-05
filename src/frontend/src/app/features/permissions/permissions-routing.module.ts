import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionMatrixComponent } from './components/permission-matrix/permission-matrix.component';

const routes: Routes = [
  {
    path: '',
    component: PermissionMatrixComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PermissionsRoutingModule { }

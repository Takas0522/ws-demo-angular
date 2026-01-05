import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'users',
    loadChildren: () => import('./features/users/users.module').then(m => m.UsersModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'permissions',
    loadChildren: () => import('./features/permissions/permissions.module').then(m => m.PermissionsModule),
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: '/users',
    pathMatch: 'full'
  }
  // Feature modules will be lazy loaded here
  // Example:
  // {
  //   path: 'dashboard',
  //   loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule),
  //   canActivate: [AuthGuard]
  // }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

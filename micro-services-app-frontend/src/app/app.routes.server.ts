import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'products',
    renderMode: RenderMode.Client
  },
  {
    path: 'customers',
    renderMode: RenderMode.Client
  },
  {
    path: 'bills',
    renderMode: RenderMode.Client
  },
  {
    path: 'bills/:customerId',
    renderMode: RenderMode.Client
  },
  {
    path: 'bill-details/:billId',
    renderMode: RenderMode.Client
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];

import { Injectable, inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private keycloakService = inject(KeycloakService);

    get isLoggedIn(): boolean {
        return this.keycloakService.isLoggedIn();
    }

    get username(): string {
        return this.keycloakService.getUsername();
    }

    get token(): string {
        return this.keycloakService.getToken();
    }

    getUserRoles(): string[] {
        return this.keycloakService.getUserRoles();
    }

    hasRole(role: string): boolean {
        return this.keycloakService.isUserInRole(role);
    }

    login(): void {
        this.keycloakService.login();
    }

    logout(): void {
        this.keycloakService.logout(window.location.origin);
    }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/customer.model';
import { Product } from '../models/product.model';
import { Bill } from '../models/bill.model';

@Injectable({
    providedIn: 'root'
})
export class ConsumerService {
    private gatewayUrl = '';

    constructor(private http: HttpClient) { }

    // ============================================
    // Customer Operations
    // ============================================
    
    public getCustomers(): Observable<any> {
        return this.http.get(`${this.gatewayUrl}/customer-service/api/customers`);
    }

    public getCustomerById(id: number): Observable<Customer> {
        return this.http.get<Customer>(`${this.gatewayUrl}/customer-service/api/customers/${id}`);
    }

    public createCustomer(customer: Partial<Customer>): Observable<Customer> {
        return this.http.post<Customer>(`${this.gatewayUrl}/customer-service/api/customers`, customer);
    }

    public updateCustomer(id: number, customer: Partial<Customer>): Observable<Customer> {
        return this.http.put<Customer>(`${this.gatewayUrl}/customer-service/api/customers/${id}`, customer);
    }

    public deleteCustomer(id: number): Observable<void> {
        return this.http.delete<void>(`${this.gatewayUrl}/customer-service/api/customers/${id}`);
    }

    // ============================================
    // Product Operations
    // ============================================

    public getProducts(): Observable<any> {
        return this.http.get(`${this.gatewayUrl}/inventory-service/api/products`);
    }

    public getProductById(id: string): Observable<Product> {
        return this.http.get<Product>(`${this.gatewayUrl}/inventory-service/api/products/${id}`);
    }

    public createProduct(product: Partial<Product>): Observable<Product> {
        return this.http.post<Product>(`${this.gatewayUrl}/inventory-service/api/products`, product);
    }

    public updateProduct(id: string, product: Partial<Product>): Observable<Product> {
        return this.http.put<Product>(`${this.gatewayUrl}/inventory-service/api/products/${id}`, product);
    }

    public deleteProduct(id: string): Observable<void> {
        return this.http.delete<void>(`${this.gatewayUrl}/inventory-service/api/products/${id}`);
    }

    // ============================================
    // Bill Operations
    // ============================================

    public getBillsByCustomerID(customerId: number): Observable<any> {
        return this.http.get(`${this.gatewayUrl}/billing-service/api/bills/search/findByCustomerId?customerId=${customerId}&projection=fullBill`);
    }

    public getAllBills(): Observable<any> {
        return this.http.get(`${this.gatewayUrl}/billing-service/api/bills?projection=fullBill`);
    }

    public getBillDetails(id: number): Observable<Bill> {
        return this.http.get<Bill>(`${this.gatewayUrl}/billing-service/bills/${id}`);
    }
}

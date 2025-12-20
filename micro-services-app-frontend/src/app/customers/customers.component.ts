import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConsumerService } from '../services/consumer.service';
import { Router } from '@angular/router';
import { Customer } from '../models/customer.model';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css'
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  loading = true;

  // Modal states
  showModal = false;
  showDeleteModal = false;
  isEditMode = false;

  // Form data
  currentCustomer: Partial<Customer> = { name: '', email: '' };
  customerToDelete: Customer | null = null;

  // Toast
  toasts: { id: number; type: string; message: string }[] = [];
  toastId = 0;

  constructor(private consumerService: ConsumerService, private router: Router) { }

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.consumerService.getCustomers().subscribe({
      next: (data) => {
        this.customers = data._embedded?.customers || [];
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.showToast('error', 'Failed to load customers');
        this.loading = false;
      }
    });
  }

  // Modal handlers
  openAddModal(): void {
    this.isEditMode = false;
    this.currentCustomer = { name: '', email: '' };
    this.showModal = true;
  }

  openEditModal(customer: Customer): void {
    this.isEditMode = true;
    this.currentCustomer = { ...customer };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.currentCustomer = { name: '', email: '' };
  }

  openDeleteModal(customer: Customer): void {
    this.customerToDelete = customer;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.customerToDelete = null;
  }

  // CRUD operations
  saveCustomer(): void {
    if (!this.currentCustomer.name || !this.currentCustomer.email) {
      this.showToast('error', 'Please fill in all fields');
      return;
    }

    if (this.isEditMode && this.currentCustomer.id) {
      this.consumerService.updateCustomer(this.currentCustomer.id, this.currentCustomer).subscribe({
        next: () => {
          this.showToast('success', 'Customer updated successfully');
          this.closeModal();
          this.loadCustomers();
        },
        error: (err) => {
          console.error(err);
          this.showToast('error', 'Failed to update customer');
        }
      });
    } else {
      this.consumerService.createCustomer(this.currentCustomer).subscribe({
        next: () => {
          this.showToast('success', 'Customer created successfully');
          this.closeModal();
          this.loadCustomers();
        },
        error: (err) => {
          console.error(err);
          this.showToast('error', 'Failed to create customer');
        }
      });
    }
  }

  confirmDelete(): void {
    if (!this.customerToDelete) return;

    this.consumerService.deleteCustomer(this.customerToDelete.id).subscribe({
      next: () => {
        this.showToast('success', 'Customer deleted successfully');
        this.closeDeleteModal();
        this.loadCustomers();
      },
      error: (err) => {
        console.error(err);
        this.showToast('error', 'Failed to delete customer');
        this.closeDeleteModal();
      }
    });
  }

  handleViewBills(customer: Customer): void {
    this.router.navigate(['/bills', customer.id]);
  }

  // Toast notifications
  showToast(type: string, message: string): void {
    const id = ++this.toastId;
    this.toasts.push({ id, type, message });
    setTimeout(() => this.removeToast(id), 4000);
  }

  removeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }
}

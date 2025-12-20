import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConsumerService } from '../services/consumer.service';
import { Product } from '../models/product.model';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  loading = true;

  // Modal states
  showModal = false;
  showDeleteModal = false;
  isEditMode = false;

  // Form data
  currentProduct: Partial<Product> = { id: '', name: '', price: 0, quantity: 0 };
  productToDelete: Product | null = null;

  // Toast
  toasts: { id: number; type: string; message: string }[] = [];
  toastId = 0;

  constructor(private consumerService: ConsumerService) { }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.consumerService.getProducts().subscribe({
      next: (data) => {
        this.products = data._embedded?.products || [];
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.showToast('error', 'Failed to load products');
        this.loading = false;
      }
    });
  }

  // Modal handlers
  openAddModal(): void {
    this.isEditMode = false;
    this.currentProduct = { id: '', name: '', price: 0, quantity: 0 };
    this.showModal = true;
  }

  openEditModal(product: Product): void {
    this.isEditMode = true;
    this.currentProduct = { ...product };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.currentProduct = { id: '', name: '', price: 0, quantity: 0 };
  }

  openDeleteModal(product: Product): void {
    this.productToDelete = product;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.productToDelete = null;
  }

  // Generate UUID for new products
  generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }

  // CRUD operations
  saveProduct(): void {
    if (!this.currentProduct.name || this.currentProduct.price === undefined || this.currentProduct.quantity === undefined) {
      this.showToast('error', 'Please fill in all fields');
      return;
    }

    if (this.isEditMode && this.currentProduct.id) {
      this.consumerService.updateProduct(this.currentProduct.id, this.currentProduct).subscribe({
        next: () => {
          this.showToast('success', 'Product updated successfully');
          this.closeModal();
          this.loadProducts();
        },
        error: (err) => {
          console.error(err);
          this.showToast('error', 'Failed to update product');
        }
      });
    } else {
      // Generate UUID for new product
      const newProduct = {
        ...this.currentProduct,
        id: this.generateUUID()
      };
      this.consumerService.createProduct(newProduct).subscribe({
        next: () => {
          this.showToast('success', 'Product created successfully');
          this.closeModal();
          this.loadProducts();
        },
        error: (err) => {
          console.error(err);
          this.showToast('error', 'Failed to create product');
        }
      });
    }
  }

  confirmDelete(): void {
    if (!this.productToDelete) return;

    this.consumerService.deleteProduct(this.productToDelete.id.toString()).subscribe({
      next: () => {
        this.showToast('success', 'Product deleted successfully');
        this.closeDeleteModal();
        this.loadProducts();
      },
      error: (err) => {
        console.error(err);
        this.showToast('error', 'Failed to delete product');
        this.closeDeleteModal();
      }
    });
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

  // Format price
  formatPrice(price: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(price);
  }
}
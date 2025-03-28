import { Component, OnInit, AfterViewInit } from '@angular/core';
import { RefundDetails, RefundStatus } from '../../Entiti/RefundDetails';
import { RefaundService } from './services/refaund.service';
import * as bootstrap from 'bootstrap';

@Component({
  selector: 'app-rembousement',
  templateUrl: './rembousement.component.html',
  styleUrls: ['./rembousement.component.css']
})
export class RembousementComponent implements OnInit, AfterViewInit {
  refundDetailsList: RefundDetails[] = [];
  selectedRefundDetails: RefundDetails = new RefundDetails();
  isEditMode: boolean = false;
  refundStatuses = Object.values(RefundStatus);

  constructor(private refaundService: RefaundService) { }

  ngOnInit(): void {
    this.getRefundDetailsList();
  }

  ngAfterViewInit(): void {
    this.initializeDropdowns();
  }

  initializeDropdowns(): void {
   /* const dropdowns = document.querySelectorAll('.dropdown-toggle');
    dropdowns.forEach((dropdown) => {
      if (!dropdown.classList.contains('dropdown-initialized')) {
        try {
          new bootstrap.Dropdown(dropdown);
          dropdown.classList.add('dropdown-initialized');
        } catch (error) {
          console.error('Error initializing dropdown:', error);
        }
      }
    });*/
  }

  getRefundDetailsList(): void {
    this.refaundService.getRefundDetailsList().subscribe(
      (data: RefundDetails[]) => {
        this.refundDetailsList = data;
        setTimeout(() => {
          this.initializeDropdowns();
        }, 0); 
      },
      (error) => {
        console.error('Error fetching refund details', error);
      }
    );
  }

  openCreateModal(): void {
    this.selectedRefundDetails = new RefundDetails();
    this.isEditMode = false;
    this.showModal();
  }

  openEditModal(refundDetails: RefundDetails): void {
    this.selectedRefundDetails = { ...refundDetails };
    this.isEditMode = true;
    this.showModal();
  }

  showModal(): void {
    const modalElement = document.getElementById('refundModal');
    if (modalElement) {
      const modal = new bootstrap.Modal(modalElement);
      modal.show();
    }
  }

  onSubmit(): void {
    if (this.isEditMode) {
      this.updateRefundDetails(this.selectedRefundDetails.refundId, this.selectedRefundDetails);
    } else {
      this.createRefundDetails(this.selectedRefundDetails);
    }
  }

  createRefundDetails(refundDetails: RefundDetails): void {
    this.refaundService.createRefundDetails(refundDetails).subscribe(
      (response) => {
        console.log('Refund details created successfully', response);
        this.getRefundDetailsList(); 
      },
      (error) => {
        console.error('Error creating refund details', error);
      }
    );
  }

  updateRefundDetails(id: number, refundDetails: RefundDetails): void {
    this.refaundService.updateRefundDetails(id, refundDetails).subscribe(
      (response) => {
        console.log('Refund details updated successfully', response);
        this.getRefundDetailsList(); 
      },
      (error) => {
        console.error('Error updating refund details', error);
      }
    );
  }

  deleteRefundDetails(id: number): void {
    this.refaundService.deleteRefundDetails(id).subscribe(
      () => {
        console.log('Refund details deleted successfully');
        this.getRefundDetailsList(); 
      },
      (error) => {
        console.error('Error deleting refund details', error);
      }
    );
  }
}
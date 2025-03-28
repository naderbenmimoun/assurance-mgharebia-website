import { Component } from '@angular/core';

@Component({
  selector: 'app-body-front',
  templateUrl: './body-front.component.html',
  styleUrls: ['./body-front.component.scss', ]
})
export class BodyFrontComponent  {
  isLoading = true;

  ngOnInit(): void {
    // Simulate a loading delay, e.g., for data fetching
    setTimeout(() => {
      this.isLoading = false;
    }, 2000);  // Adjust the time as needed
  }
}

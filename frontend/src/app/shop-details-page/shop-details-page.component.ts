import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';

class ShopDetails {
  id: string;
  name: string;
  description: string;
  email: string;
  street: string;
  zipcode: string;
  country: string;
  website: string;
  slots: Array<Slot>;
}

class Slot {
  id: string;
  start: string;
  end: string;
  available: boolean;
}

@Component({
  selector: 'shop-details-page',
  templateUrl: './shop-details-page.component.html',
  styleUrls: ['./shop-details-page.component.css']
})
export class ShopDetailsPageComponent implements OnInit {
  shopId: string;
  details: ShopDetails;


  constructor(private httpClient: HttpClient,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.paramMap
      .subscribe(value => this.shopId = value.get('id'));
    this.refresh();
  }

  refresh() {
    // Test data until the backend works.
    this.details = {
      id: 'moes',
      name: 'Moe\'s Whiskeyladen',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut ' +
        'labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea ' +
        'rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit ' +
        'amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam ' +
        'erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no ' +
        'sea takimata sanctus est Lorem ipsum dolor sit amet.',
      email: 'info@moesdrinks.com',
      street: 'Aschauer Straße 32',
      zipcode: '81549 München',
      country: 'Deutschland',
      website: 'https://www.moesdrinks.com',
      slots: [
        {
          start: '10:00',
          end: '11:00',
          available: false
        },
        {
          start: '11:00',
          end: '12:00',
          available: false
        },
        {
          start: '12:00',
          end: '13:00',
          available: false
        },
        {
          start: '13:00',
          end: '14:00',
          available: true
        },
        {
          start: '14:00',
          end: '15:00',
          available: true
        },
        {
          start: '15:00',
          end: '16:00',
          available: true
        }
      ]
    } as ShopDetails;
    this.httpClient.get('/api/shop/' + this.shopId)
      .subscribe((shopDetails: ShopDetails) => {
        this.details = shopDetails;
      });
  }

  showBookingPopup(id: string) {}
}

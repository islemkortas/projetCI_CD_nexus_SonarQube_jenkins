import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {

  message = '';

  constructor(private http: HttpClient) {}

  callBackend() {
    this.http.get('http://localhost:8087/hello', { responseType: 'text' })
      .subscribe(res => {
        this.message = res;
      });
  }
}

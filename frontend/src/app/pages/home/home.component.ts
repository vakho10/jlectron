import {ChangeDetectionStrategy, Component} from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeComponent {

  onClickMe() {
    alert("You clicked me! :)");
  }

  onExit() {
    // Send message to Java
    // @ts-ignore
    window.cefQuery({
      request: 'closeApp',
      onSuccess: function(response: any) { console.log(response); },
      onFailure: function(error_code: any, error_message: any) { console.error(error_message); }
    });
  }
}

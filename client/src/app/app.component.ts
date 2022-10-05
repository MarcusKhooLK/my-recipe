import { GoogleLoginProvider, SocialAuthService, SocialUser } from '@abacritt/angularx-social-login';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AccountService } from './services/account.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit, OnDestroy {

  isLoggedIn: boolean = false;
  user!:SocialUser | null

  onLoginSub$!: Subscription

  constructor(private socialAuthService: SocialAuthService, private accSvc:AccountService, private router: Router) {}

  ngOnInit() {
    this.socialAuthService.authState.subscribe((user) => {
      if(user) {
        this.user = user;
        this.isLoggedIn = user != null;
        this.accSvc.socialLogin(user.email, user.name, user.id)
        .then(result=> {
          localStorage.setItem('email', user.email);
          localStorage.setItem('username', user.name)
          this.router.navigate(['/profile'])
        })
        .catch(error=>{
          console.error(">>> error:", error)
          localStorage.removeItem('email')
          localStorage.removeItem('username')
        })
      }
    });
    this.isLoggedIn = localStorage.getItem('email') !== null
    this.onLoginSub$ = this.accSvc.onLogin.subscribe(resp => {
      this.isLoggedIn = true
      localStorage.setItem('email', resp.data.email)
      localStorage.setItem('username', resp.data.username)
    })
  }

  ngOnDestroy(): void {
    localStorage.clear()
    this.onLoginSub$.unsubscribe()
  }

  logOut(): void {
    if(this.user) {
      this.socialAuthService.signOut()
      this.user = null
    }
    localStorage.removeItem('email')
    localStorage.removeItem('username')
    this.isLoggedIn = false
    this.router.navigate(['/'])
  }
}
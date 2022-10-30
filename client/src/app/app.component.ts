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
  username!: string
  onLoginSub$!: Subscription
  onLogoutSub$!: Subscription

  form!: FormGroup

  constructor(private socialAuthService: SocialAuthService, private accSvc: AccountService, private router: Router) { }

  ngOnInit() {
    const sessionId = localStorage.getItem("sessionId") ?? ''
    if (sessionId) {
      this.accSvc.authSession(sessionId)
        .then(result => {
          this.accSvc.isLoggedIn = this.isLoggedIn = true
          this.accSvc.userLoggedIn = result.data
          this.username = result.data.username
        })
        .catch(error => {
        })
    }
    this.onLoginSub$ = this.accSvc.onLoginEvent.subscribe(user => {
      this.accSvc.userLoggedIn = user
      this.username = user.username
      this.isLoggedIn = this.accSvc.isLoggedIn = true
      localStorage.setItem("sessionId", user.sessionId)
    })
    this.onLogoutSub$ = this.accSvc.onLogoutEvent.subscribe(nothing => {
      this.logOut()
    })
  }

  ngOnDestroy(): void {
    localStorage.clear()
    this.onLoginSub$.unsubscribe()
    this.onLogoutSub$.unsubscribe()

    const sessionId = localStorage.getItem("sessionId") ?? ''
    if (sessionId) this.accSvc.destroySession(sessionId)
  }

  logOut(): void {
    localStorage.clear()
    this.accSvc.logout()
    this.socialAuthService.signOut()
      .then(result => {
        console.log("log out successfully!")
      })
      .catch(error => {
        console.log("log out unsuccssful. No social user")
      })
    this.username = ""
    this.isLoggedIn = false
    this.router.navigate(['/'])
  }
}
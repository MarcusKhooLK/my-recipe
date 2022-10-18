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
  username!:string

  onSocialLoginSub$!: Subscription

  form!: FormGroup

  constructor(private socialAuthService: SocialAuthService, private accSvc:AccountService, private router: Router, private fb: FormBuilder) {}

  ngOnInit() {
    this.isLoggedIn = localStorage.getItem('email') !== null
    this.username = localStorage.getItem('username') ?? ''
    this.onSocialLoginSub$ = this.accSvc.socialLoginEvent.subscribe(user=>{
      this.user = user
      this.isLoggedIn = true
      this.username = user.name
    })
    this.onSocialLoginSub$ = this.accSvc.onLoginEvent.subscribe(username=>{
      this.isLoggedIn = true
      this.username = username
    })
  }

  ngOnDestroy(): void {
    localStorage.clear()
    this.onSocialLoginSub$.unsubscribe()
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

  onSearch(){
    
  }
}
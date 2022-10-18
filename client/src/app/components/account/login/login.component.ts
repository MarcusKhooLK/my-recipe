import { FacebookLoginProvider, GoogleLoginProvider, SocialAuthService, SocialUser } from '@abacritt/angularx-social-login';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  
  form!: FormGroup
  isLoggedIn: boolean = false
  errorMsg: string = ""
  user!: SocialUser

  isLoading: boolean = false

  constructor(private fb: FormBuilder, private accSvc:AccountService, private router: Router, private socialAuthService: SocialAuthService) { }

  ngOnInit(): void {
    this.isLoggedIn = localStorage.getItem('email') !== null
    if(this.isLoggedIn) {
      this.router.navigate(['/profile'])
      return
    }

    this.form = this.createForm()

    this.socialAuthService.authState.subscribe((user) => {
      if(user) {
        this.user = user;
        this.isLoading = true
        this.accSvc.socialLogin(user.email, user.name, user.id)
        .then(result=> {
          this.isLoading = false
          this.isLoggedIn = true
          localStorage.setItem('email', user.email);
          localStorage.setItem('username', user.name)
          this.router.navigate(['/profile'])
          this.accSvc.socialLoginEvent.next(user)
        })
        .catch(error=>{
          this.socialAuthService.signOut()
          this.isLoading = false
          this.isLoggedIn = false
          this.errorMsg = "Account already exists"
        })
      }
    });
  }

  private createForm() {
    return this.fb.group({
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  onSubmitForm() {
    const email = this.form.get('email')?.value
    const pwd = this.form.get('password')?.value
    this.isLoading = true
    this.accSvc.auth(email, pwd)
    .then(result=>{
      this.accSvc.onLoginEvent.next(result.data.username)
      this.isLoading = false
      this.isLoggedIn = true
      localStorage.setItem('email', result.data.email)
      localStorage.setItem('username', result.data.username)
      this.router.navigate(['/profile'])
    })
    .catch(error=>{
      this.isLoading = false
      this.errorMsg = "Invalid credentials"
    })
  }

  signInWithFB() {
    this.socialAuthService.signIn(FacebookLoginProvider.PROVIDER_ID);
  }

}

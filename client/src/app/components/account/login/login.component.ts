import { GoogleLoginProvider, SocialAuthService, SocialUser } from '@abacritt/angularx-social-login';
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
  isLoggedIn: boolean = false;
  errorMsg: string = ""

  constructor(private fb: FormBuilder, private accSvc:AccountService, private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn = localStorage.getItem('email') !== null
    if(this.isLoggedIn) {
      this.router.navigate(['/profile'])
      return
    }

    this.form = this.createForm()
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
    this.accSvc.auth(email, pwd)
    .then(result=>{
      console.info(">>> result: ", result)
      this.accSvc.onLogin.next(result)
      this.router.navigate(['/profile'])
    })
    .catch(error=>{
      console.error(">>> error: ", error)
      this.errorMsg = "Invalid credentials"
    })
  }

}

import { GoogleLoginProvider, SocialAuthService, SocialUser } from '@abacritt/angularx-social-login';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateComponent implements OnInit {

  form!: FormGroup
  errorMsg: string = ""
  isLoggedIn: boolean = false;

  constructor(private fb: FormBuilder, private accSvc: AccountService, private router: Router) { }

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
      username: this.fb.control<string>('', [Validators.required]),
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  onSubmitForm() {
    const email = this.form.get('email')?.value
    const pwd = this.form.get('password')?.value
    const username = this.form.get('username')?.value
    this.accSvc.createAccount(username, email, pwd)
    .then(result=>{
      this.router.navigate(['/'])
    })
    .catch(error=>{
      console.error(">>> error: ", error)
      this.errorMsg = error.error.message
    })
  }
}

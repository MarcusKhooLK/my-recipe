import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Response } from 'src/app/models/response';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  isLoading: boolean = true
  isLoggedIn: boolean = false

  constructor(private router:Router, private accSvc: AccountService) { }

  ngOnInit(): void {
    const sessionId = localStorage.getItem("sessionId") ?? ''
    this.accSvc.authSession(sessionId)
    .then(result=>{
      this.accSvc.isLoggedIn = this.isLoggedIn = true
      this.accSvc.userLoggedIn = result.data
      this.isLoading = false;
    })
    .catch(error=>{
      this.router.navigate(['/'])
      console.error("error >>>> ", error)
    })
  }

  onDeleteAccount() {
    if(confirm("Are you sure? This action is irreversible!")) {
      const email = this.accSvc.userLoggedIn?.email
      if(!email) return
      this.accSvc.deleteAccount(email)
      .then(result=>{
        alert(result.message)
        this.isLoggedIn = false
        this.accSvc.onLogoutEvent.next()
      })
      .catch(error=>{
        console.error("error >>> ", error)
      })
    }
  }

}

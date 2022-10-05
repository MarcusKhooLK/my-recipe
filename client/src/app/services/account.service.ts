import { HttpClient } from "@angular/common/http";
import { Injectable, OnDestroy, OnInit } from "@angular/core";
import { firstValueFrom, Subject } from "rxjs";
import { Response } from 'src/app/models/response'

@Injectable()
export class AccountService implements OnInit, OnDestroy{

    onLogin = new Subject<Response>()

    constructor(private httpClient: HttpClient) {}
    ngOnInit(): void {
        console.info(" AccountService onInit")
    }
    ngOnDestroy(): void {
        console.info("AccountService onDestroy")
    }

    createAccount(username: string, email:string, password:string) : Promise<Response> {
        return firstValueFrom(
            this.httpClient.post<Response>(
                '/api/account', {username, email, password}
            )
        )
    }

    auth(email: string, password:string) : Promise<Response> {
        return firstValueFrom(
            this.httpClient.post<Response>(
                '/api/account/auth', {email, password}
            )
        ) 
    }

    socialLogin(email: string, username: string, token: string) {
        return firstValueFrom(
            this.httpClient.post<Response>(
                '/api/account/authsocial', {email, username, token}
            )
        )
    }
}
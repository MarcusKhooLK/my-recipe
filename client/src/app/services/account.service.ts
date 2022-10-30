import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable, OnDestroy, OnInit } from "@angular/core";
import { firstValueFrom, Subject } from "rxjs";
import { Response } from 'src/app/models/response'
import { User } from "../models/myuser";

@Injectable()
export class AccountService {

    userLoggedIn!:User | null

    onLoginEvent = new Subject<User>()
    onLogoutEvent = new Subject<void>()

    isLoggedIn : boolean = false;

    constructor(private httpClient: HttpClient) {}

    logout() {
        this.userLoggedIn = null
        this.isLoggedIn = false
        const sessionId = localStorage.getItem("sessionId") ?? ''
        if(sessionId) this.destroySession(sessionId)
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

    authSession(sessionId:string) : Promise<Response> {
        return firstValueFrom(
            this.httpClient.post<Response>(
                '/api/account/authsession', {sessionId}
            )
        ) 
    }

    destroySession(sessionId: string) : Promise<Response> {
        return firstValueFrom(
            this.httpClient.delete<Response>(
                '/api/account/authsession', {body: sessionId}
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

    deleteAccount(email: string) {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.delete<Response>('/api/account', { headers, body: email })
        )
    }
}
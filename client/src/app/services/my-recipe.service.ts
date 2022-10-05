import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { Recipe } from "../models/recipe";
import { RecipeSummary } from "../models/recipesummary";
import { Response } from "../models/response";

@Injectable()
export class MyRecipeService {

    constructor(private httpClient: HttpClient) { }

    getRecipesByName(name: string): Promise<RecipeSummary[]> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<RecipeSummary[]>(`/api/myrecipe/recipes/${name}`, { headers })
        )
    }

    getRecipesByCategory(cat: string): Promise<RecipeSummary[]> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<RecipeSummary[]>(`/api/myrecipe/recipes/category/${cat}`, { headers })
        )
    }

    getRecipesByArea(area: string): Promise<RecipeSummary[]> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<RecipeSummary[]>(`/api/myrecipe/recipes/area/${area}`, { headers })
        )
    }

    createRecipe(formData: FormData): Promise<Response> {
        return firstValueFrom(
            this.httpClient.post<Response>('/api/myrecipe', formData)
        )
    }

    getRecipe(recipeId: string): Promise<Recipe> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<Recipe>(`/api/myrecipe/recipe/${recipeId}`, { headers })
        )
    }

    getRecipeSummaryByEmail(email: string): Promise<RecipeSummary[]> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        const params = new HttpParams().set('email', email)
        return firstValueFrom(
            this.httpClient.get<RecipeSummary[]>(`/api/myrecipe/recipes`, { headers, params })
        )
    }
}
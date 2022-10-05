import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable, OnInit } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { Recipe } from "../models/recipe";
import { RecipeSummary } from "../models/recipesummary";

@Injectable()
export class MealDbService {

    constructor(private httpClient: HttpClient) { }

    getRecipes(query: string): Promise<RecipeSummary[]> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        const params = new HttpParams()
            .set('s', query)
        return firstValueFrom(
            this.httpClient.get<RecipeSummary[]>('/api/mealdb/recipes', { headers, params })
        )
    }

    getRecipesByArea(area: string): Promise<RecipeSummary[]> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<RecipeSummary[]>(`/api/mealdb/recipes/area/${area}`, {headers})
        )
    }

    getRecipesByCategory(cat: string): Promise<RecipeSummary[]> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<RecipeSummary[]>(`/api/mealdb/recipes/category/${cat}`, {headers})
        )
    }

    getRecipe(recipeId: string): Promise<Recipe> {
        const headers = new HttpHeaders()
            .set('Accept', 'application/json')
            .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<Recipe>(`/api/mealdb/recipe/${recipeId}`, { headers})
        )
    }

    getAllAreas(): Promise<string[]> {
        const headers = new HttpHeaders()
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<string[]>('/api/mealdb/areas', { headers})
        )
    }
    
    getAllCategories(): Promise<string[]> {
        const headers = new HttpHeaders()
        .set('Accept', 'application/json')
        .set('Content-Type', 'application/json')
        return firstValueFrom(
            this.httpClient.get<string[]>('/api/mealdb/categories', { headers})
        )
    }
}
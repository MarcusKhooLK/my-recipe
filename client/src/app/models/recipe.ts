import { RecipeSummary } from "./recipesummary";

export interface Recipe extends RecipeSummary{
    category: string;
    country: string;
    instructions: string;
    youtubeLink: string;
    createdBy: string;
    ingredients: string[]
    measurements: string[]
}
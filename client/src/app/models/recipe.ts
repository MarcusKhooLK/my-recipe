export interface Recipe{
    recipeId: string;
    name: string;
    category: string;
    country: string;
    instructions: string;
    thumbnail: string;
    youtubeLink: string;
    createdBy: string;
    ingredients: string[]
    measurements: string[]
}
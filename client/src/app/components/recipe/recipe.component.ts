import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Recipe } from 'src/app/models/recipe';
import { AccountService } from 'src/app/services/account.service';
import { MealDbService } from 'src/app/services/mealdb.service';
import { MyRecipeService } from 'src/app/services/my-recipe.service';

@Component({
  selector: 'app-recipe',
  templateUrl: './recipe.component.html',
  styleUrls: ['./recipe.component.css']
})
export class RecipeComponent implements OnInit {

  recipe!: Recipe
  isLoading: boolean = true;

  constructor(private activatedRoute: ActivatedRoute, private mealDbSvc: MealDbService, private myRecipeSvc: MyRecipeService, private router: Router, private accSvc:AccountService) { }

  ngOnInit(): void {
    const recipeId: string = this.activatedRoute.snapshot.params['recipeId']
    const username: string = this.activatedRoute.snapshot.params['user']
    if (username) {
      this.myRecipeSvc.getRecipe(recipeId)
      .then(result => {
        this.recipe = result
        this.recipe.user = true
        this.isLoading = false;
      })
      .catch(error => {
        this.router.navigate(['/notFound'])
      })
    } else {
      this.mealDbSvc.getRecipe(recipeId)
        .then(result => {
          this.recipe = result
          this.isLoading = false;
        })
        .catch(error => {
          this.isLoading = false;
          this.router.navigate(['/notFound'])
        })
    }
  }

}

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RecipeSummary } from 'src/app/models/recipesummary';
import { MealDbService } from 'src/app/services/mealdb.service';
import { MyRecipeService } from 'src/app/services/my-recipe.service';

@Component({
  selector: 'app-list-recipes',
  templateUrl: './list-recipes.component.html',
  styleUrls: ['./list-recipes.component.css']
})
export class ListRecipesComponent implements OnInit {

  recipes: RecipeSummary[] = []
  query!: string
  isLoading: boolean = true;

  constructor(private mealDbSvc: MealDbService, private router: Router, private activatedRoute: ActivatedRoute, private myRecipeSvc: MyRecipeService) { }

  ngOnInit(): void {
    // query from links
    const cat = this.activatedRoute.snapshot.params['cat']
    const area = this.activatedRoute.snapshot.params['area']
    console.info("cat ", cat)
    console.info("area ", area)
    if(cat) {
      this.mealDbSvc.getRecipesByCategory(cat)
      .then(result=>{
        console.info(cat)
        this.recipes.push(...result)
      })
      .then(result=>{
        this.myRecipeSvc.getRecipesByCategory(cat)
        .then(result=>{
          result.forEach(v=>{
            v.user = true
          })
          this.recipes.push(...result)
          this.isLoading = false;
        })
        .catch(error=>{
          console.error(">>> error: ", error)
          this.isLoading = false;
        })
      })
      .catch(error=>{
        console.error(">>> error: ", error)
        this.isLoading = false;
      })

      return;

    } else if(area) {
      this.mealDbSvc.getRecipesByArea(area)
      .then(result=>{
        this.recipes.push(...result)
      })
      .then(result=>{
        this.myRecipeSvc.getRecipesByArea(area)
        .then(result=>{
          result.forEach(v=>{
            v.user = true
          })
          this.recipes.push(...result)
          this.isLoading = false;
        })
        .catch(error=>{
          console.error(">>> error: ", error)
          this.isLoading = false;
        })
      })
      .catch(error=>{
        console.error(">>> error: ", error)
        this.isLoading = false;
      })

      return;

    }

    // query from search
    this.query = localStorage.getItem('query') ?? ''
    if (this.query) {
      this.mealDbSvc.getRecipes(this.query)
        .then(result => {
          console.log(result)
          this.recipes = result
        })
        .then(result => {
          this.myRecipeSvc.getRecipesByName(this.query)
          .then(result => {
            result.forEach(v=>{
              v.user = true
            })
            this.recipes.push(...result)
            this.isLoading = false;
          })
          .catch(error=>{
            console.error(">>> error: ", error)
            this.isLoading = false;
          })
        })
        .catch(error => {
          console.error(">>> error: ", error)
          this.isLoading = false;
        })
    } else {
      this.router.navigate(['/search'])
    }
    localStorage.removeItem('query')
  }

}

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RecipeSummary } from 'src/app/models/recipesummary';
import { MyRecipeService } from 'src/app/services/my-recipe.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  isLoggedIn: boolean = false;
  recipes: RecipeSummary[] = []

  constructor(private router: Router, private myRecipeSvc: MyRecipeService) { }

  ngOnInit(): void {
    this.isLoggedIn = localStorage.getItem('email') !== null
    if(this.isLoggedIn === false) {
      this.router.navigate(['/'])
      return
    }

    this.myRecipeSvc.getRecipeSummaryByEmail(localStorage.getItem('email') ?? '')
    .then(result=>{
      this.recipes = result
    })
    .catch(error=>{
      this.router.navigate(['/notFound'])
    })
  }

}

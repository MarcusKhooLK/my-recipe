import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Recipe } from 'src/app/models/recipe';
import { RecipeSummary } from 'src/app/models/recipesummary';
import { AccountService } from 'src/app/services/account.service';
import { MealDbService } from 'src/app/services/mealdb.service';
import { MyRecipeService } from 'src/app/services/my-recipe.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  isLoggedIn: boolean = false;
  recipes: RecipeSummary[] = []
  isEditing: boolean = false;
  form!: FormGroup
  ingredientsArray!: FormArray
  areas: string[] = ['American']
  categories: string[] = ['Beef']
  username!: string
  oldThumbnail!:string
  isLoading: boolean = true;

  @ViewChild('file')
  thumbnailImage!: ElementRef

  constructor(private router: Router, private myRecipeSvc: MyRecipeService, private fb: FormBuilder, private mealDbSvc: MealDbService, private accSvc:AccountService) { }

  ngOnInit(): void {
    
    const sessionId = localStorage.getItem("sessionId") ?? ''
    this.accSvc.authSession(sessionId)
    .then(result=>{
      this.accSvc.isLoggedIn = this.isLoggedIn = true
      this.accSvc.userLoggedIn = result.data
      this.isLoading = false;

      this.username = this.accSvc.userLoggedIn?.username ?? ''
      this.mealDbSvc.getAllAreas()
      .then(result=>{
        this.areas = result;
      })
      .then(result=>{
        this.mealDbSvc.getAllCategories()
        .then(result=>{
          this.categories = result;
        })
        .catch(error=>{
          console.error(">>> error: ", error)
        })
      })
      .catch(error=>{
        console.error(">>> error: ", error)
      })
  
      this.updateRecipes()
    })
    .catch(error=>{
      this.router.navigate(['/'])
      console.error("error >>>> ", error)
    })

    this.form = this.createForm()
  }

  onEdit(recipeId: string) {
    this.isEditing = true
    this.isLoading = true
    this.myRecipeSvc.getRecipe(recipeId)
    .then(result=>{
      console.info(">>> result: ", result)
      this.form = this.createForm(result)
      this.oldThumbnail = result.thumbnail
      this.isLoading = false
    })
    .catch(error=>{
      console.error(">>> error: ", error)
      this.isLoading = false
    })
  }

  private createForm(r?: Recipe) : FormGroup {
    this.createIngredientItems(r?.ingredients, r?.measurements)
    return this.fb.group({
      recipeId: this.fb.control<string>(r?.recipeId || ''),
      name: this.fb.control<string>(r?.name || '', [Validators.required]),
      thumbnail: this.fb.control<string>(''),
      category: this.fb.control<string>(r?.category || '', [Validators.required]),
      area: this.fb.control<string>(r?.country || '', [Validators.required]),
      instructions: this.fb.control<string>(r?.instructions || '', [Validators.required]),
      youtubeLink: this.fb.control<string>(r?.youtubeLink || ''),
      ingredients: this.ingredientsArray
    })
  }

  private createIngredientItems(ingredients: string[] = [], measurements: string[] = []) {
    this.ingredientsArray = this.fb.array([])
    for(let i = 0; i < ingredients.length; i++) {
      this.ingredientsArray.push(this.createIngredientItem(ingredients[i], measurements[i]))
    }
  }

  private createIngredientItem(i: string, m: string) {
    return this.fb.group({
      measurement: this.fb.control<string>(m, [Validators.required]),
      ingredient: this.fb.control<string>(i, [Validators.required])
    })
  }

  onAddIngredient() {
    this.ingredientsArray.push(this.createIngredientItem('',''))
  }

  onRemoveIngredient(idx: number) {
    this.ingredientsArray.removeAt(idx)
  }

  onDelete(recipeId: string) {
    if(confirm('Are you sure?')) {
      console.info(">>> onDelete: ", recipeId)
      this.isLoading = true
      this.myRecipeSvc.deleteRecipeByRecipeId(recipeId, this.accSvc.userLoggedIn?.email ?? '')
      .then(result=>{
        console.info(">>> result ", result)
        this.updateRecipes()
      })
      .catch(error=>{
        console.error(">>> error ", error)
        this.isLoading = false
      })
    }
  }

  cancelEdit() {
    this.form.reset()
    this.isEditing = false
  }

  onSubmitEdit() {
    const formData = new FormData()
    const ingredientsArray: string[] = []
    const measurementsArray: string[] = []
    for(let i = 0; i < this.form.get('ingredients')?.value.length; i++) {
      ingredientsArray.push(this.form.get('ingredients')?.value[i].ingredient)
      measurementsArray.push(this.form.get('ingredients')?.value[i].measurement)
    }

    formData.set('recipeId', this.form.get('recipeId')?.value)
    formData.set('name', this.form.get('name')?.value)
    formData.set('thumbnail', this.thumbnailImage.nativeElement.files[0])
    formData.set('category', this.form.get('category')?.value)
    formData.set('area', this.form.get('area')?.value)
    formData.set('instructions', this.form.get('instructions')?.value)
    formData.set('youtubeLink', this.form.get('youtubeLink')?.value)
    formData.set('ingredients', ingredientsArray.join(','))
    formData.set('measurements', measurementsArray.join(','))
    formData.set('email', this.accSvc.userLoggedIn?.email ?? '')

    this.isLoading = true;

    this.myRecipeSvc.editRecipe(formData)
    .then(result=>{
      console.log(">>> result: ", result)
      this.form.reset()
      this.updateRecipes()
      this.isEditing = false;
    })
    .catch(error=>{
      console.log(">>> error: ", error)
      this.isLoading = false;
    })
  }

  private updateRecipes() {
    this.myRecipeSvc.getRecipeSummaryByEmail(this.accSvc.userLoggedIn?.email ?? '')
    .then(result=>{
      this.recipes = result
      this.recipes.forEach(v=>{
        v.user = true
      })
      this.isLoading = false;
    })
    .catch(error=>{
      this.router.navigate(['/notFound'])
      this.isLoading = false;
    })
  }
}

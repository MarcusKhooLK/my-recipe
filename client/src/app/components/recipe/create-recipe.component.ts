import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MealDbService } from 'src/app/services/mealdb.service';
import { MyRecipeService } from 'src/app/services/my-recipe.service';

@Component({
  selector: 'app-create-recipe',
  templateUrl: './create-recipe.component.html',
  styleUrls: ['./create-recipe.component.css']
})
export class CreateRecipeComponent implements OnInit {

  form!: FormGroup
  ingredientsArray!: FormArray
  areas: string[] = ['American']
  categories: string[] = ['Beef']
  isLoggedIn: boolean = false

  selectedArea: string = 'American'
  selectedCat: string = 'Beef'

  @ViewChild('file')
  thumbnailImage!: ElementRef

  isLoading: boolean = true

  constructor(private fb: FormBuilder, private mealDbSvc: MealDbService, private myRecipeSvc: MyRecipeService, private router: Router) { }

  ngOnInit(): void {
    this.isLoggedIn = localStorage.getItem('email') !== null
    if(this.isLoggedIn === false) {
      this.router.navigate(['/'])
      return
    }

    this.mealDbSvc.getAllAreas()
    .then(result=>{
      this.areas = result;
    })
    .catch(error=>{
      console.error(">>> error: ", error)
    })

    this.mealDbSvc.getAllCategories()
    .then(result=>{
      this.categories = result;
      this.isLoading = false;
    })
    .catch(error=>{
      console.error(">>> error: ", error)
      this.isLoading = false;
    })

    this.form = this.createForm()
  }

  createForm(): FormGroup {
    this.ingredientsArray = this.fb.array([])
    this.onAddIngredient()
    return this.fb.group({
      name: this.fb.control<string>('', [Validators.required]),
      thumbnail: this.fb.control<string>('', [Validators.required]),
      category: this.fb.control<string>('Beef', [Validators.required]),
      area: this.fb.control<string>('American', [Validators.required]),
      instructions: this.fb.control<string>('', [Validators.required]),
      youtubeLink: this.fb.control<string>(''),
      ingredients: this.ingredientsArray
    })
  }

  onAddIngredient() {
    const item = this.fb.group({
      measurement: this.fb.control<string>('', [Validators.required]),
      ingredient: this.fb.control<string>('', [Validators.required])
    })
    this.ingredientsArray.push(item)
  }

  onRemoveIngredient(idx: number) {
    this.ingredientsArray.removeAt(idx)
  }

  onSubmitForm() {
    const formData = new FormData()
    const ingredientsArray: string[] = []
    const measurementsArray: string[] = []
    for(let i = 0; i < this.form.get('ingredients')?.value.length; i++) {
      ingredientsArray.push(this.form.get('ingredients')?.value[i].ingredient)
      measurementsArray.push(this.form.get('ingredients')?.value[i].measurement)
    }
    formData.set('name', this.form.get('name')?.value)
    formData.set('thumbnail', this.thumbnailImage.nativeElement.files[0])
    formData.set('category', this.form.get('category')?.value)
    formData.set('area', this.form.get('area')?.value)
    formData.set('instructions', this.form.get('instructions')?.value)
    formData.set('youtubeLink', this.form.get('youtubeLink')?.value)
    formData.set('ingredients', ingredientsArray.join(','))
    formData.set('measurements', measurementsArray.join(','))
    formData.set('email', localStorage.getItem('email') ?? '')

    this.isLoading = true;

    this.myRecipeSvc.createRecipe(formData)
    .then(result=>{
      console.log(">>> result: ", result)
      this.form.reset()
      this.isLoading = false;
      this.router.navigate(['/recipe', "user", result.data.recipeId])
    })
    .catch(error=>{
      console.log(">>> error: ", error)
      this.isLoading = false;
    })
  }
}
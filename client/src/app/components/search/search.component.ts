import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MealDbService } from 'src/app/services/mealdb.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  form!: FormGroup
  areas: string[] = []
  categories: string[] = []
  
  constructor(private fb: FormBuilder, private router:Router, private mealDbSvc: MealDbService) { }

  ngOnInit(): void {
    this.form = this.createForm()

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
    })
    .catch(error=>{
      console.error(">>> error: ", error)
    })
  }

  createForm() {
    return this.fb.group({
      search: this.fb.control<string>('', [Validators.required])
    })
  }

  onSearch() {
    console.info(">>> onSearch: ", this.form.value)
    const query = this.form.get('search')?.value
    if(!!query) {
      localStorage.setItem('query', query)
      this.router.navigate(['/recipes'])
    }
  }
}
